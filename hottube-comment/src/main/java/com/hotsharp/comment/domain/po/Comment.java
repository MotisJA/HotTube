package com.hotsharp.comment.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer vid;
    private Integer uid;
    private Integer rootId;
    private Integer parentId;
    private Integer toUserId;
    private String content;
    private Integer love;
    private Integer bad;
    private Date createTime;
    private Integer isTop;
    private Integer isDeleted;
}
