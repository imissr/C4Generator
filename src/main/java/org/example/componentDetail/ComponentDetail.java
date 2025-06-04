package org.example.componentDetail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@NoArgsConstructor
@ToString
//POJO(Plain Old Java Object) for the component-value
public class ComponentDetail {


    private String tags;
    private String technology;
    private String description;
    private String container;

    /**
     * Constructor for ComponentDetail.
     *
     * @param tags        Tags associated with the component.
     * @param technology  Technology used in the component.
     * @param description Description of the component.
     */
    @JsonCreator
    public ComponentDetail(@JsonProperty("tags") String tags, @JsonProperty("technology") String technology, @JsonProperty("description") String description , @JsonProperty("container") String container) {
        this.tags = tags;
        this.technology = technology;
        this.description = description;
        this.container = container;
    }




}
