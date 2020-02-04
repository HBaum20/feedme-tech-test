package io.stars.transform.connector;

import io.stars.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@RequiredArgsConstructor
public class DataStoreConnector
{
    private static final String EXTERNAL_ERROR_MESSAGE = "Downstream error from feedme data store: {}";
    private static final String DATA_PATH = "data";
    private static final String EVENTS_PATH = "events";
    private static final String MARKETS_PATH = "markets";
    private static final String OUTCOMES_PATH = "outcomes";

    private final WebClient webClient;

    public Mono<EventDocument> saveEventToDatabase(final Event event)
    {
        return webClient.post()
                        .uri(builder -> builder.pathSegment(DATA_PATH)
                                               .pathSegment(EVENTS_PATH)
                                               .build())
                        .contentType(APPLICATION_JSON)
                        .bodyValue(event)
                        .retrieve()
                        .onStatus(HttpStatus::isError, response -> Mono.error(WebClientResponseException.create(
                                response.statusCode().value(),
                                response.statusCode().getReasonPhrase(),
                                response.headers().asHttpHeaders(),
                                null,
                                null
                        )))
                        .bodyToMono(EventDocument.class)
                        .doOnError(ex -> log.error(EXTERNAL_ERROR_MESSAGE, ex.getMessage()));
    }

    public Mono<MarketDocument> saveMarketToDatabase(final Market market)
    {
        return webClient.post()
                        .uri(builder -> builder.pathSegment(DATA_PATH)
                                               .pathSegment(MARKETS_PATH)
                                               .build())
                        .contentType(APPLICATION_JSON)
                        .bodyValue(market)
                        .retrieve()
                        .onStatus(HttpStatus::isError, response -> Mono.error(WebClientResponseException.create(
                                response.statusCode().value(),
                                response.statusCode().getReasonPhrase(),
                                response.headers().asHttpHeaders(),
                                null,
                                null
                        )))
                        .bodyToMono(MarketDocument.class)
                        .doOnError(ex -> log.error(EXTERNAL_ERROR_MESSAGE, ex.getMessage()));
    }

    public Mono<OutcomeDocument> saveOutcomeToDatabase(final Outcome outcome)
    {
        return webClient.post()
                        .uri(builder -> builder.pathSegment(DATA_PATH)
                                               .pathSegment(OUTCOMES_PATH)
                                               .build())
                        .contentType(APPLICATION_JSON)
                        .bodyValue(outcome)
                        .retrieve()
                        .onStatus(HttpStatus::isError, response -> Mono.error(WebClientResponseException.create(
                                response.statusCode().value(),
                                response.statusCode().getReasonPhrase(),
                                response.headers().asHttpHeaders(),
                                null,
                                null
                        )))
                        .bodyToMono(OutcomeDocument.class)
                        .doOnError(ex -> log.error(EXTERNAL_ERROR_MESSAGE, ex.getMessage()));
    }
}
