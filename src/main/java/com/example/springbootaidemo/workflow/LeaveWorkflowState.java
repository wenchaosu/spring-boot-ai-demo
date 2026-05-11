package com.example.springbootaidemo.workflow;

/**
 * Leave approval states: employee → supervisor → manager (see leave-approval-flow spec).
 */
public enum LeaveWorkflowState {
    AWAITING_SUPERVISOR,
    AWAITING_MANAGER,
    COMPLETED,
    REJECTED
}
