package com.hotsharp.api.client;

import com.hotsharp.api.dto.UserDTO;
import com.hotsharp.common.domain.User;
import com.hotsharp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("user-service")
public interface UserClient {

    @GetMapping("/user/info/get-one")
    Result<UserDTO> getUserById(@RequestParam("uid") Integer uid);

    @PostMapping("/user/info/get-list")
    List<UserDTO> getUserByIdList(@RequestBody List<Integer> list);

    @PostMapping("/user/info/get")
    List<User> getUserList(@RequestBody User user);

}
