package org.example.crudstudent.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({"timestamp", "status", "message", "data", "path"})
public class ApiResponse<T> {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyy-MM-dd | HH:mm:ss")
    private LocalDateTime timestamp;
    private Integer status;
    private String message;
    private T data;
    private String path;
}
