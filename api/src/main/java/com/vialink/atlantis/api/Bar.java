package com.vialink.atlantis.api;

import lombok.Builder;

public record Bar(Foo foo, String baz) {

    @Builder
    public Bar {
    }
}
