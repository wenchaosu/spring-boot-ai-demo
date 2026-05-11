package com.example.springbootaidemo.workflow;

import com.example.springbootaidemo.workflow.dto.LeaveSubmitBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LeaveWorkflowServiceTest {

    private LeaveWorkflowService service;

    @BeforeEach
    void setUp() {
        service = new LeaveWorkflowService(new HashMap<>());
    }

    private static Authentication auth(String name, String... roles) {
        var authorities =
                java.util.Arrays.stream(roles).map(SimpleGrantedAuthority::new).toList();
        return new UsernamePasswordAuthenticationToken(name, "pwd", authorities);
    }

    private static LeaveSubmitBody body() {
        return new LeaveSubmitBody("年假", LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 3));
    }

    @Test
    void submitCreatesAwaitingSupervisor() {
        LeaveWorkflowInstance created =
                service.submit(auth("alice", "ROLE_EMPLOYEE"), body());
        assertThat(created.state()).isEqualTo(LeaveWorkflowState.AWAITING_SUPERVISOR);
        assertThat(created.employeeUsername()).isEqualTo("alice");
        assertThat(created.nextActorAuthority()).isEqualTo(WorkflowAuthorities.SUPERVISOR);
    }

    @Test
    void submitForbiddenWithoutEmployeeRole() {
        assertThatThrownBy(() -> service.submit(auth("bob", "ROLE_SUPERVISOR"), body()))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void supervisorApprovesMovesToManager() {
        LeaveWorkflowInstance created =
                service.submit(auth("alice", "ROLE_EMPLOYEE"), body());
        UUID id = created.id().value();

        LeaveWorkflowInstance after =
                service.supervisorDecision(auth("bob", "ROLE_SUPERVISOR"), id, true);

        assertThat(after.state()).isEqualTo(LeaveWorkflowState.AWAITING_MANAGER);
        assertThat(after.nextActorAuthority()).isEqualTo(WorkflowAuthorities.MANAGER);
    }

    @Test
    void supervisorRejectsEndsWorkflow() {
        LeaveWorkflowInstance created =
                service.submit(auth("alice", "ROLE_EMPLOYEE"), body());
        UUID id = created.id().value();

        LeaveWorkflowInstance after =
                service.supervisorDecision(auth("bob", "ROLE_SUPERVISOR"), id, false);

        assertThat(after.state()).isEqualTo(LeaveWorkflowState.REJECTED);
    }

    @Test
    void managerCompletesAfterSupervisorApproval() {
        LeaveWorkflowInstance created =
                service.submit(auth("alice", "ROLE_EMPLOYEE"), body());
        UUID id = created.id().value();
        service.supervisorDecision(auth("bob", "ROLE_SUPERVISOR"), id, true);

        LeaveWorkflowInstance done =
                service.managerDecision(auth("carol", "ROLE_MANAGER"), id, true);

        assertThat(done.state()).isEqualTo(LeaveWorkflowState.COMPLETED);
        assertThat(done.nextActorAuthority()).isNull();
    }

    @Test
    void employeeCannotSupervisorApprove() {
        LeaveWorkflowInstance created =
                service.submit(auth("alice", "ROLE_EMPLOYEE"), body());
        UUID id = created.id().value();

        assertThatThrownBy(() -> service.supervisorDecision(auth("alice", "ROLE_EMPLOYEE"), id, true))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void supervisorCannotApproveBeforeTheirStepFromWrongState() {
        LeaveWorkflowInstance created =
                service.submit(auth("alice", "ROLE_EMPLOYEE"), body());
        UUID id = created.id().value();
        service.supervisorDecision(auth("bob", "ROLE_SUPERVISOR"), id, true);

        assertThatThrownBy(() -> service.supervisorDecision(auth("bob", "ROLE_SUPERVISOR"), id, true))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }
}
