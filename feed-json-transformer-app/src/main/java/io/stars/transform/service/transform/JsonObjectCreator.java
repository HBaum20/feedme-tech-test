package io.stars.transform.service.transform;

import io.stars.dto.*;
import io.stars.transform.error.MessageCreationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.lang.Long.parseLong;

@Slf4j
@Component
public class JsonObjectCreator
{
    private static final String EVENT = "event";
    private static final String MARKET = "market";
    private static final String OUTCOME = "outcome";
    private static final String COULD_NOT_IDENTIFY_MESSAGE_TYPE = "Could not identify message type";

    private final Pattern pipePattern = Pattern.compile("(?<!\\\\)\\|");

    public Message createMessage(final String proprietaryData)
    {
        String[] fields = Arrays.stream(pipePattern.split(trimData(proprietaryData)))
                                .map(field -> field.replaceAll("\\\\", ""))
                                .toArray(String[]::new);

        String type = fields[2];
        
        switch(type)
        {
            case EVENT: return createEvent(fields);
            case MARKET: return createMarket(fields);
            case OUTCOME: return createOutcome(fields);
            default: throw new MessageCreationException(COULD_NOT_IDENTIFY_MESSAGE_TYPE);
        }
    }

    private static String trimData(final String proprietaryData)
    {
        return proprietaryData.substring(1, proprietaryData.length() - 1);
    }
    
    private static boolean convertBoolData(final String datum)
    {
        return datum.equals("1");
    }
    
    private Header createHeader(final String[] dataFields)
    {
        try {
            return Header.builder()
                         .msgId(parseLong(dataFields[0]))
                         .operation(dataFields[1])
                         .type(dataFields[2])
                         .timestamp(parseLong(dataFields[3]))
                         .build();
        } catch(Exception ex) {
            log.error("Unable to create header: {}", ex.getMessage());
            return Header.builder().build();
        }
    }
    
    private Event createEvent(final String[] dataFields)
    {
        try {
            return Optional.of(dataFields)
                           .flatMap(fields -> Optional.of(createHeader(fields))
                                                      .map(header -> Event.builder()
                                                                          .header(header)
                                                                          .eventId(fields[4])
                                                                          .category(fields[5])
                                                                          .subCategory(fields[6])
                                                                          .name(fields[7])
                                                                          .startTime(parseLong(fields[8]))
                                                                          .displayed(convertBoolData(fields[9]))
                                                                          .suspended(convertBoolData(fields[10]))
                                                                          .build()))
                           .orElse(Event.builder().build());
        } catch(IndexOutOfBoundsException ex) {
            throw new MessageCreationException(ex.getMessage());
        }
    }
    
    private Market createMarket(final String[] dataFields)
    {
        try {
            return Optional.of(dataFields)
                           .flatMap(fields -> Optional.of(createHeader(fields))
                                                      .map(header -> Market.builder()
                                                                           .header(header)
                                                                           .eventId(fields[4])
                                                                           .marketId(fields[5])
                                                                           .name(fields[6])
                                                                           .displayed(convertBoolData(fields[7]))
                                                                           .suspended(convertBoolData(fields[8]))
                                                                           .build()))
                           .orElse(Market.builder().build());
        } catch(Exception ex) {
            log.error("Could not create market");
            throw new MessageCreationException(ex.getMessage());
        }
    }
    
    private Outcome createOutcome(final String[] dataFields)
    {
        try {
            return Optional.of(dataFields)
                           .flatMap(fields -> Optional.of(createHeader(fields))
                                                      .map(header -> Outcome.builder()
                                                                            .header(header)
                                                                            .marketId(fields[4])
                                                                            .outcomeId(fields[5])
                                                                            .name(fields[6])
                                                                            .price(fields[7])
                                                                            .displayed(convertBoolData(fields[8]))
                                                                            .suspended(convertBoolData(fields[9]))
                                                                            .build()))
                           .orElse(Outcome.builder().build());
        } catch(Exception ex) {
            log.error("Could not create outcome: {}", ex.getMessage());
            throw new MessageCreationException(ex.getMessage());
        }
    }
}
