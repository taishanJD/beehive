package com.quarkdata.yunpan.api.es;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by liuda
 * Date : 2018/3/26
 */
@Configuration
public class ElasticSearchConfig {
    @Value("${TransportAddress}")
    private String transportAddress;

    @Value("${es.port}")
    private Integer port;

    @Value("${cluster.name}")
    private String clusterName;

    @Bean
    public TransportClient client() throws UnknownHostException {
        //注意这里的端口是TCP端口9300,而非HTTP接口9200
        InetSocketTransportAddress node = new InetSocketTransportAddress(InetAddress.getByName(transportAddress),port);
        //机器名称，可以首页查询，这个不能出现错误，否则无法连接ES
        Settings settings= Settings.builder().put("cluster.name",clusterName)
                .put("client.transport.sniff", true).build();
        TransportClient client = new PreBuiltTransportClient( settings);
        client.addTransportAddress(node);
        return client;
    }
}
