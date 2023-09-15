package com.mun.todo.strategy;

import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.util.Locale;

/**
 * 기본적으로 JPA는 lower case naming 전략을 사용하고 있음
 * 해당 프로젝트는 대문자로 관리하기 위해서 해당 클래스 추가 후 application.properties에 설정 추가
 */
public class UpperCaseNamingStrategy extends CamelCaseToUnderscoresNamingStrategy {

    @Override
    protected Identifier getIdentifier(String name, boolean quoted, JdbcEnvironment jdbcEnvironment) {
        if (this.isCaseInsensitive(jdbcEnvironment)) {
            name = name.toUpperCase(Locale.ROOT);
        }

        return new Identifier(name, quoted);
    }
}
