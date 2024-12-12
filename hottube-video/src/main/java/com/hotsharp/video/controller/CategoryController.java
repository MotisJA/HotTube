package com.hotsharp.video.controller;

import com.hotsharp.common.result.Result;
import com.hotsharp.video.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 获取全部分区接口
     * @return CustomResponse对象
     */
    @GetMapping("/video/category/getall")
    public Result getAll() {
        return categoryService.getAll();
    }
}
