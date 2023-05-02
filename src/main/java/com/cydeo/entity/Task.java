package com.cydeo.entity;

import com.cydeo.enums.Status;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@Table(name = "tasks")
@Where(clause = "is_deleted=false")
public class Task extends BaseEntity {

    private String taskSubject;
    private String taskDetail;
    @Enumerated(EnumType.STRING)
    private Status taskStatus;

    @ManyToOne
    private Project project;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User assignedEmployee;

    private LocalDate assignedDate;


}
