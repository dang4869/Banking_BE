package com.me.base.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base entity class that provides common fields for all entities.
 * This abstract class includes:
 * - Primary key (id)
 * - Audit fields (createdAt, updatedAt)
 * <p>
 * All entities should extend this class to inherit these common fields.
 * The audit fields are automatically populated by Spring Data JPA auditing.
 * 
 * @author Base Project
 * @version 1.0
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    
    /**
     * Primary key for the entity.
     * Uses auto-increment strategy for ID generation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Timestamp when the entity was created.
     * Automatically populated on entity creation.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the entity was last updated.
     * Automatically updated on entity modification.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
