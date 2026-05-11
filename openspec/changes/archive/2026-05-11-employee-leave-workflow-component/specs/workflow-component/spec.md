## ADDED Requirements

### Requirement: Workflow instance exists with identifiable state

The system SHALL represent each running workflow as an instance with a unique identifier and a current state from a defined set of states for that workflow type.

#### Scenario: Instance created

- **WHEN** a workflow is started for an authenticated subject with valid inputs
- **THEN** the system SHALL create an instance with a unique id and an initial state consistent with the workflow definition

### Requirement: Role-gated transitions

The system SHALL allow advancing or rejecting a workflow instance only when the authenticated actor holds a role authorized for the current step.

#### Scenario: Unauthorized actor blocked

- **WHEN** a user without the required role attempts an action on the current step
- **THEN** the system SHALL reject the operation with an authorization failure and MUST NOT change workflow state

### Requirement: Observable progression

The system SHALL expose enough information for clients to determine the current state and which role may act next.

#### Scenario: Query current step

- **WHEN** a client requests the workflow instance status with a valid identifier
- **THEN** the response MUST include the current state and the allowed actor role(s) for the next action when the workflow is not terminal
