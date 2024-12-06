package com.hotsharp.relation.controller;

import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.relation.domain.dto.RelationFormDTO;
import com.hotsharp.relation.domain.vo.RelationVO;
import com.hotsharp.relation.service.IRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/relation")
@RequiredArgsConstructor
@Tag(name = "关注相关接口")
public class RelationController {

    private final IRelationService relationService;

    @Operation(summary = "获取粉丝信息")
    @GetMapping("/following/{userId}")
    public Result<List<Long>> getFollowerInfo(@PathVariable Long userId){
        return Results.success(relationService.getFollowerById(userId));
    }

    @Operation(summary = "获取关注信息")
    @GetMapping("/followed/{userId}")
    public Result<List<Long>> getFollowingInfo(@PathVariable Long userId){
        return Results.success(relationService.getFollowingById(userId));
    }

    @Operation(summary = "添加关注条目接口")
    @PostMapping("add")
    public Result<RelationVO> add(@RequestBody RelationFormDTO relationFormDTO){
        return Results.success(relationService.add(relationFormDTO));
    }

    @Operation(summary = "更新关注条目接口")
    @PostMapping("update")
    public Result<RelationVO> upd(@RequestBody RelationFormDTO relationFormDTO){
        return Results.success(relationService.upd(relationFormDTO));
    }

}