package com.hotsharp.api.client;

import com.hotsharp.api.dto.UserDTO;
import com.hotsharp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserClient {

    @GetMapping("/user/info/get-one")
    Result<UserDTO> getUserById(@RequestParam("uid") Integer uid);

}
