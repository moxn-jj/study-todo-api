package com.mun.todo.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table
public class Todo implements Serializable {

    private static final long serialVersionUID = -947585423656694361L;

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @Column
    private LocalDateTime createdDateTime;

    @Column
    private Boolean isComplete;

    @Column
    private String color;

    @Builder
    public Todo(Long id, String content, LocalDateTime createdDateTime, Boolean isComplete, String color) {
        this.id = id;
        this.content = content;
        this.createdDateTime = createdDateTime;
        this.isComplete = isComplete;
        this.color = color;
    }
}
