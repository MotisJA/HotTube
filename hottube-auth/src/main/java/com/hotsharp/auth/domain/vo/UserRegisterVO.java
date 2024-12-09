package com.hotsharp.auth.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserRegisterVO {
    private Long userId;
    private String username;
    private String phoneNumber;
    private String token;
}
