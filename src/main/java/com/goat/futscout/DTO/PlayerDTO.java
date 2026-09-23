package com.goat.futscout.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PlayerDTO {

    private String id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Club is required")
    private String club;

    @NotBlank(message = "Position is required")
    private String position;

    @NotBlank(message = "Nationality is required")
    private String nationality;

    @Min(value = 14, message = "Age must be at least 14")
    @Max(value = 50, message = "Age must be at most 50")
    private int age;

    @Min(value = 0, message = "Goals cannot be negative")
    private int goals;

    @Min(value = 0, message = "Assists cannot be negative")
    private int assists;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "10.0")
    private double rating;

    @Min(value = 0, message = "Market value cannot be negative")
    private double marketValue;
}
