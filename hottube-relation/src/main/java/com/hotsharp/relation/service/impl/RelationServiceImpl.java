package com.hotsharp.relation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotsharp.relation.domain.dto.RelationFormDTO;
import com.hotsharp.relation.domain.po.Relation;
import com.hotsharp.relation.domain.vo.RelationVO;
import com.hotsharp.relation.mapper.RelationMapper;
import com.hotsharp.relation.service.IRelationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelationServiceImpl extends ServiceImpl<RelationMapper, Relation> implements IRelationService {

    @Override
    public List<Long> getFollowingById(Long id) {
        // 执行查询
        List<Relation> relations = lambdaQuery().eq(Relation::getFollowerId, id).list();

        // 将查询结果转换为List<Long>
        return relations.stream()
                .map(Relation::getFollowerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getFollowerById(Long id) {
        // 执行查询
        List<Relation> relations = lambdaQuery().eq(Relation::getFollowedId, id).list();

        // 将查询结果转换为List<Long>
        return relations.stream()
                .map(Relation::getFollowerId)
                .collect(Collectors.toList());
    }

    @Override
    public RelationVO add(RelationFormDTO form) {
        Long follower = form.getFollowerUid();
        Long followed = form.getFollowedUid();
        // 1.校验关注条目是否存在
        Relation relation = lambdaQuery().eq(Relation::getFollowerId, follower).eq(Relation::getFollowedId, followed).one();

        if(relation != null){
            // 2.存在则更新条目状态
            relation.setStatus(form.getStatus());
            relation.setCreatedDate(LocalDateTime.now());
            save(relation);
        }
        else {
            // 2.保存关注条目
            Relation newRelation = new Relation()
                    .setFollowerId(follower)
                    .setFollowedId(followed)
                    .setStatus(true)
                    .setCreatedDate(LocalDateTime.now());
            save(newRelation);
        }
        // 3.返回VO
        RelationVO vo = new RelationVO()
                .setFollowerId(follower)
                .setFollowedId(followed);
        return vo;
    }

    @Override
    public RelationVO upd(RelationFormDTO form) {
        Long follower = form.getFollowerUid();
        Long followed = form.getFollowedUid();
        // 1.校验关注条目是否存在
        Relation relation = lambdaQuery().eq(Relation::getFollowerId, follower).eq(Relation::getFollowedId, followed).one();
        Assert.notNull(relation, "未找到关注条目");

        // 2.更新关注条目状态
        relation.setStatus(form.getStatus());

        if(form.getStatus()){
            relation.setCreatedDate(LocalDateTime.now());
        }
        else {
            relation.setDeletedDate(LocalDateTime.now());
        }
        save(relation);

        // 3.返回VO
        RelationVO vo = new RelationVO()
                .setFollowerId(follower)
                .setFollowedId(followed);
        return vo;
    }
}
