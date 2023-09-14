package com.mun.todo;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableBatchProcessing // 배치 기능 활성화
@EnableJpaAuditing // DB 컬럼 생성, 수정 시간 자동 생성
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
