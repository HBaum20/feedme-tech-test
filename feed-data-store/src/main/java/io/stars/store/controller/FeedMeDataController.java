package io.stars.store.controller;

import io.stars.dto.*;
import io.stars.store.service.FeedMeDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/data")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FeedMeDataController
{
    private final FeedMeDataService feedMeDataService;

    @PostMapping(value = "/events", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<EventDocument>> saveEventToDatabase(@RequestBody final Event event)
    {
        return feedMeDataService.saveEvent(event)
                                .map(eventDocument -> ResponseEntity.created(
                                        URI.create(format("/events/%s", event.getEventId())))
                                                                    .body(eventDocument));
    }

    @PostMapping(value = "/markets", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<MarketDocument>> saveMarketToDatabase(@RequestBody final Market market)
    {
        return feedMeDataService.saveMarket(market)
                                .map(marketDocument -> ResponseEntity.created(
                                        URI.create(format("/markets/%s", marketDocument.getMarketId()))
                                ).body(marketDocument));
    }

    @PostMapping(value = "/outcomes", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<OutcomeDocument>> saveOutcomeToDatabase(@RequestBody final Outcome outcome)
    {
        return feedMeDataService.saveOutcome(outcome)
                                .map(outcomeDocument -> ResponseEntity.created(
                                        URI.create(format("/outcomes/%s", outcomeDocument.getOutcomeId()))
                                ).body(outcomeDocument));
    }

    @GetMapping(value = "/events", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<EventDocumentsResponse>> getEvents()
    {
        return feedMeDataService.getAllEvents()
                                .collectList()
                                .map(events -> EventDocumentsResponse.builder().events(events).build())
                                .filter(response -> !response.getEvents().isEmpty())
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.noContent().build());
    }
}
