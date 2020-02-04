package io.stars.store.converter;

import io.stars.store.error.MalformedEventException;
import io.stars.dto.*;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventDocumentConverterTest
{
    private EventDocumentConverter converter;

    @Before
    public void setUp()
    {
        converter = new EventDocumentConverter();
    }

    @Test
    public void createDocumentWithEventCreatesEventDocument()
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

        final EventDocument eventDoc = converter.convertToDocument(event);

        assertThat(eventDoc).isNotNull();
        assertThat(eventDoc.getMsgId()).isEqualTo(1L);
        assertThat(eventDoc.getOperation()).isEqualTo("create");
        assertThat(eventDoc.getType()).isEqualTo("event");
        assertThat(eventDoc.getTimestamp()).isEqualTo(1000L);
        assertThat(eventDoc.getEventId()).isEqualTo("event_id");
        assertThat(eventDoc.getCategory()).isEqualTo("Football");
        assertThat(eventDoc.getSubCategory()).isEqualTo("Sky Bet League One");
        assertThat(eventDoc.getName()).isEqualTo("Scunthorpe vs Oxford");
        assertThat(eventDoc.getStartTime()).isEqualTo(2000L);
        assertThat(eventDoc.isDisplayed()).isFalse();
        assertThat(eventDoc.isSuspended()).isTrue();
    }

    @Test
    public void createMarketDocumentShouldCreateMarketDocument()
    {
        final Market market = Market.builder()
                                    .header(Header.builder()
                                                  .msgId(2L)
                                                  .operation("create")
                                                  .type("market")
                                                  .timestamp(1000L)
                                                  .build())
                                    .eventId("event_id")
                                    .marketId("market_id")
                                    .name("Full Time Result")
                                    .displayed(false)
                                    .suspended(true)
                                    .build();

        final MarketDocument marketDoc = converter.convertToDocument(market);

        assertThat(marketDoc).isNotNull();
        assertThat(marketDoc.getMsgId()).isEqualTo(2L);
        assertThat(marketDoc.getOperation()).isEqualTo("create");
        assertThat(marketDoc.getType()).isEqualTo("market");
        assertThat(marketDoc.getTimestamp()).isEqualTo(1000L);
        assertThat(marketDoc.getEventId()).isEqualTo("event_id");
        assertThat(marketDoc.getMarketId()).isEqualTo("market_id");
        assertThat(marketDoc.getName()).isEqualTo("Full Time Result");
        assertThat(marketDoc.isDisplayed()).isEqualTo(false);
        assertThat(marketDoc.isSuspended()).isEqualTo(true);
    }

    @Test
    public void createOutcomeDocumentShouldCreateOutcomeDocument()
    {
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

        final OutcomeDocument outcomeDoc = converter.convertToDocument(outcome);

        assertThat(outcomeDoc).isNotNull();
        assertThat(outcomeDoc.getMsgId()).isEqualTo(3L);
        assertThat(outcomeDoc.getOperation()).isEqualTo("create");
        assertThat(outcomeDoc.getType()).isEqualTo("outcome");
        assertThat(outcomeDoc.getTimestamp()).isEqualTo(1000L);
        assertThat(outcomeDoc.getMarketId()).isEqualTo("market_id");
        assertThat(outcomeDoc.getOutcomeId()).isEqualTo("outcome_id");
        assertThat(outcomeDoc.getName()).isEqualTo("Scunthorpe");
        assertThat(outcomeDoc.getPrice()).isEqualTo("7/2");
        assertThat(outcomeDoc.isDisplayed()).isFalse();
        assertThat(outcomeDoc.isSuspended()).isTrue();
    }

    @Test(expected = MalformedEventException.class)
    public void createDocumentWithMissingHeaderFieldsThrowsException()
    {
        final Event event = Event.builder()
                                 .header(Header.builder()
                                               .msgId(1L)
                                               .operation("create")
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

        converter.convertToDocument(event);
    }
}