package io.stars.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Header
{
    private Long msgId;
    private String operation;
    private String type;
    private Long timestamp;
}
