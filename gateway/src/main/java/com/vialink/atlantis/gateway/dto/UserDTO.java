package com.vialink.atlantis.gateway.dto;

import java.util.List;

public record UserDTO(List<String> roles, String firstName, String lastName, String identifier) {
}
