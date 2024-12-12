package com.hotsharp.video.service;


import com.hotsharp.common.result.Result;
import com.hotsharp.video.pojo.entity.Category;

public interface CategoryService {
    /**
     * 获取全部分区数据
     * @return CustomResponse对象
     */
    Result getAll();

    /**
     * 根据id查询对应分区信息
     * @param mcId 主分区ID
     * @param scId 子分区ID
     * @return Category类信息
     */
    Category getCategoryById(String mcId, String scId);
}
