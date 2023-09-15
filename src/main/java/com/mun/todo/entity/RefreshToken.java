package com.mun.todo.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Getter
@NoArgsConstructor
@Table(name = "REFRESH_TOKEN")
@Entity
public class RefreshToken extends Common {

    @Id
    @Column(name = "MEMBER_ID")
    private String key;

    @Column(name = "REFRESH_TOKEN")
    private String value;

    @Builder
    public RefreshToken(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public RefreshToken updateValue(String token) {
        this.value = token;
        return this;
    }
}