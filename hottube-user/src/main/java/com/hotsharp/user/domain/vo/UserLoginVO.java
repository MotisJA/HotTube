package com.hotsharp.user.domain.vo;

import com.alibaba.nacos.shaded.org.checkerframework.common.value.qual.IntRange;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserLoginVO {
    private String token;
    private Long userId;
    private String username;
}
