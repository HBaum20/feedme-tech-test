package io.stars.store.repository;

import io.stars.dto.EventDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedMeMongoRepository extends MongoRepository<EventDocument, Long>
{
    Optional<EventDocument> findByEventId(String eventId);

    @Query("{'markets.marketId': ?0}")
    Optional<EventDocument> findByMarketId(String marketId);

    @Query("{'markets.outcomes.outcomeId': ?0}")
    Optional<EventDocument> findByOutcomeId(String outcomeId);
}
