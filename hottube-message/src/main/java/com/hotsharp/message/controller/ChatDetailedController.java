package com.hotsharp.message.controller;

import com.hotsharp.common.result.Result;
import com.hotsharp.common.utils.UserContext;
import com.hotsharp.message.service.ChatDetailedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "聊天记录接口")
@RestController
public class ChatDetailedController {
    @Autowired
    private ChatDetailedService chatDetailedService;


    /**
     * 获取更多历史消息记录
     * @param uid   聊天对象的UID
     * @param offset    偏移量，即已经获取过的消息数量，从哪条开始获取更多
     * @return  CustomResponse对象，包含更多消息记录的map
     */
    @Operation(summary = "获取更多历史消息记录")
    @GetMapping("/msg/chat-detailed/get-more")
    public Result getMoreChatDetails(@RequestParam("uid") Integer uid,
                                     @RequestParam("offset") Long offset) {
        Integer loginUid = UserContext.getUserId();
        Result customResponse = new Result();
        customResponse.setData(chatDetailedService.getDetails(uid, loginUid, offset));
        return customResponse;
    }

    /**
     * 删除消息
     * @param id    消息ID
     * @return  CustomResponse对象
     */
    @Operation(summary = "删除消息")
    @PostMapping("/msg/chat-detailed/delete")
    public Result delDetail(@RequestParam("id") Integer id) {
        Integer loginUid = UserContext.getUserId();
        Result customResponse = new Result();
        if (!chatDetailedService.deleteDetail(id, loginUid)) {
            customResponse.setCode(500);
            customResponse.setMessage("删除消息失败");
        }
        return customResponse;
    }
}
