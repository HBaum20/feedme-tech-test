package io.stars.store.service;

import io.stars.store.converter.EventDocumentConverter;
import io.stars.store.repository.ReactiveMongoRepository;
import io.stars.dto.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class FeedMeDataServiceTest
{
    @Mock
    private EventDocumentConverter converter;

    @Mock
    private ReactiveMongoRepository repository;

    @InjectMocks
    private FeedMeDataService service;

    @Before
    public void setUp()
    {
        when(repository.deleteEvent(any(EventDocument.class))).thenReturn(Mono.empty());
    }

    @Test
    public void saveEventToEmptyDatabaseShouldSaveEvent()
    {
        final String eventId = "event_id";
        final Event event = Event.builder()
                                 .header(Header.builder()
                                               .msgId(1L)
                                               .operation("create")
                                               .type("event")
                                               .timestamp(1000L)
                                               .build())
                                 .eventId(eventId)
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
                                                         .eventId(eventId)
                                                         .category("Football")
                                                         .subCategory("Sky Bet League One")
                                                         .name("Scunthorpe vs Oxford")
                                                         .startTime(2000L)
                                                         .displayed(false)
                                                         .suspended(true)
                                                         .build();

        when(repository.getByEventId(eventId)).thenReturn(Mono.empty());
        when(converter.convertToDocument(event)).thenReturn(eventDocument);
        when(repository.save(eventDocument)).thenReturn(Mono.just(eventDocument));

        StepVerifier.create(service.saveEvent(event))
                    .expectNext(eventDocument)
                    .verifyComplete();

        verify(repository, times(1)).save(eventDocument);
        verify(converter, times(1)).convertToDocument(event);
    }

    @Test
    public void saveEventWhenEventAlreadyExistsBecauseOfMarketShouldUpdateEventAndAddMarkets()
    {
        final String eventId = "event_id";
        final String marketId = "market_id";

        final Event event = Event.builder()
                                 .header(Header.builder()
                                               .msgId(1L)
                                               .operation("create")
                                               .type("event")
                                               .timestamp(1000L)
                                               .build())
                                 .eventId(eventId)
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
                                                         .eventId(eventId)
                                                         .category("Football")
                                                         .subCategory("Sky Bet League One")
                                                         .name("Scunthorpe vs Oxford")
                                                         .startTime(2000L)
                                                         .displayed(false)
                                                         .suspended(true)
                                                         .build();

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

        final EventDocument existingEvent = EventDocument.builder()
                                                         .eventId("event_id")
                                                         .displayed(false)
                                                         .suspended(false)
                                                         .markets(singletonList(marketDocument))
                                                         .build();

        final EventDocument updatedEvent = eventDocument.toBuilder().markets(singletonList(marketDocument)).build();

        when(repository.getByEventId(event.getEventId())).thenReturn(Mono.just(existingEvent));
        when(converter.convertToDocument(event)).thenReturn(eventDocument);
        when(repository.save(updatedEvent)).thenReturn(Mono.just(updatedEvent));

        StepVerifier.create(service.saveEvent(event))
                    .expectNext(updatedEvent)
                    .verifyComplete();

        verify(repository, times(1)).save(updatedEvent);
        verify(converter, times(1)).convertToDocument(event);
    }

    @Test
    public void saveMarketToExistingEventShouldSaveUpdatedEventWithMarkets()
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

        final EventDocument existingEvent = EventDocument.builder()
                                                         .msgId(1L)
                                                         .operation("create")
                                                         .type("event")
                                                         .timestamp(1000L)
                                                         .eventId(eventId)
                                                         .category("Football")
                                                         .subCategory("Sky Bet League One")
                                                         .name("Scunthorpe vs Oxford")
                                                         .startTime(2000L)
                                                         .displayed(false)
                                                         .suspended(true)
                                                         .build();

        final EventDocument updatedDocument = existingEvent.toBuilder().markets(singletonList(marketDocument)).build();

        when(repository.getByMarketId(marketId)).thenReturn(Mono.empty());
        when(repository.getByEventId(eventId)).thenReturn(Mono.just(existingEvent));
        when(converter.convertToDocument(market)).thenReturn(marketDocument);
        when(repository.save(updatedDocument)).thenReturn(Mono.just(updatedDocument));

        StepVerifier.create(service.saveMarket(market))
                    .expectNext(marketDocument)
                    .verifyComplete();

        verify(repository, times(1)).save(updatedDocument);
        verify(repository, times(1)).getByMarketId(marketId);
        verify(repository, times(1)).getByEventId(eventId);
        verify(converter, times(1)).convertToDocument(market);
    }

    @Test
    public void saveMarketWhenMarketAlreadyExistsForExistingOutcomeCreatesEventWithMarketEventIdAndAddsOutcomes()
    {
        final String eventId = "event_id";
        final String marketId = "market_id";
        final String outcomeId = "outcome_id";

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

        final List<MarketDocument> marketDocs = new ArrayList<>();
        final List<OutcomeDocument> outcomeDocs = new ArrayList<>();
        outcomeDocs.add(outcomeDocument);
        marketDocs.add(MarketDocument.builder()
                                     .marketId(marketId)
                                     .displayed(false)
                                     .suspended(false)
                                     .outcomes(outcomeDocs)
                                     .build());

        final EventDocument existingEvent = EventDocument.builder()
                                                         .eventId("randomisedByMongo")
                                                         .suspended(false)
                                                         .displayed(false)
                                                         .markets(marketDocs)
                                                         .build();

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

        final EventDocument eventDocument = EventDocument.builder()
                                                         .eventId(eventId)
                                                         .displayed(false)
                                                         .suspended(false)
                                                         .build();

        final MarketDocument marketDocumentWithOutcomes = marketDocument.toBuilder().outcomes(singletonList(outcomeDocument)).build();

        final EventDocument updatedEvent = eventDocument.toBuilder()
                                                        .markets(singletonList(marketDocumentWithOutcomes))
                                                        .build();

        when(repository.getByMarketId(marketId)).thenReturn(Mono.just(existingEvent));
        when(converter.convertToDocument(market)).thenReturn(marketDocument);
        when(repository.getByEventId(eventId)).thenReturn(Mono.empty());
        when(repository.save(updatedEvent)).thenReturn(Mono.just(updatedEvent));

        StepVerifier.create(service.saveMarket(market))
                    .expectNext(marketDocumentWithOutcomes)
                    .verifyComplete();

        verify(repository, times(2)).getByMarketId(marketId);
        verify(converter, times(1)).convertToDocument(market);
        verify(repository, times(1)).getByEventId(eventId);
        verify(repository, times(1)).save(updatedEvent);
        verify(repository, times(1)).deleteEvent(any(EventDocument.class));
    }

    @Test
    public void saveMarketWhenCorrespondingEventDoesNotExistAnywhereShouldReturnTempEventWithMarket()
    {
        final String marketId = "market_id";
        final String eventId = "event_id";

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

        final EventDocument resultantEvent = EventDocument.builder()
                                                          .eventId(eventId)
                                                          .markets(singletonList(marketDocument))
                                                          .build();

        when(repository.getByMarketId(marketId)).thenReturn(Mono.empty());
        when(repository.getByEventId(eventId)).thenReturn(Mono.empty());
        when(converter.convertToDocument(market)).thenReturn(marketDocument);
        when(repository.save(resultantEvent)).thenReturn(Mono.just(resultantEvent));

        StepVerifier.create(service.saveMarket(market))
                    .expectNext(marketDocument)
                    .verifyComplete();

        verify(repository, times(1)).getByMarketId(marketId);
        verify(repository, times(1)).getByEventId(eventId);
        verify(converter, times(1)).convertToDocument(market);
        verify(repository, times(1)).save(resultantEvent);
    }

    @Test
    public void addOutcomeWhenNoCorrespondingMarketExistsReturnsTempEventWithTempMarketWithOutcome()
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

        final EventDocument eventDocument = EventDocument.builder()
                                                         .displayed(false)
                                                         .suspended(false)
                                                         .markets(singletonList(
                                                                 MarketDocument.builder()
                                                                               .displayed(false)
                                                                               .suspended(false)
                                                                               .marketId(marketId)
                                                                               .outcomes(
                                                                                       singletonList(
                                                                                               outcomeDocument
                                                                                       )
                                                                               )
                                                                               .build()
                                                         ))
                                                         .build();

        when(repository.getByMarketId(marketId)).thenReturn(Mono.empty());
        when(converter.convertToDocument(outcome)).thenReturn(outcomeDocument);
        when(repository.save(eventDocument)).thenReturn(Mono.just(eventDocument));

        StepVerifier.create(service.saveOutcome(outcome))
                    .expectNext(outcomeDocument)
                    .verifyComplete();

        verify(repository, times(1)).getByMarketId(marketId);
        verify(converter, times(1)).convertToDocument(outcome);
        verify(repository, times(1)).save(eventDocument);
    }

    @Test
    public void saveOutcomeWhenMarketExistsAddsOutcomeToMarket()
    {
        final String eventId = "event_id";
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

        final EventDocument eventDocument = EventDocument.builder()
                                                         .eventId(eventId)
                                                         .markets(singletonList(
                                                                 MarketDocument.builder()
                                                                               .eventId(eventId)
                                                                               .marketId(marketId)
                                                                               .build()
                                                         ))
                                                         .build();

        final EventDocument updatedEvent = eventDocument.toBuilder()
                                                        .markets(eventDocument.getMarkets()
                                                                              .stream()
                                                                              .map(market -> market.toBuilder()
                                                                                                   .outcomes(singletonList(outcomeDocument))
                                                                                                   .build())
                                                                              .collect(Collectors.toList()))
                                                        .build();

        when(repository.getByMarketId(marketId)).thenReturn(Mono.just(eventDocument));
        when(converter.convertToDocument(outcome)).thenReturn(outcomeDocument);
        when(repository.save(updatedEvent)).thenReturn(Mono.just(updatedEvent));

        StepVerifier.create(service.saveOutcome(outcome))
                    .expectNext(outcomeDocument)
                    .verifyComplete();

        verify(repository, times(1)).getByMarketId(marketId);
        verify(converter, times(1)).convertToDocument(outcome);
        verify(repository, times(1)).save(updatedEvent);
    }

    @Test
    public void getAllEventsShouldReturnAllEventsInMongo()
    {
        when(repository.getAll()).thenReturn(Flux.just(
                EventDocument.builder().eventId("1").build(),
                EventDocument.builder().eventId("2").build(),
                EventDocument.builder().eventId("3").build(),
                EventDocument.builder().eventId("4").build(),
                EventDocument.builder().eventId("5").build()
        ));

        StepVerifier.create(service.getAllEvents())
                    .expectNextCount(5)
                    .verifyComplete();
    }
}