package com.hotsharp.auth.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserLoginVO {
    private String token;
    private Long userId;
    private String username;
}
