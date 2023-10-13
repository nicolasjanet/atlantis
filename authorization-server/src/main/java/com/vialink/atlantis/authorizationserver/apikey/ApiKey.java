package com.vialink.atlantis.authorizationserver.apikey;

import com.vialink.atlantis.authorizationserver.user.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKey {

    @Id
    @GeneratedValue
    @Column(
            columnDefinition = "CHAR(36)"
    )
    private UUID id;

    @Column(
            nullable = false,
            columnDefinition = "CHAR(36)"
    )
    protected UUID spaceId;

    @Column(nullable = false, unique = true)
    private String applicationName;

    @Column(nullable = false)
    private String applicationDescription;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String secret;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private boolean authorizationCode;

    @Column(nullable = false)
    private boolean clientCredentials;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "api_key_role",
            joinColumns = @JoinColumn(name = "api_key_id", nullable = false)
    )
    private List<Role> roles = new ArrayList<>();

}
