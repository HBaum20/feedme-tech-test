package io.stars.transform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeedMeTCPConfig
{
    @Value("${feedme.tcp.host}")
    private String host;

    @Value("${feedme.tcp.clientPort}")
    private Integer port;

    @Bean
    public String host()
    {
        return host;
    }

    @Bean
    public Integer port()
    {
        return port;
    }
}
