package com.groupehillstone.leavemgt.entities;

import com.groupehillstone.leavemgt.identity.IdentityRole;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "collaborators", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Collaborator extends AbstractAuditableEntity {

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "identity_role")
    private IdentityRole identityRole;

    @ManyToOne
    private Collaborator commercialManager;

}
