package com.mag.app.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequestDto(

        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must be 50 characters or fewer")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must be 50 characters or fewer")
        String lastName,

        @NotBlank(message = "Email address is required")
        @Email(message = "Please provide a valid email address")
        String email,

        @NotBlank(message = "Profession is required")
        String profession,

        @NotBlank(message = "Country is required")
        String country,

        @NotBlank(message = "City is required")
        String city
) {}