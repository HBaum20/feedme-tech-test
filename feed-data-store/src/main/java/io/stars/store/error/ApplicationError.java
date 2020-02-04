package io.stars.store.error;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonDeserialize
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationError
{
    private Integer status;
    private String errorId;
    private String errorType;
    private String errorMessage;
}
