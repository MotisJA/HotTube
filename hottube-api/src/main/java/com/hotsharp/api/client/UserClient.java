package com.hotsharp.api.client;

import com.hotsharp.api.dto.UserDTO;
import com.hotsharp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("user-service")
@RequestMapping("/user")
public interface UserClient {

    @GetMapping("/info/get-one")
    Result<UserDTO> getOneUserInfo(@PathVariable Integer userId);



}
