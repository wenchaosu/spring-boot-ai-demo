package com.example.springbootaidemo.workflow;

import java.util.UUID;

/**
 * Stable identifier for a workflow instance (generic workflow-component primitive).
 */
public record WorkflowInstanceId(UUID value) {

    public static WorkflowInstanceId generate() {
        return new WorkflowInstanceId(UUID.randomUUID());
    }

    public static WorkflowInstanceId of(UUID uuid) {
        return new WorkflowInstanceId(uuid);
    }
}
