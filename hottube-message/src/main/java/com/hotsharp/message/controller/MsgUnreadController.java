package com.hotsharp.message.controller;

import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.common.utils.UserContext;
import com.hotsharp.message.service.MsgUnreadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "消息未读接口")
@RestController
@RequestMapping("/msg-unread")
public class MsgUnreadController {
    @Autowired
    private MsgUnreadService msgUnreadService;

    /**
     * 获取当前用户全部消息未读数
     * @return
     */
    @Operation(summary = "获取当前用户全部消息未读数")
    @GetMapping("/all")
    public Result getMsgUnread() {
        Integer uid = UserContext.getUserId();
        return Results.success(msgUnreadService.getUnread(uid));
    }

    /**
     * 清除某一列的未读消息提示
     * @param column    msg_unread表列名 "reply"/"at"/"love"/"system"/"whisper"/"dynamic"
     */
    @Operation(summary = "清除某一列的未读消息提示")
    @PostMapping("/clear")
    public void clearUnread(@RequestParam("column") String column) {
        Integer uid = UserContext.getUserId();
        msgUnreadService.clearUnread(uid, column);
    }
}
