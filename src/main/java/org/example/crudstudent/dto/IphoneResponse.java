package org.example.crudstudent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonPropertyOrder({
        "id",
        "name",
        "description",
        "price",
        "color",
        "isAvailable"
})
public class IphoneResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String color;
    @JsonProperty("isAvailable")
    private Boolean available;
}
