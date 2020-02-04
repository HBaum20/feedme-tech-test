package io.stars.transform.config;

import io.stars.transform.connector.DataStoreConnector;
import io.stars.transform.connector.ReactiveWebClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataStoreConfig
{
    @Value("${data.store.baseUrl}")
    private String baseUrl;

    @Value("${data.store.port}")
    private Integer port;

    @Bean
    public DataStoreConnector dataStoreConnector()
    {
        return new DataStoreConnector(ReactiveWebClientFactory.createWebClient(baseUrl, port));
    }
}
