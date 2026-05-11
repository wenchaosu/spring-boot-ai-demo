package com.example.springbootaidemo.workflow.dto;

import com.example.springbootaidemo.workflow.LeaveWorkflowInstance;

import java.time.LocalDate;
import java.util.UUID;

public record LeaveWorkflowResponse(
        UUID id,
        String employeeUsername,
        String state,
        String reason,
        LocalDate startDate,
        LocalDate endDate,
        String nextActorAuthority) {

    public static LeaveWorkflowResponse from(LeaveWorkflowInstance instance) {
        return new LeaveWorkflowResponse(
                instance.id().value(),
                instance.employeeUsername(),
                instance.state().name(),
                instance.reason(),
                instance.startDate(),
                instance.endDate(),
                instance.nextActorAuthority());
    }
}
