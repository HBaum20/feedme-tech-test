package io.stars.store.error;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonDeserialize
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationErrors
{
    private List<ApplicationError> errors;
}
