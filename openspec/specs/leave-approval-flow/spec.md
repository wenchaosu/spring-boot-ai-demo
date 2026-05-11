## Requirements

### Requirement: Employee submits leave after login

Only an authenticated employee SHALL be able to initiate a leave request that enters the leave approval workflow.

#### Scenario: Submit creates pending supervisor review

- **WHEN** an authenticated employee submits a valid leave request
- **THEN** the system SHALL create a workflow instance in a state awaiting supervisor approval

### Requirement: Supervisor approval gate

The workflow MUST require supervisor approval before any manager final approval when supervisor approval has not yet been granted.

#### Scenario: Supervisor approves

- **WHEN** an authenticated supervisor approves the pending request for that instance
- **THEN** the workflow SHALL transition to a state awaiting manager approval

#### Scenario: Supervisor rejects

- **WHEN** an authenticated supervisor rejects the pending request for that instance
- **THEN** the workflow SHALL reach a terminal rejected state and MUST NOT proceed to manager approval

### Requirement: Manager final approval

After supervisor approval, the workflow MUST require manager approval to complete successfully.

#### Scenario: Manager approves

- **WHEN** an authenticated manager approves the instance that is awaiting manager approval
- **THEN** the workflow SHALL reach a terminal completed state

#### Scenario: Manager rejects

- **WHEN** an authenticated manager rejects the instance that is awaiting manager approval
- **THEN** the workflow SHALL reach a terminal rejected state

### Requirement: Authenticated access only

Endpoints that start or act on leave workflow instances MUST require an authenticated principal; unauthenticated requests MUST be denied.

#### Scenario: Unauthenticated submit denied

- **WHEN** a client without authentication attempts to submit or act on a leave workflow
- **THEN** the system SHALL deny access without mutating workflow state
