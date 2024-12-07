package com.hotsharp.user.service;

import com.hotsharp.api.dto.UserDTO;

public interface IUserService {

    /**
     * 根据uid查询用户信息
     * @param id 用户ID
     * @return 用户可见信息实体类 UserDTO
     */
    UserDTO getUserById(Integer id);
}
