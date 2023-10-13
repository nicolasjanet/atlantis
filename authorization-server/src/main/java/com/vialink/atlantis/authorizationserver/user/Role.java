package com.vialink.atlantis.authorizationserver.user;

import com.vialink.atlantis.api.RoleDefinition;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class Role implements Serializable {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleDefinition role;

    @Column(columnDefinition = "CHAR(36)", nullable = false)
    private UUID spaceId;

    private boolean execute;

    private boolean grant;

    public Role() {
    }

    public Role(RoleDefinition role, UUID targetSpaceId, boolean execute, boolean grant) {
        this.role = role;
        this.spaceId = targetSpaceId;
        this.execute = execute;
        this.grant = grant;
    }

    @Override
    public String toString() {
        List<String> elements = new ArrayList<>();
        elements.add(role.toString());
        elements.add(spaceId.toString());
        if (execute) {
            elements.add("EXECUTE");
        }
        if (grant) {
            elements.add("GRANT");
        }
        return String.join(":", elements);
    }

}
