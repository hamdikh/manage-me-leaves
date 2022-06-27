package com.groupehillstone.leavemgt.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "jobs")
public class Job extends AbstractAuditableEntity implements Serializable {

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany
    private Set<Collaborator> collaborators;

    @Column(nullable = false)
    private Integer rank;

}
