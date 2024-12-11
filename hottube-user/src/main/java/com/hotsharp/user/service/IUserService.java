package com.hotsharp.user.service;

import com.hotsharp.api.dto.UserDTO;
import com.hotsharp.common.result.Result;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IUserService {

    /**
     * 根据uid查询用户信息
     * @param id 用户ID
     * @return 用户可见信息实体类 UserDTO
     */
    UserDTO getUserById(Integer id);

    /**
     * 根据有序uid列表查询用户信息
     * @param list 用户id列表
     * @return  用户信息列表
     */
    List<UserDTO> getUserByIdList(List<Integer> list);

    /**
     * 更新用户个人信息
     * @param uid   用户uid
     * @param nickname  昵称
     * @param desc  个性签名
     * @param gender    性别： 0 女 1 男 2 双性人
     * @return  响应对象
     */
    Result updateUserInfo(Integer uid, String nickname, String desc, Integer gender) throws IOException;

    /**
     * 更新用户头像
     * @param uid
     * @param file
     * @return
     */
    Result updateUserAvatar(Integer uid, MultipartFile file) throws IOException;
}
