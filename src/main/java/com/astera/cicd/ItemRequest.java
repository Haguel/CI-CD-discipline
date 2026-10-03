package com.astera.cicd;

import jakarta.validation.constraints.NotBlank;

public record ItemRequest(@NotBlank String name, String description) {
}
