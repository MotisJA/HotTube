package com.hotsharp.api.client;

import com.hotsharp.common.domain.ChatDetailed;
import com.hotsharp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient("message-service")
public interface MessageClient {

    @GetMapping("/chat-detailed/mapper/get")
    Result<List<ChatDetailed>> selectChatDetailed(ChatDetailed chatDetailed);

    @PostMapping("/message/disconnectUser/{userId}")
    void disconnectUser(@PathVariable("userId") Integer userId);
}
