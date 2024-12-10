package com.hotsharp.gateway.filters;

import com.hotsharp.common.utils.JwtUtil;
import com.hotsharp.common.utils.RedisUtil;
import com.hotsharp.gateway.config.AuthProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获取request
        ServerHttpRequest request = exchange.getRequest();
        // 2.判断是否需要做登录拦截
        if (isExclude(request.getPath().toString())) {
            // 放行
            return chain.filter(exchange);
        }
        // 3.获取token
        String token = null;
        List<String> headers = request.getHeaders().get("Authorization");
        if (headers != null && !headers.isEmpty()) {
            String authHeader = headers.get(0);
            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7); // 提取实际的token
            }
        }

        // 4.校验并解析token
        if (!StringUtils.hasText(token)) {
            return unauthorizedResponse(exchange);
        }

        String userId;
        String role;
        try {
            if (!jwtUtil.verifyToken(token)) {
                return unauthorizedResponse(exchange);
            }
            userId = jwtUtil.getSubjectFromToken(token);
            role = jwtUtil.getClaimFromToken(token, "role");
        } catch (Exception e) {
            return unauthorizedResponse(exchange);
        }

//        // 5.从Redis中获取用户信息
//        User user = redisUtil.getObject("security:" + role + ":" + userId, User.class);
//        if (user == null) {
//            return unauthorizedResponse(exchange);
//        }

        // 6.传递用户信息
        ServerWebExchange swe = exchange.mutate()
                .request(builder -> builder.header("uid", userId))
                .build();

        // 7.放行
        return chain.filter(swe);
    }

    private boolean isExclude(String path) {
        for (String pathPattern : authProperties.getExcludePaths()) {
            if (antPathMatcher.match(pathPattern, path)) {
                return true;
            }
        }
        return false;
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
