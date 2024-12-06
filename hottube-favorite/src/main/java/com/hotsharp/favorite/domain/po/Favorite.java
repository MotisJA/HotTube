package com.hotsharp.favorite.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 收藏夹实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("favorite")
@Accessors(chain = true)
public class Favorite {
    @TableId(type = IdType.AUTO)
    private Integer fid;    // 收藏夹ID
    private Integer uid;    // 所属用户ID
    private Integer type;   // 收藏夹类型 1默认收藏夹 2用户创建
    private Integer visible;    // 对外开放 0隐藏 1公开
    private String cover;   // 收藏夹封面url
    private String title;   // 收藏夹名称
    private String description; // 简介
    private Integer count;  // 收藏夹中视频数量
    private Integer isDelete;   // 是否删除 1已删除
}

