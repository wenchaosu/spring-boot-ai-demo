## 1. Workflow foundation

- [x] 1.1 Add domain types for workflow instance id, state name, and role-gated transition interface (`workflow-component`)
- [x] 1.2 Implement an in-memory workflow registry/service that creates instances and applies transitions only when actor role matches current step
- [x] 1.3 Add unit tests for unauthorized transition and valid progression

## 2. Leave approval definition

- [x] 2.1 Encode leave flow states: `AWAITING_SUPERVISOR`, `AWAITING_MANAGER`, `COMPLETED`, `REJECTED` with transitions matching `leave-approval-flow` spec
- [x] 2.2 Map authenticated principal to roles `EMPLOYEE`, `SUPERVISOR`, `MANAGER` (stub resolver or property-based mapping for demo)

## 3. API & security wiring

- [x] 3.1 Add REST endpoints: employee submits leave; supervisor/manager approve or reject; optional GET status by instance id
- [x] 3.2 Integrate Spring Security (or minimal filter) so anonymous calls are denied on these endpoints; permit authenticated users with role checks in service layer
- [x] 3.3 Add MockMvc tests covering happy path and rejection paths per scenarios

## 4. Documentation & polish

- [x] 4.1 Align `doc/leave-workflow.md` or README snippet with implemented endpoints and roles
- [x] 4.2 Document persistence limitation (in-memory) and JDBC upgrade path in design follow-up if needed
