## Context

项目为 Spring Boot + Spring AI 演示应用；已有文档 `doc/leave-workflow.md` 描述请假角色与顺序，但缺少可执行的工作流抽象。目标是在**员工已登录**的前提下，提供可复用的工作流基础能力与请假专用流程，便于后续扩展其他审批类业务。

## Goals / Non-Goals

**Goals:**

- 定义清晰的基础工作流组件（实例生命周期、状态、按角色的待办步骤、转移规则）。
- 实现请假流程：**提交 → 主管审批 → 经理终审**，含通过/驳回语义。
- API 与领域模型与现有 Spring Web 风格一致；便于单元测试与 MockMvc 扩展。

**Non-Goals:**

- 不做完整 IAM（OAuth/OIDC 集成可作为后续）；本阶段以「当前登录用户」抽象即可。
- 不强制引入重量级 BPMN 引擎（Camunda 等）；优先进程内状态机 + 可替换持久化。
- 不要求前端界面；仅后端契约与行为。

## Decisions

1. **基础组件模型**  
   - **Decision**：引入与工作流类型无关的「实例 + 当前状态 + 允许的参与者角色 + 转移事件」模型；请假流程为该模型上的一个**定义**（definition）或**模板**。  
   - **Rationale**：与提案中的 `workflow-component` / `leave-approval-flow` 能力拆分一致，便于测试与复用。  
   - **Alternatives**：仅针对请假写死 if/else—— rejected（难扩展）。

2. **持久化**  
   - **Decision**：第一版采用内存或简单 JDBC 表存储流程实例与审批记录（设计阶段可在任务清单中二选一）；接口层与持久化解耦。  
   - **Rationale**：演示与小团队落地优先速度；后续可换仓库实现。  
   - **Alternatives**：一上来 Mongo/Event sourcing——推迟。

3. **认证边界**  
   - **Decision**：REST 层假定请求上下文中可解析 `principal`（用户标识）与角色集合（至少区分 employee / supervisor / manager）；未登录请求 MUST 拒绝相关端点。  
   - **Rationale**：满足「员工登录后请假」；不与具体安全框架绑定。

4. **API 形态**  
   - **Decision**：资源导向 REST：`POST` 发起请假、`POST`/语义化动作表达审批；响应携带实例 id、当前状态、下一步可操作角色。  
   - **Rationale**：易测试、易文档化。

## Risks / Trade-offs

- **[Risk]** 内存存储进程重启丢失实例 → **[Mitigation]** 文档标注仅限开发；生产选型 JDBC/JPA 并在 tasks 中落地。  
- **[Risk]** 角色与组织架构耦合 → **[Mitigation]** 主管/经理可先配置为用户 id 或静态映射，规格中允许后续替换为组织服务。

## Migration Plan

- 新功能为主；无存量流程数据。**Rollback**：下线相关 Controller/Bean 即可。

## Open Questions

- 请假是否需要日历区间、类型（病假/年假）等字段：可在实现 tasks 中定为最小字段集（起止日期 + 原因）。
- 主管/经理与员工的绑定关系：首版可用请求参数或配置文件映射。

---

## Implementation notes（落地后补充）

- **持久化**：当前 `LeaveWorkflowService` 使用内存 `ConcurrentHashMap` 存放实例，仅适合开发与演示；升级为 JDBC/JPA 时可保留领域模型与 REST 契约，替换存储实现即可。
- **安全**：`SecurityConfiguration` 提供内存用户与 Basic 认证；生产应接入统一认证中心或表单/OIDC，并关闭 `{noop}` 明文口令。
