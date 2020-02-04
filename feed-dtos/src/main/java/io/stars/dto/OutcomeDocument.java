package io.stars.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutcomeDocument {
    private Long msgId;
    private String operation;
    private String type;
    private Long timestamp;
    private String marketId;
    @Id private String outcomeId;
    private String name;
    private String price;
    private boolean displayed;
    private boolean suspended;
}
