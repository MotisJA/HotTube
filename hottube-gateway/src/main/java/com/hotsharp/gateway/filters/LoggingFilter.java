package com.hotsharp.gateway.filters;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        // 打印请求头
        System.out.println("Request Headers Before Forwarding:");
        exchange.getRequest().getHeaders().forEach((name, values) -> {
            values.forEach(value -> System.out.println(name + ": " + value));
        });

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // 打印响应头
            System.out.println("Response Headers After Forwarding:");
            exchange.getResponse().getHeaders().forEach((name, values) -> {
                values.forEach(value -> System.out.println(name + ": " + value));
            });
        }));
    }

    @Override
    public int getOrder() {
        return -1; // 设置过滤器的优先级，值越小优先级越高
    }
}