package com.mun.todo.batch.job;

import com.mun.todo.batch.RefreshTokenTasklet;
import com.mun.todo.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 리프레시 토큰을 삭제하는 배치
 * tasklet 방식으로 처리
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = "refreshTokenTaskletJob")
public class RefreshTokenTaskletJobConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final RefreshTokenRepository refreshTokenRepository;

    @Bean
    public Job refreshTokenTaskletJob() {

        log.info("▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ refreshTokenTaskletJob ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");

        return jobBuilderFactory.get("refreshTokenTaskletJob")
                .start(refreshTokenTaskletStep1())
                .next(refreshTokenTaskletStep2())
                .build();
    }

    /**
     * 시간이 지난 리프레시 토큰은 삭제
     * @return
     */
    @Bean
    public Step refreshTokenTaskletStep1() {

        log.info("▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ refreshTokenTaskletStep1 ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");

        return stepBuilderFactory.get("refreshTokenTaskletStep1")
                // tasklet은 step 안에서 단일로 수행될 기능을 선언할 때 사용함
                .tasklet(new RefreshTokenTasklet(refreshTokenRepository)) // Step 안에서 수행될 기능들을 명시
                .build();
    }

    /**
     * 오직 테스트를 위한 메소드
     * @return
     */
    @Bean
    public Step refreshTokenTaskletStep2() {
        return stepBuilderFactory.get("step2")
                .tasklet((contribution, chunkContext) -> {
                    log.info("▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ refreshTokenTaskletStep2 ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}