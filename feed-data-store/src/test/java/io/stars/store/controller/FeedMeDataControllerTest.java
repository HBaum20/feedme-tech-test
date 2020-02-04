package io.stars.store.controller;

import io.stars.store.service.FeedMeDataService;
import io.stars.dto.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RunWith(MockitoJUnitRunner.class)
public class FeedMeDataControllerTest
{
    @Mock
    private FeedMeDataService service;

    @InjectMocks
    private FeedMeDataController controller;

    @Test
    public void saveEvent_shouldSaveEventAndReturnCreated()
    {
        final Event event = Event.builder()
                                 .header(Header.builder()
                                               .msgId(1L)
                                               .operation("create")
                                               .type("event")
                                               .timestamp(1000L)
                                               .build())
                                 .eventId("event_id")
                                 .category("Football")
                                 .subCategory("Sky Bet League One")
                                 .name("Scunthorpe vs Oxford")
                                 .startTime(2000L)
                                 .displayed(false)
                                 .suspended(true)
                                 .build();

        final EventDocument eventDocument = EventDocument.builder()
                                                         .msgId(1L)
                                                         .operation("create")
                                                         .type("event")
                                                         .timestamp(1000L)
                                                         .eventId("event_id")
                                                         .category("Football")
                                                         .subCategory("Sky Bet League One")
                                                         .name("Scunthorpe vs Oxford")
                                                         .startTime(2000L)
                                                         .displayed(false)
                                                         .suspended(true)
                                                         .build();

        when(service.saveEvent(event)).thenReturn(Mono.just(eventDocument));

        StepVerifier.create(controller.saveEventToDatabase(event))
                    .expectNextMatches(response -> response.getBody().equals(eventDocument) &&
                                                    response.getStatusCode().equals(CREATED))
                    .verifyComplete();

        verify(service, times(1)).saveEvent(event);
    }

    @Test
    public void saveMarket_savesMarketAndReturnsCreated()
    {
        final String eventId = "event_id";
        final String marketId = "market_id";

        final MarketDocument marketDocument = MarketDocument.builder()
                                                            .msgId(2L)
                                                            .operation("complete")
                                                            .type("market")
                                                            .timestamp(1000L)
                                                            .eventId(eventId)
                                                            .marketId(marketId)
                                                            .name("Full Time Result")
                                                            .displayed(false)
                                                            .suspended(true)
                                                            .build();

        final Market market = Market.builder()
                                    .header(Header.builder()
                                                  .msgId(2L)
                                                  .operation("complete")
                                                  .type("market")
                                                  .timestamp(1000L)
                                                  .build())
                                    .eventId(eventId)
                                    .marketId(marketId)
                                    .name("Full Time Result")
                                    .displayed(false)
                                    .suspended(true)
                                    .build();

        when(service.saveMarket(market)).thenReturn(Mono.just(marketDocument));

        StepVerifier.create(controller.saveMarketToDatabase(market))
                    .expectNextMatches(response -> response.getBody().equals(marketDocument) &&
                                                   response.getStatusCode().equals(CREATED))
                    .verifyComplete();

        verify(service, times(1)).saveMarket(market);
    }

    @Test
    public void saveOutcome_savesOutcomeAndReturnsCreated()
    {
        final String marketId = "market_id";
        final String outcomeId = "outcome_id";

        final Outcome outcome = Outcome.builder()
                                       .header(Header.builder()
                                                     .msgId(3L)
                                                     .operation("create")
                                                     .type("outcome")
                                                     .timestamp(1000L)
                                                     .build())
                                       .marketId("market_id")
                                       .outcomeId("outcome_id")
                                       .name("Scunthorpe")
                                       .price("7/2")
                                       .displayed(false)
                                       .suspended(true)
                                       .build();

        final OutcomeDocument outcomeDocument = OutcomeDocument.builder()
                                                               .msgId(3L)
                                                               .operation("create")
                                                               .type("outcome")
                                                               .timestamp(1000L)
                                                               .marketId(marketId)
                                                               .outcomeId(outcomeId)
                                                               .name("Scunthorpe")
                                                               .price("7/2")
                                                               .displayed(false)
                                                               .suspended(true)
                                                               .build();

        when(service.saveOutcome(outcome)).thenReturn(Mono.just(outcomeDocument));

        StepVerifier.create(controller.saveOutcomeToDatabase(outcome))
                    .expectNextMatches(response -> response.getBody().equals(outcomeDocument) &&
                                                   response.getStatusCode().equals(CREATED))
                    .verifyComplete();

        verify(service, times(1)).saveOutcome(outcome);
    }

    @Test
    public void getAllEvents_returnsAllEvents()
    {
        final Flux<EventDocument> events = Flux.just(
                EventDocument.builder().eventId("1").build(),
                EventDocument.builder().eventId("2").build(),
                EventDocument.builder().eventId("3").build(),
                EventDocument.builder().eventId("4").build(),
                EventDocument.builder().eventId("5").build()
        );

        when(service.getAllEvents()).thenReturn(events);

        StepVerifier.create(controller.getEvents())
                    .expectNextMatches(response -> response.getStatusCode().equals(OK) &&
                                                   response.getBody().getEvents().size() == 5)
                    .verifyComplete();

        verify(service, times(1)).getAllEvents();
    }


    @Test
    public void getAllEvents_whenNoneExist_shouldReturnNoContent()
    {
        when(service.getAllEvents()).thenReturn(Flux.empty());

        StepVerifier.create(controller.getEvents())
                    .expectNext(ResponseEntity.noContent().build())
                    .verifyComplete();

        verify(service, times(1)).getAllEvents();
    }
}