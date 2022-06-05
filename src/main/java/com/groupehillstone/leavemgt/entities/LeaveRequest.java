package com.groupehillstone.leavemgt.entities;

import com.groupehillstone.leavemgt.enums.LeaveStatus;
import com.groupehillstone.leavemgt.enums.LeaveType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "leave_requests")
public class LeaveRequest extends AbstractAuditableEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private LeaveType type;

    @Column(name = "description", length = 10240)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LeaveStatus status;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne
    private Collaborator collaborator;

    @ManyToOne
    private Collaborator firstValidator;

    @ManyToOne
    private Collaborator secondValidator;

}
