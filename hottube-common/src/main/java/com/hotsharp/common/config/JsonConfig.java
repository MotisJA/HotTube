package com.hotsharp.common.config;

//import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;

//@Configuration
public class JsonConfig {
//    @Bean
//    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
//        return jacksonObjectMapperBuilder -> {
//            // long -> string
//            jacksonObjectMapperBuilder.serializerByType(Long.class, ToStringSerializer.instance);
//            jacksonObjectMapperBuilder.serializerByType(BigInteger.class, ToStringSerializer.instance);
//        };
//    }
}