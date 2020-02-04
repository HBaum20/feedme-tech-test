package io.stars.dto;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Message
{
    protected Header header;
    protected String name;
    protected boolean displayed;
    protected boolean suspended;
}
