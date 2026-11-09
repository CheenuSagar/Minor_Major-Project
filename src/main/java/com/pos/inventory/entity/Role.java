package com.pos.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ROLES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "NAME", length = 50, nullable = false, unique = true)
    private RoleType name;

    @Column(name = "DESCRIPTION", length = 200)
    private String description;
}
