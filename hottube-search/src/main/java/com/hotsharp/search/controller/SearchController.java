package com.hotsharp.search.controller;

import com.hotsharp.api.client.UserClient;
import com.hotsharp.api.client.VideoClient;
import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.common.utils.ESUtil;
import com.hotsharp.search.domain.po.HotSearch;
import com.hotsharp.search.service.ISearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.List;

@Tag(name = "search", description = "搜索相关接口")
@RestController
public class SearchController {
    @Autowired
    private ISearchService searchService;

    @Autowired
    private ESUtil esUtil;

    @Autowired
    private VideoClient videoClient;

    @Autowired
    private UserClient userClient;

    /**
     * 获取热搜词条
     * @return  热搜列表
     */
    @Operation(summary = "获取热搜词条")
    @GetMapping("/search/hot/get")
    public Result<List<HotSearch>> getHotSearch() {
        return Results.success(searchService.getHotSearch());
    }

    /**
     * 添加搜索词或者给该搜索词热度加一
     * @param keyword   搜索词
     * @return  返回格式化后的搜索词，有可能为null
     */
    @Operation(summary = "添加搜索词或者给该搜索词热度加一")
    @PostMapping("/search/word/add")
    public Result<String> addSearchWord(@RequestParam("keyword") String keyword) {
        return Results.success(searchService.addSearchWord(keyword));
    }

    /**
     * 根据输入内容获取相关搜索推荐词
     * @param keyword   关键词
     * @return  包含推荐搜索词的列表
     */
    @Operation(summary = "根据输入内容获取相关搜索推荐词")
    @GetMapping("/search/word/get")
    public Result<List<String>> getSearchWord(@RequestParam("keyword") String keyword) throws UnsupportedEncodingException {
        keyword = URLDecoder.decode(keyword, "UTF-8");  // 解码经过url传输的字符串
        if (keyword.trim().length() == 0) {
            return Results.success(Collections.emptyList());
        }
        return Results.success(searchService.getMatchingWord(keyword));
    }

    /**
     * 获取各种类型相关数据数量  视频&用户
     * @param keyword   关键词
     * @return  包含视频数量和用户数量的顺序列表
     */
    @Operation(summary = "获取各种类型相关数据数量  视频&用户")
    @GetMapping("/search/count")
    public Result<List<Long>> getCount(@RequestParam("keyword") String keyword) throws UnsupportedEncodingException {
        keyword = URLDecoder.decode(keyword, "UTF-8");  // 解码经过url传输的字符串
        return Results.success(searchService.getCount(keyword));
    }

    /**
     * 搜索相关已过审视频
     * @param keyword   关键词
     * @param page  第几页
     * @return  视频列表
     * @throws UnsupportedEncodingException
     */
    @GetMapping("/search/video/only-pass")
    public Result getMatchingVideo(@RequestParam("keyword") String keyword, @RequestParam("page") Integer page) throws UnsupportedEncodingException {
        keyword = URLDecoder.decode(keyword, "UTF-8");  // 解码经过url传输的字符串
        List<Integer> vids = esUtil.searchVideosByKeyword(keyword, page, 30, true);
        return Results.success(videoClient.getVideosWithDataByIdList(vids));
    }

    /**
     * 搜索用户
     * @param keyword
     * @param page
     * @return
     * @throws UnsupportedEncodingException
     */
    @Operation(summary = "搜索用户")
    @GetMapping("/search/user")
    public Result getMatchingUser(@RequestParam("keyword") String keyword, @RequestParam("page") Integer page) throws UnsupportedEncodingException {
        keyword = URLDecoder.decode(keyword, "UTF-8");  // 解码经过url传输的字符串
        List<Integer> uids = esUtil.searchUsersByKeyword(keyword, page, 30);
        return Results.success(userClient.getUserByIdList(uids));
    }
}
