package io.stars.transform.service.transform;

import io.stars.dto.*;
import io.stars.transform.error.MessageCreationException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonObjectCreatorTest {
    private JsonObjectCreator objectCreator;

    @Before
    public void setUp() {
        objectCreator = new JsonObjectCreator();
    }

    @Test(expected = MessageCreationException.class)
    public void createMessage_withUnknownType_throwsException() {
        final String message = "|1|create|parlay|1580488990623|ff3e7e4c-df91-4967-9ed9-1cca4b32aaa3|Football|Sky Bet League Two|\\|Colchester\\| vs \\|Coventry\\||1580489024113|0|1|";
        objectCreator.createMessage(message);
    }

    @Test
    public void createMessageOfTypeEvent_createsValidMessageOfTypeEvent() {
        String packet1 = "|1|create|event|1580488990623|ff3e7e4c-df91-4967-9ed9-1cca4b32aaa3|Football|Sky Bet League Two|\\|Colchester\\| vs \\|Coventry\\||1580489024113|0|1|";

        final Message message = objectCreator.createMessage(packet1);

        assertThat(message).isNotNull();
        assertThat(message instanceof Event).isTrue();
        assertThat((Event) message).isEqualToComparingFieldByField(Event.builder()
                                                                        .header(Header.builder()
                                                                                      .msgId(1L)
                                                                                      .operation("create")
                                                                                      .type("event")
                                                                                      .timestamp(1580488990623L)
                                                                                      .build())
                                                                        .eventId("ff3e7e4c-df91-4967-9ed9-1cca4b32aaa3")
                                                                        .category("Football")
                                                                        .subCategory("Sky Bet League Two")
                                                                        .name("|Colchester| vs |Coventry|")
                                                                        .startTime(1580489024113L)
                                                                        .suspended(true)
                                                                        .displayed(false)
                                                                        .build());
    }

    @Test
    public void createMessageOfTypeMarket_createsValidMessageOfTypeMarket() {
        String packet2 = "|2|create|market|1580488990625|ff3e7e4c-df91-4967-9ed9-1cca4b32aaa3|76b986b6-d263-4ae6-8b87-043b29b61995|Full Time Result|0|1|";

        final Message message = objectCreator.createMessage(packet2);

        assertThat(message).isNotNull();
        assertThat(message instanceof Market).isTrue();
        assertThat((Market) message).isEqualToComparingFieldByField(Market.builder()
                                                                          .header(Header.builder()
                                                                                        .msgId(2L)
                                                                                        .operation("create")
                                                                                        .type("market")
                                                                                        .timestamp(1580488990625L)
                                                                                        .build())
                                                                          .eventId("ff3e7e4c-df91-4967-9ed9-1cca4b32aaa3")
                                                                          .marketId("76b986b6-d263-4ae6-8b87-043b29b61995")
                                                                          .name("Full Time Result")
                                                                          .displayed(false)
                                                                          .suspended(true)
                                                                          .build());
    }

    @Test
    public void createMessageOfTypeOutcome_createsValidMessageOfTypeOutcome()
    {
        String packet3 = "|3|create|outcome|1580488990626|76b986b6-d263-4ae6-8b87-043b29b61995|b032f0f9-2290-4f84-8c7c-24a7c5da0bac|\\|Colchester\\||6/5|0|1|";

        final Message message = objectCreator.createMessage(packet3);

        assertThat(message).isNotNull();
        assertThat(message instanceof Outcome).isTrue();
        assertThat((Outcome) message).isEqualToComparingFieldByField(Outcome.builder()
                                                                            .header(Header.builder()
                                                                                          .msgId(3L)
                                                                                          .operation("create")
                                                                                          .type("outcome")
                                                                                          .timestamp(1580488990626L)
                                                                                          .build())
                                                                            .marketId("76b986b6-d263-4ae6-8b87-043b29b61995")
                                                                            .outcomeId("b032f0f9-2290-4f84-8c7c-24a7c5da0bac")
                                                                            .name("|Colchester|")
                                                                            .price("6/5")
                                                                            .displayed(false)
                                                                            .suspended(true)
                                                                            .build());
    }

    @Test(expected = MessageCreationException.class)
    public void createMessageUsingErroneousData_shouldThrowException()
    {
        String packet3 = "|3|create|outcome|1580488990626|76b986b6-d263-4ae6-8b87-043b29b61995@i@am@banksy@";

        objectCreator.createMessage(packet3);
    }
}