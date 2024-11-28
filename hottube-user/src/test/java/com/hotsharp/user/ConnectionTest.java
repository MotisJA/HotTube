package com.hotsharp.user;

import com.hotsharp.user.domain.dto.RegisterFormDTO;
import com.hotsharp.user.service.IUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConnectionTest {

    @Autowired
    public IUserService userService;

    @Test
    public void testConnection() {
        System.out.println("Connection test");
    }

    @Test
    public void testJdbc() {
        String jdbcUrl = "jdbc:mysql://172.31.170.153:3306/hottube?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai";
        String username = "root";  // 替换为你的数据库用户名
        String password = "root";  // 替换为你的数据库密码

        // 测试连接
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            if (connection != null && !connection.isClosed()) {
                System.out.println("Connection successful!");
            }
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testRegister() {
        RegisterFormDTO registerFormDTO = new RegisterFormDTO();
        registerFormDTO.setUsername("test");
        registerFormDTO.setPassword("test");
        userService.register(registerFormDTO);
    }
}
