package com.groupehillstone.leavemgt.entities;

import com.groupehillstone.leavemgt.enums.LeaveStatus;
import com.groupehillstone.leavemgt.enums.LeaveTime;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "leaves")
public class Leave extends AbstractAuditableEntity {

    @ManyToOne
    private LeaveType type;

    @Column(name = "description", length = 10240)
    private String description;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(name = "leave_days")
    private Map<LocalDate, LeaveTime> leaveDays = new HashMap<>();

    @ManyToOne
    private LeaveRequest leaveRequest;

}
