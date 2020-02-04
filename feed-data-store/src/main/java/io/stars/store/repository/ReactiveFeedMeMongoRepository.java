package io.stars.store.repository;

import io.stars.dto.EventDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ReactiveFeedMeMongoRepository implements ReactiveMongoRepository
{
    private final FeedMeMongoRepository repository;

    @Override
    public synchronized Mono<EventDocument> save(final EventDocument event)
    {
        return Mono.just(repository.save(event));
    }

    @Override
    public synchronized Mono<EventDocument> getByEventId(final String eventId)
    {
        return Mono.justOrEmpty(repository.findByEventId(eventId));
    }

    @Override
    public synchronized Mono<EventDocument> getByMarketId(final String marketId)
    {
        return Mono.justOrEmpty(repository.findByMarketId(marketId));
    }

    @Override
    public synchronized Mono<EventDocument> getByOutcomeId(final String outcomeId)
    {
        return Mono.justOrEmpty(repository.findByOutcomeId(outcomeId));
    }

    @Override
    public Flux<EventDocument> getAll()
    {
        return Flux.fromIterable(repository.findAll());
    }

    @Override
    public synchronized Mono<Void> deleteEvent(final EventDocument eventDocument)
    {
        repository.delete(eventDocument);
        return Mono.empty();
    }
}
