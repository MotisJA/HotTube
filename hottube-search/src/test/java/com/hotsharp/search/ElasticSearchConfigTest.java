package com.hotsharp.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.InfoResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotsharp.api.client.UserClient;
import com.hotsharp.api.client.VideoClient;
import com.hotsharp.common.domain.ESUser;
import com.hotsharp.common.domain.ESVideo;
import com.hotsharp.common.domain.User;
import com.hotsharp.common.domain.Video;
import com.hotsharp.common.utils.ESUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ElasticSearchConfigTest {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private ESUtil esUtil;
    @Autowired
    private VideoClient videoClient;
    @Autowired
    private UserClient userClient;

    @Test
    public void testElasticsearchConnection() throws Exception {
        // 获取Elasticsearch信息
        InfoResponse infoResponse = elasticsearchClient.info();

        // 打印Elasticsearch信息
        System.out.println("Elasticsearch cluster name: " + infoResponse.clusterName());
        System.out.println("Elasticsearch cluster UUID: " + infoResponse.clusterUuid());
        System.out.println("Elasticsearch version: " + infoResponse.version().number());

        // 断言连接成功
        assertNotNull(infoResponse);
    }

    // 测试 ElasticSearch
    // 创建索引
    @Test
    void createIndex() throws IOException {
        // 定义资源路径
        String videoPath = "/static/esindex/video.json";
        String userPath = "/static/esindex/user.json";
        String searchWordPath = "/static/esindex/search_word.json";

        // 从资源路径中读取 JSON 文件
        InputStream input1 = this.getClass().getResourceAsStream(videoPath);
        InputStream input2 = this.getClass().getResourceAsStream(userPath);
        InputStream input3 = this.getClass().getResourceAsStream(searchWordPath);

        // 检查 InputStream 是否为 null
        if (input1 == null) {
            throw new IOException("Resource not found: " + videoPath);
        }
        if (input2 == null) {
            throw new IOException("Resource not found: " + userPath);
        }
        if (input3 == null) {
            throw new IOException("Resource not found: " + searchWordPath);
        }

        // 创建 CreateIndexRequest 对象，并将 JSON 文件内容作为索引配置
        CreateIndexRequest req1 = CreateIndexRequest.of(b -> b.index("video").withJson(input1));
        CreateIndexRequest req2 = CreateIndexRequest.of(b -> b.index("user").withJson(input2));
        CreateIndexRequest req3 = CreateIndexRequest.of(b -> b.index("search_word").withJson(input3));

        // 使用 Elasticsearch 客户端创建索引
        CreateIndexResponse resp1 = elasticsearchClient.indices().create(req1);
        CreateIndexResponse resp2 = elasticsearchClient.indices().create(req2);
        CreateIndexResponse resp3 = elasticsearchClient.indices().create(req3);

        // 打印创建索引的响应
        System.out.println(resp1);
        System.out.println(resp2);
        System.out.println(resp3);
    }

    // 批量添加文档
    @Test
    void bulkAddDocVideo() throws IOException {
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
//        queryWrapper.ne("status", 3);
//        List<Video> videos = videoMapper.selectList(queryWrapper);
        List<Video> videos = videoClient.selectVideos(new Video()).getData();
        List<BulkOperation> bulkOperationList = new ArrayList<>();
        for (Video video : videos) {
            ESVideo esVideo = new ESVideo(video.getVid(), video.getUid(), video.getTitle(), video.getMcId(), video.getScId(), video.getTags(), video.getStatus());
            bulkOperationList.add(BulkOperation.of(o -> o.index(i -> i.document(esVideo).id(esVideo.getVid().toString()))));
        }
        BulkResponse bulkResponse = elasticsearchClient.bulk(b -> b.index("video").operations(bulkOperationList));
        System.out.println(bulkResponse);
    }

    @Test
    void bulkAddDocUser() throws IOException {
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.ne("state", 2);
//        List<User> users = userMapper.selectList(queryWrapper);
        List<User> users = userClient.getUserList(new User());
        List<BulkOperation> bulkOperationList = new ArrayList<>();
        for (User user : users) {
            ESUser esUser = new ESUser(user.getUid(), user.getNickname());
            bulkOperationList.add(BulkOperation.of(o -> o.index(i -> i.document(esUser).id(esUser.getUid().toString()))));
        }
        BulkResponse bulkResponse = elasticsearchClient.bulk(b -> b.index("user").operations(bulkOperationList));
        System.out.println(bulkResponse);
    }
}