package com.example.springbootaidemo.web;

import com.example.springbootaidemo.config.SecurityConfiguration;
import com.example.springbootaidemo.workflow.LeaveWorkflowService;
import com.example.springbootaidemo.workflow.LeaveWorkflowState;
import com.example.springbootaidemo.workflow.WorkflowAuthorities;
import com.example.springbootaidemo.workflow.WorkflowInstanceId;
import com.example.springbootaidemo.workflow.LeaveWorkflowInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LeaveWorkflowController.class)
@Import(SecurityConfiguration.class)
class LeaveWorkflowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeaveWorkflowService leaveWorkflowService;

    private static LeaveWorkflowInstance sample(UUID id) {
        return new LeaveWorkflowInstance(
                WorkflowInstanceId.of(id),
                "alice",
                LeaveWorkflowState.AWAITING_SUPERVISOR,
                "年假",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 3));
    }

    @Test
    void postLeaveWithoutAuthReturns401() throws Exception {
        mockMvc.perform(post("/api/leave/workflows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {"reason":"x","startDate":"2026-06-01","endDate":"2026-06-03"}\
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postLeaveAsEmployeeReturns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(leaveWorkflowService.submit(any(), any())).thenReturn(sample(id));

        mockMvc.perform(post("/api/leave/workflows")
                        .with(httpBasic("alice", "alice"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {"reason":"年假","startDate":"2026-06-01","endDate":"2026-06-03"}\
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("AWAITING_SUPERVISOR"))
                .andExpect(jsonPath("$.nextActorAuthority").value(WorkflowAuthorities.SUPERVISOR));
    }

    @Test
    void getWorkflowReturns404WhenMissing() throws Exception {
        UUID id = UUID.randomUUID();
        when(leaveWorkflowService.get(any(), eq(id))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/leave/workflows/" + id).with(httpBasic("alice", "alice")))
                .andExpect(status().isNotFound());
    }

    @Test
    void supervisorRejectReturnsRejected() throws Exception {
        UUID id = UUID.randomUUID();
        LeaveWorkflowInstance rejected = new LeaveWorkflowInstance(
                WorkflowInstanceId.of(id),
                "alice",
                LeaveWorkflowState.REJECTED,
                "年假",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 3));
        when(leaveWorkflowService.supervisorDecision(any(), eq(id), eq(false)))
                .thenReturn(rejected);

        mockMvc.perform(post("/api/leave/workflows/" + id + "/supervisor-decision")
                        .with(httpBasic("bob", "bob"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"approve\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("REJECTED"));
    }
}
