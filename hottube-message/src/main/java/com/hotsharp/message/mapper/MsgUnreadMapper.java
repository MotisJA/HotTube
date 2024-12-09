package com.hotsharp.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotsharp.message.domain.po.MsgUnread;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MsgUnreadMapper extends BaseMapper<MsgUnread> {
}
