package com.hotsharp.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotsharp.message.domain.po.Chat;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMapper extends BaseMapper<Chat> {
}
