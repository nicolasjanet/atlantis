package com.vialink.atlantis.api;

import lombok.Builder;

import java.util.UUID;

public record Foo(UUID id) {

    @Builder
    public Foo {
    }
}
