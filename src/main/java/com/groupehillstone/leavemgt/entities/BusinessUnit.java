package com.groupehillstone.leavemgt.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "business_units")
public class BusinessUnit extends AbstractAuditableEntity {

    @Column
    private String name;

    @Column
    private String code;

}
