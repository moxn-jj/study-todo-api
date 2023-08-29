package com.mun.todo;

import static org.assertj.core.api.Assertions.assertThat;

import com.mun.todo.entity.Todo;
import com.mun.todo.repository.TodoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@DataJpaTest
public class JpaMappingTest {
    
    private final String content = "Junit 테스트 내용";
    
    @Autowired
    private TodoRepository todoRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    private Todo getSaved() {

        Todo todo =  Todo.builder()
                .content(content)
                .createdDateTime(LocalDateTime.now())
                .build();
        
        return entityManager.persist(todo);
    }

    @Test
    public void testGet(){

        // given
        Todo todo = getSaved();
        System.out.println(todo.toString());
        Long id= todo.getId();

        // when
        Todo savedTodo = todoRepository.getOne(id);

        // then
        assertThat(savedTodo.getContent()).isEqualTo(content);
        assertThat(savedTodo.getContent()).isEqualTo(todo.getContent());
    }

    @Test
    public void testSave(){

        // given
        Todo todo = Todo.builder()
                .content("testSave 테스트 하기")
                .isComplete(true)
                .createdDateTime(LocalDateTime.now())
                .build();

        // when
        Todo savedTodo = todoRepository.save(todo);
        System.out.println(savedTodo.toString());

        // then
        assertThat(savedTodo.getId()).isGreaterThan(0);
        assertThat(savedTodo.getContent()).isEqualTo("testSave 테스트 하기");
        assertThat(savedTodo.getIsComplete()).isEqualTo(true);
    }

    @Test
    public void testDelete(){

        // given
        Todo todo = getSaved();
        System.out.println(todo.toString());
        Long id = todo.getId();

        // when
        todoRepository.deleteById(id);

        // then
        assertThat(entityManager.find(Todo.class, id)).isNull();
    }

}
