package com.webflux.webflux.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class RequestSampleDto {
    @NotBlank
    @Size(min = 10, max = 100)
    private String name;
    @Future
    private LocalDateTime startDate;
    @Future
    private LocalDateTime endDate;

    @AssertTrue
    private boolean isNameIncluded() {
        return name.contains("hello");
    }

}
