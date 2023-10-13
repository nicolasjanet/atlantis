package com.vialink.atlantis.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class ApiKeyInfo {
    private final UUID id;
    @JsonProperty("application_name")
    private final String applicationName;
    private final String email;
}
