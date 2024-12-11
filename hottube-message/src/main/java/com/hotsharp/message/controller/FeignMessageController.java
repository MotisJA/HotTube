package com.hotsharp.message.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotsharp.common.domain.ChatDetailed;
import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.message.im.IMServer;
import com.hotsharp.message.mapper.ChatDetailedMapper;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
public class FeignMessageController {

    @Autowired
    private ChatDetailedMapper chatDetailedMapper;

    @GetMapping("/chat-detailed/mapper/get")
    public Result<List<ChatDetailed>> selectChatDetailed(ChatDetailed chatDetailed) {
        QueryWrapper<ChatDetailed> queryWrapper = new QueryWrapper<>(chatDetailed);
        return Results.success(chatDetailedMapper.selectList(queryWrapper));
    }

    @PostMapping("/message/disconnectUser/{userId}")
    public void disconnectUser(@PathVariable("userId") Integer userId) {
        Set<Channel> userChannels = IMServer.userChannel.get(userId);
        if (userChannels != null) {
            for (Channel channel : userChannels) {
                try {
                    channel.close().sync(); // 等待通道关闭完成
                } catch (InterruptedException e) {
                    // 处理异常，如果有必要的话
                    e.printStackTrace();
                }
            }
            IMServer.userChannel.remove(userId);
        }
    }
}
