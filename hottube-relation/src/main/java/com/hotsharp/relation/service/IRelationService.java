package com.hotsharp.relation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotsharp.relation.domain.dto.RelationFormDTO;
import com.hotsharp.relation.domain.po.Relation;
import com.hotsharp.relation.domain.vo.RelationVO;

import java.util.List;

public interface IRelationService extends IService<Relation> {
    List<Long> getFollowingById(Long id);
    List<Long> getFollowerById(Long id);

    RelationVO add(RelationFormDTO form);
    RelationVO upd(RelationFormDTO form);
}
