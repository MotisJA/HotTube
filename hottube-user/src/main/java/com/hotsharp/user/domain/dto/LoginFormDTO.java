package com.hotsharp.user.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(name = "登录表单实体")
public class LoginFormDTO {

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED,example = "admin")
    private String username;

    @NotNull(message = "密码不能为空")
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED,example = "123456")
    private String password;

    @Schema(description = "是否记住我", requiredMode = Schema.RequiredMode.REQUIRED,example = "false")
    private Boolean rememberMe = false;

}
