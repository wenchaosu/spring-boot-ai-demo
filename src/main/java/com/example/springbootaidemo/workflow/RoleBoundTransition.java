package com.example.springbootaidemo.workflow;

/**
 * Describes a allowed transition gated by a role (workflow-component abstraction).
 *
 * @param <S> state enum type
 */
public interface RoleBoundTransition<S extends Enum<S>> {

    S from();

    S to();

    /** Spring Security authority string, e.g. ROLE_SUPERVISOR */
    String requiredAuthority();
}
