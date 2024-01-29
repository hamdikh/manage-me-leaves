package com.groupehillstone.leavemgt.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractAuditableEntity extends AbstractEntity {

    @CreationTimestamp
    @Column(updatable = false)
    protected LocalDateTime createdAt;

    @ManyToOne
    protected Collaborator createdBy;

    @UpdateTimestamp
    @Column
    protected LocalDateTime updatedAt;

    @ManyToOne
    protected Collaborator updatedBy;

}