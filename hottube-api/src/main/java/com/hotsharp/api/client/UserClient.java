package com.hotsharp.api.client;


import com.hotsharp.api.dto.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("user-service")
public interface UserClient {

    @GetMapping("/{userId}")
    User getUserInfo(@PathVariable Long userId);

}
