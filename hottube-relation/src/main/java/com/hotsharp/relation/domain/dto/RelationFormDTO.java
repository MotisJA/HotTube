package com.hotsharp.relation.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(description = "关注表单实体")
public class RelationFormDTO {

    @ApiModelProperty(value = "关注者", required = true)
    private Long followerUid;

    @ApiModelProperty(value = "被关注者", required = true)
    private Long followedUid;

    @ApiModelProperty(value = "是关注还是取关", required = true)
    private Boolean Status;

}