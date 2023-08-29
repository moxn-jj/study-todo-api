package com.mun.todo;

import com.mun.todo.domain.Todo;
import com.mun.todo.repository.TodoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@SpringBootApplication
public class TodoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner runner(TodoRepository todoRepository) throws Exception {
//        return (args) -> {
//            IntStream.rangeClosed(1, 10).forEach(index -> todoRepository.save(Todo.builder()
//                    .content("오늘 할 일 " + index)
//                    .createdDateTime(LocalDateTime.now())
//                    .isComplete(false)
//                    .build()
//            ));
//        };
//    }
}
