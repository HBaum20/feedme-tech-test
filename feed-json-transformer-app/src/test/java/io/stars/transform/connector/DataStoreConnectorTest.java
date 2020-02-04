package io.stars.transform.connector;

import com.google.common.io.Resources;
import io.stars.dto.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.net.URL;

import static com.google.common.base.Charsets.UTF_8;
import static org.springframework.http.HttpStatus.*;

public class DataStoreConnectorTest
{
    private MockWebServer server;
    private DataStoreConnector connector;

    @Before
    public void setUp()
    {
        server = new MockWebServer();
        connector = new DataStoreConnector(ReactiveWebClientFactory.createWebClient(server.url("/").toString(), null));
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void sendEventToDatabaseThrowsErrorWhen500()
    {
        server.enqueue(new MockResponse().setResponseCode(INTERNAL_SERVER_ERROR.value()));

        StepVerifier.create(connector.saveEventToDatabase(Event.builder().build()))
                    .expectError(WebClientResponseException.class)
                    .verify();
    }

    @Test
    public void sendEventToDatabaseThrowsErrorWhen400()
    {
        server.enqueue(new MockResponse().setResponseCode(BAD_REQUEST.value()));

        StepVerifier.create(connector.saveEventToDatabase(Event.builder().build()))
                    .expectError(WebClientResponseException.class)
                    .verify();
    }

    @Test
    public void sendEventToDatabaseValidReturnsCreatedWithEvent() throws IOException {
        URL dataResource = Resources.getResource("test-data/postEvent.json");
        String json = Resources.toString(dataResource, UTF_8);

        server.enqueue(new MockResponse().setBody(json)
                                         .setHeader("Content-Type", "application/json")
                                         .setResponseCode(CREATED.value()));

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

        StepVerifier.create(connector.saveEventToDatabase(event))
                    .expectNext(eventDocument)
                    .verifyComplete();
    }

    @Test
    public void sendMarketToDatabaseThrowsErrorWhen500()
    {
        server.enqueue(new MockResponse().setResponseCode(INTERNAL_SERVER_ERROR.value()));

        StepVerifier.create(connector.saveMarketToDatabase(Market.builder().build()))
                    .expectError(WebClientResponseException.class)
                    .verify();
    }

    @Test
    public void sendMarketToDatabaseThrowsErrorWhen400()
    {
        server.enqueue(new MockResponse().setResponseCode(BAD_REQUEST.value()));

        StepVerifier.create(connector.saveMarketToDatabase(Market.builder().build()))
                    .expectError(WebClientResponseException.class)
                    .verify();
    }

    @Test
    public void sendMarketToDatabaseValidReturnsMarket() throws IOException
    {
        final URL dataResource = Resources.getResource("test-data/postMarket.json");
        final String json = Resources.toString(dataResource, UTF_8);

        server.enqueue(new MockResponse().setBody(json)
                                         .setHeader("Content-Type", "application/json")
                                         .setResponseCode(CREATED.value()));

        final String eventId = "event_id";
        final String marketId = "market_id";
        final MarketDocument marketDocument = MarketDocument.builder()
                                                            .msgId(2L)
                                                            .operation("create")
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
                                                  .operation("create")
                                                  .type("market")
                                                  .timestamp(1000L)
                                                  .build())
                                    .eventId(eventId)
                                    .marketId(marketId)
                                    .name("Full Time Result")
                                    .displayed(false)
                                    .suspended(true)
                                    .build();

        StepVerifier.create(connector.saveMarketToDatabase(market))
                    .expectNext(marketDocument)
                    .verifyComplete();
    }

    @Test
    public void sendOutcomeToDatabaseThrowsErrorWhen500()
    {
        server.enqueue(new MockResponse().setResponseCode(INTERNAL_SERVER_ERROR.value()));

        StepVerifier.create(connector.saveOutcomeToDatabase(Outcome.builder().build()))
                    .expectError(WebClientResponseException.class)
                    .verify();
    }

    @Test
    public void sendOutcomeToDatabaseThrowsErrorWhen400()
    {
        server.enqueue(new MockResponse().setResponseCode(BAD_REQUEST.value()));

        StepVerifier.create(connector.saveOutcomeToDatabase(Outcome.builder().build()))
                    .expectError(WebClientResponseException.class)
                    .verify();
    }

    @Test
    public void sendOutcomeToDatabaseValidReturnsOutcome() throws IOException
    {
        final URL dataResource = Resources.getResource("test-data/postOutcome.json");
        final String json = Resources.toString(dataResource, UTF_8);

        server.enqueue(new MockResponse().setBody(json)
                                         .setHeader("Content-Type", "application/json")
                                         .setResponseCode(CREATED.value()));

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

        StepVerifier.create(connector.saveOutcomeToDatabase(outcome))
                    .expectNext(outcomeDocument)
                    .verifyComplete();
    }
}