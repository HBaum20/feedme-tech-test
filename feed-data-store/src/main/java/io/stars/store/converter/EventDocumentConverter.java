package io.stars.store.converter;

import io.stars.dto.*;
import io.stars.store.error.MalformedEventException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;


@Component
public class EventDocumentConverter
{
    private static final String NO_HEADER_MESSAGE = "Supplied Event has no header";

    public EventDocument convertToDocument(final Event event)
    {
        return EventDocument.builder()
                            .msgId((Long)getHeaderField(event, Header::getMsgId))
                            .operation((String)getHeaderField(event, Header::getOperation))
                            .type((String)getHeaderField(event, Header::getType))
                            .timestamp((Long)getHeaderField(event, Header::getTimestamp))
                            .eventId(event.getEventId())
                            .category(event.getCategory())
                            .subCategory(event.getSubCategory())
                            .name(event.getName())
                            .startTime(event.getStartTime())
                            .displayed(event.isDisplayed())
                            .suspended(event.isSuspended())
                            .build();
    }

    public MarketDocument convertToDocument(final Market market)
    {
        return MarketDocument.builder()
                             .msgId((Long)getHeaderField(market, Header::getMsgId))
                             .operation((String)getHeaderField(market, Header::getOperation))
                             .type((String)getHeaderField(market, Header::getType))
                             .timestamp((Long)getHeaderField(market, Header::getTimestamp))
                             .eventId(market.getEventId())
                             .marketId(market.getMarketId())
                             .name(market.getName())
                             .displayed(market.isDisplayed())
                             .suspended(market.isSuspended())
                             .build();
    }

    public OutcomeDocument convertToDocument(final Outcome outcome)
    {
        return OutcomeDocument.builder()
                              .msgId((Long)getHeaderField(outcome, Header::getMsgId))
                              .operation((String)getHeaderField(outcome, Header::getOperation))
                              .type((String)getHeaderField(outcome, Header::getType))
                              .timestamp((Long)getHeaderField(outcome, Header::getTimestamp))
                              .marketId(outcome.getMarketId())
                              .outcomeId(outcome.getOutcomeId())
                              .name(outcome.getName())
                              .price(outcome.getPrice())
                              .displayed(outcome.isDisplayed())
                              .suspended(outcome.isSuspended())
                              .build();
    }

    public Object getHeaderField(final Message message, final Function<Header, Object> fieldSupplier)
    {
        return Optional.ofNullable(message.getHeader())
                       .map(fieldSupplier)
                       .orElseThrow(() -> new MalformedEventException(NO_HEADER_MESSAGE));
    }
}
