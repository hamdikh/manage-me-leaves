package com.groupehillstone.leavemgt.entities;

import com.groupehillstone.leavemgt.enums.LeaveStatus;
import com.groupehillstone.leavemgt.enums.LeaveType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "leave_requests")
public class LeaveRequest extends AbstractAuditableEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LeaveStatus status;

    @OneToMany
    private List<Leave> leaves;

    @ManyToOne
    private Collaborator collaborator;

    @ManyToOne
    private Collaborator firstValidator;

    @ManyToOne
    private Collaborator secondValidator;

    @Column(name = "first_validation_at")
    private LocalDateTime firstValidationAt;

    @Column(name = "second_validation_at")
    private LocalDateTime secondValidationAt;

}
