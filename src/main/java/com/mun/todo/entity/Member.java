package com.mun.todo.entity;

import com.mun.todo.enums.Authority;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Table(name = "MEMBER") // 엔티티와 매핑할 테이블을 지정
@Entity(name = "MEMBER") // 해당 클래스를 JPA가 관리하게 함, DB 테이블과 매핑할 클래스에 필수로 붙여야 함, 기본생성자 필수로 가져야 함
public class Member extends Common {

    @Id
    @Column(name = "MEMBER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 DB에 위임 : id 값이 null로 들어오면 auto_increment
    private Long id;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "AUTHORITY")
    @Enumerated(EnumType.STRING) // Enum 타입 객체를 매핑하며 enum 이름을 DB에 저장함 (기본값은 순서를 지정하근 ORDINAL이나 사용하지 않음)
    private Authority authority;

    @Builder // 빌더 패턴
    public Member(String email, String password, Authority authority){
        this.email = email;
        this.password = password;
        this.authority = authority;
    }

}
