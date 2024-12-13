package com.hotsharp.relation.domain.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RelationFormDTO {

    private Long followerUid;

    private Long followedUid;

    private Boolean Status;

}