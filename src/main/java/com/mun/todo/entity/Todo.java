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
@Table(name = "TODO")
public class Todo extends Common implements Serializable {

    private static final long serialVersionUID = -947585423656694361L;

    @Id
    @Column(name = "TODO_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "IS_COMPLETE")
    private Boolean isComplete;

    @Column(name = "COLOR")
    private String color;

    @Builder
    public Todo(Long id, String content, Boolean isComplete, String color) {
        this.id = id;
        this.content = content;
        this.isComplete = isComplete;
        this.color = color;
    }
}
