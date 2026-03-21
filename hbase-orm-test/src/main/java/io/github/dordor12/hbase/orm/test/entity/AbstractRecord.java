package io.github.dordor12.hbase.orm.test.entity;

import io.github.dordor12.hbase.orm.annotation.Column;
import io.github.dordor12.hbase.orm.annotation.MappedSuperclass;

import java.time.LocalDateTime;

/**
 * MappedSuperclass example - shared fields inherited by subclasses.
 */
@MappedSuperclass
public abstract class AbstractRecord {

    @Column(family = "a", qualifier = "created_at")
    private LocalDateTime createdAt;

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
