package com.example.springbootaidemo.workflow;

/**
 * Spring Security role names for workflow actors (aligned with UserDetails roles).
 */
public final class WorkflowAuthorities {

    public static final String EMPLOYEE = "ROLE_EMPLOYEE";
    public static final String SUPERVISOR = "ROLE_SUPERVISOR";
    public static final String MANAGER = "ROLE_MANAGER";

    private WorkflowAuthorities() {}
}
