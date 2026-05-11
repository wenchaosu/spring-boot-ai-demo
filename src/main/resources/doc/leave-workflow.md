# 请假工作流说明

## 1. 概述

创建一条**请假**业务工作流，用于规范员工发起请假后的多级审批过程。

## 2. 角色

| 角色 | 说明 |
|------|------|
| 员工 | 发起请假申请 |
| 主管 | 对员工请假进行第一层审批 |
| 经理 | 在主管通过后进行最终审批 |

实现中与 Spring Security 角色对应关系：`ROLE_EMPLOYEE`、`ROLE_SUPERVISOR`、`ROLE_MANAGER`。

## 3. 流程说明

1. **员工**提交请假申请。
2. **主管**审批：通过后进入下一步；不通过则流程结束（驳回）。
3. **主管通过后**，由**经理**进行最终审批：通过后请假流程**完成**；不通过则流程结束（驳回）。

简言之：**员工请假 → 主管审批 → 经理审批 → 工作流完成**。

## 4. HTTP API（当前实现）

Base path：`/api/leave/workflows`。均使用 **HTTP Basic** 认证（演示环境）；未登录访问返回 **401**。

| 方法 | 路径 | 角色 | 说明 |
|------|------|------|------|
| POST | `/api/leave/workflows` | 员工 | 提交请假，请求体 JSON：`reason`、`startDate`、`endDate`（ISO 日期）。成功后状态为 `AWAITING_SUPERVISOR`。 |
| GET | `/api/leave/workflows/{id}` | 已登录 | 查询实例状态；不存在返回 **404**。 |
| POST | `/api/leave/workflows/{id}/supervisor-decision` | 主管 | 请求体：`{"approve": true\|false}`。 |
| POST | `/api/leave/workflows/{id}/manager-decision` | 经理 | 请求体：`{"approve": true\|false}`。仅在 `AWAITING_MANAGER` 时合法。 |

响应 JSON 字段示例：`id`、`employeeUsername`、`state`、`reason`、`startDate`、`endDate`、`nextActorAuthority`（下一步所需角色，如 `ROLE_SUPERVISOR`；终态为 `null`）。

### 演示账号（内存用户，勿用于生产）

| 用户名 | 密码 | 角色 |
|--------|------|------|
| alice | alice | 员工 |
| bob | bob | 主管 |
| carol | carol | 经理 |

### 持久化说明

实例保存在**进程内存**中，重启即丢失；生产环境应替换为数据库实现。
