package org.example.componentDetail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Models a single relation from one component to another.
 */
@Getter
@ToString
@NoArgsConstructor
public class Relations {

    private String target;
    private String type;

    @JsonCreator
    public Relations(
            @JsonProperty("target") String target,
            @JsonProperty("type") String type
    ) {
        this.target = target;
        this.type = type;
    }
}
