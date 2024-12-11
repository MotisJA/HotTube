package com.hotsharp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotsharp.common.constant.VideoConstant;
import com.hotsharp.common.utils.UserContext;
import com.hotsharp.mapper.VideoMapper;
import com.hotsharp.pojo.dto.VideoDTO;
import com.hotsharp.pojo.entity.Video;
import com.hotsharp.service.VideoService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {

    @Resource
    private VideoMapper videoMapper;

    @Override
    public void publish(VideoDTO videoDTO) {
        Video video = new Video();
        BeanUtils.copyProperties(videoDTO, video);
        video.setUid(UserContext.getUserId());
        video.setStatus(VideoConstant.VIDEO_STATUS_CONVERTING);
        video.setUploadDate(new Date());
        videoMapper.insert(video);
    }

    @Override
    public void updateStatus(Integer vid, Integer status) {
        Video video = new Video();
        video.setStatus(status);
        video.setVid(vid);
        videoMapper.updateById(video);
    }

    @Override
    public List<Video> list() {
        QueryWrapper<Video> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", UserContext.getUserId());
        List<Video> videos = videoMapper.selectList(wrapper);
        return videos;
    }

    @Override
    public boolean deleteByVid(Integer vid) {
        Video video = videoMapper.selectById(vid);
        if (!video.getUid().equals(UserContext.getUserId())) {
            return false;
        }
        videoMapper.deleteById(vid);
        return true;
    }
}
