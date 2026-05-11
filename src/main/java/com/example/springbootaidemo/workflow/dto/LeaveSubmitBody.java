package com.example.springbootaidemo.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record LeaveSubmitBody(
        @NotBlank String reason,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate) {}
