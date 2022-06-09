package com.groupehillstone.leavemgt.entities;

import com.groupehillstone.leavemgt.enums.LeaveStatus;
import com.groupehillstone.leavemgt.enums.LeaveType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "leaves")
public class Leave extends AbstractAuditableEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private LeaveType type;

    @Column(name = "description", length = 10240)
    private String description;

    @ElementCollection
    @Column(name = "leave_days")
    private List<LocalDate> leaveDays = new ArrayList<>();

    @ManyToOne
    private LeaveRequest leaveRequest;

}
