package com.hotsharp.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotsharp.common.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
