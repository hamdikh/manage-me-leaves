package com.groupehillstone.leavemgt.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "leave_types")
public class LeaveType extends AbstractAuditableEntity {

    @Column(name = "name")
    private String name;

}
