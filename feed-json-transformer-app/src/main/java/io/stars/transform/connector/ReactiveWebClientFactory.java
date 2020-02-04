package io.stars.transform.connector;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@UtilityClass
public class ReactiveWebClientFactory
{
    public static WebClient createWebClient(final String host, final Integer port)
    {
        final String url = format("%s:%d", host, port);
        return WebClient.builder()
                        .baseUrl(url)
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .defaultUriVariables(Collections.singletonMap("url", url))
                        .build();
    }
}
