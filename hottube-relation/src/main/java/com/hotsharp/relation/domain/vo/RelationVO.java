package com.hotsharp.relation.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RelationVO {
    private Long followerId;
    private Long followedId;
}
