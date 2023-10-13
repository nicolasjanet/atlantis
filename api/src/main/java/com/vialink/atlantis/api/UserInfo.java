package com.vialink.atlantis.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Builder
@Getter
public class UserInfo {
    private final UUID id;
    @JsonProperty("space_id")
    private final UUID spaceId;
    @JsonProperty("first_name")
    private final String firstName;
    @JsonProperty("last_name")
    private final String lastName;
    private final String email;
    private final String phone;
    private final Set<RoleDefinition> roles;
}
