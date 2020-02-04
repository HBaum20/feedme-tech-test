package io.stars.store.service;

import io.stars.store.converter.EventDocumentConverter;
import io.stars.store.repository.ReactiveMongoRepository;
import io.stars.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FeedMeDataService
{
    private final ReactiveMongoRepository repository;
    private final EventDocumentConverter converter;

    public Mono<EventDocument> saveEvent(final Event event)
    {
        return repository.getByEventId(event.getEventId())
                         .map(eventDocument -> converter.convertToDocument(event).toBuilder().markets(eventDocument.getMarkets()).build())
                         .switchIfEmpty(Mono.defer(() -> Mono.just(converter.convertToDocument(event))))
                         .flatMap(repository::save);
    }



    public Mono<MarketDocument> saveMarket(final Market market)
    {
        return repository.getByMarketId(market.getMarketId()) // (1) Check if an (temp) event containing a market with this ID exists
                         .flatMap(event -> getMarketFromEventSatisfyingCondition(event, marketDocument -> equal(marketDocument.getMarketId(), market.getMarketId()))
                                 .map(marketDocument -> addOutcomesToMarket(converter.convertToDocument(market), marketDocument.getOutcomes()))
                                 .doOnNext(marketDocument -> addMarketToEvent(event, marketDocument))
                                 .flatMap(marketDocument -> repository.getByEventId(marketDocument.getEventId())
                                                                      .map(eventDocument -> addEventIdAndMarketsToEvent(null, eventDocument, market)))
                                 .switchIfEmpty(Mono.defer(() -> Mono.just(addEventIdAndMarketsToEvent(market.getEventId(), event, market))))
                                 .doOnNext(eventDocument -> repository.getByMarketId(eventDocument.getMarkets().get(0).getMarketId())
                                                                      .doOnNext(tempEvent -> repository.deleteEvent(tempEvent)
                                                                                                       .subscribe())
                                                                      .subscribe()))
                         .switchIfEmpty(Mono.defer(() -> repository.getByEventId(market.getEventId()) // Start execution here if event from (1) does not exist: (2) check if event with eventID in market exists
                                                                   .switchIfEmpty(Mono.defer(() -> Mono.just(EventDocument.builder() // Start execution here if event from (3) does not exist
                                                                                                                          .eventId(market.getEventId())
                                                                                                                          .build())))
                                                                   .doOnNext(event -> addMarketToEvent(event, converter.convertToDocument(market)))))
                         .doOnNext(removeTemporaryMarkets())
                         .flatMap(repository::save)
                         .flatMap(event -> getMarketWithId(event, market.getMarketId()));
    }


    public Mono<OutcomeDocument> saveOutcome(final Outcome outcome)
    {
        return repository.getByMarketId(outcome.getMarketId()) // (1) Check if event with market ID in outcome exists
                         .switchIfEmpty(Mono.defer(() -> Mono.just(EventDocument.builder().build()) // Start execution here if event from (1) does not exist: create new temporary event to persist outcome
                                                             .doOnNext(event -> addMarketToEvent(event, MarketDocument.builder()
                                                                                                                      .marketId(outcome.getMarketId())
                                                                                                                      .build()))))
                         .flatMap(event -> getMarketFromEventSatisfyingCondition(event, market -> equal(market.getMarketId(), outcome.getMarketId()))
                                 .doOnNext(market -> market.getOutcomes().add(converter.convertToDocument(outcome)))
                                 .map(market -> event))
                         .flatMap(repository::save)
                         .flatMap(event -> getMarketFromEventSatisfyingCondition(event, market -> equal(market.getMarketId(), outcome.getMarketId())))
                         .flatMap(market -> getOutcomeFromMarketSatisfyingCondition(market, outcomeDocument -> outcomeDocument.getOutcomeId().equals(outcome.getOutcomeId())));
    }

    public Flux<EventDocument> getAllEvents()
    {
        return repository.getAll();
    }

    private Consumer<EventDocument> removeTemporaryMarkets()
    {
        return event -> event.setMarkets(event.getMarkets()
                                              .stream()
                                              .filter(marketDocument -> marketDocument.getMsgId() != null)
                                              .collect(Collectors.toList()));
    }

    private MarketDocument addOutcomesToMarket(final MarketDocument market, final List<OutcomeDocument> outcomes)
    {
        return market.toBuilder().outcomes(outcomes).build();
    }

    private EventDocument addEventIdAndMarketsToEvent(final String eventId, final EventDocument event, final Market market)
    {
        return event.toBuilder()
                    .eventId(eventId)
                    .markets(event.getMarkets()
                                  .stream()
                                  .filter(marketDocument -> equal(marketDocument.getMarketId(), market.getMarketId()))
                                  .collect(Collectors.toList()))
                    .build();
    }

    private static Mono<MarketDocument> getMarketWithId(final EventDocument event, final String marketId)
    {
        return Mono.justOrEmpty(event.getMarkets()
                                     .stream()
                                     .filter(marketDocument -> equal(marketDocument.getMarketId(), marketId))
                                     .findAny());
    }

    private static boolean equal(final String str1, final String str2)
    {
        return str1.equals(str2);
    }

    private static void addMarketToEvent(final EventDocument event, final MarketDocument market)
    {
        event.getMarkets().add(market);
    }

    private static Mono<MarketDocument> getMarketFromEventSatisfyingCondition(final EventDocument event, final Predicate<MarketDocument> condition)
    {
        return Mono.justOrEmpty(event.getMarkets()
                                     .stream()
                                     .filter(condition)
                                     .findAny());
    }

    private static Mono<OutcomeDocument> getOutcomeFromMarketSatisfyingCondition(final MarketDocument market, final Predicate<OutcomeDocument> condition)
    {
        return Mono.justOrEmpty(market.getOutcomes()
                                      .stream()
                                      .filter(condition)
                                      .findAny());
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
