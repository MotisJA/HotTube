package com.hotsharp.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.InfoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ElasticSearchConfigTest {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

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
}