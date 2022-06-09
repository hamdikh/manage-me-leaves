package com.groupehillstone.leavemgt.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "holidays")
public class Holiday extends AbstractAuditableEntity {

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "designation")
    private String designation;

    @Column(columnDefinition = "boolean default true")
    private Boolean isEnabled = true;

    @Column(name = "zone")
    private String zone;

    @Column(name = "year")
    private int year;

}
