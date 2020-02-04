package io.stars.store.repository;

import io.stars.dto.EventDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveMongoRepository
{
    Mono<EventDocument> save(EventDocument event);
    Mono<EventDocument> getByEventId(String eventId);
    Mono<EventDocument> getByMarketId(String marketId);
    Mono<EventDocument> getByOutcomeId(String outcomeId);
    Flux<EventDocument> getAll();
    Mono<Void> deleteEvent(final EventDocument eventDocument);
}
