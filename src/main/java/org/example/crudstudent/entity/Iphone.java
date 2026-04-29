package org.example.crudstudent.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "iphones")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Iphone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private BigDecimal price;
    private String color;
    @Column(name = "is_available")
    private Boolean available;

}
