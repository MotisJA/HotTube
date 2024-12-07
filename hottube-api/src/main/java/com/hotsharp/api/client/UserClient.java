package com.hotsharp.api.client;

import com.hotsharp.api.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("user-service")
public interface UserClient {

    @GetMapping("/{userId}")
    UserDTO getUser(@PathVariable Integer userId);

}
