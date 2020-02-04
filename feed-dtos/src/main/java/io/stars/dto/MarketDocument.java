package io.stars.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MarketDocument {
    private Long msgId;
    private String operation;
    private String type;
    private Long timestamp;
    private String eventId;
    @Id private String marketId;
    private String name;
    private boolean displayed;
    private boolean suspended;

    @Builder.Default
    private List<OutcomeDocument> outcomes = new ArrayList<>();
}
