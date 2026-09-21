package com.mag.app.user;

import java.time.Instant;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "user_seq", initialValue = 1100, allocationSize = 1)
    private Integer id;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be 50 characters or fewer")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be 50 characters or fewer")
    private String lastName;

    @NotBlank(message = "Email address is required")
    @Email(message = "Please provide a valid email address")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Profession is required")
    private String profession;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "City is required")
    private String city;

    @CreationTimestamp
    @Column(name = "date_created", nullable = false, updatable = false)
    private Instant dateCreated;
}