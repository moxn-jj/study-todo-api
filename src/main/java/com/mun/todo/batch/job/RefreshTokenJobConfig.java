package com.mun.todo.batch.job;

import javax.persistence.EntityManagerFactory;

import com.mun.todo.entity.RefreshToken;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration // spring batch의 job은 Configuration으로 등록
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = "refreshTokenJob")
public class RefreshTokenJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    /**
     * 빌더로 초기화
     * @return
     * @throws Exception
     */
    @Bean
    public Job refreshTokenJob() throws Exception {
        return jobBuilderFactory.get("refreshTokenJob") // refreshTokenJob이라는 이름으로 batch job 생성
                .start(simpleStep1(null)) // job 안에는 여러 step이 존재함, step 안에는 tasklet이나 reader, processor, writer 묶음이 존재함
                .next(simpleStep2()) // next()를 이용하여 step을 순차적으로 처리할 수 있음
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate) throws Exception {

        log.info("▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ simpleStep1 value : " + requestDate + " ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");

        return stepBuilderFactory.get("simpleStep1") // simpleStep1이라는 이름으로 batch step 생성
                .<RefreshToken, RefreshToken> chunk(10)
                .reader(reader(null))
                .processor(processor(null))
                .writer(writer(null))
                .build();

        /*return stepBuilderFactory.get("simpleStep1")
                // tasklet은 step 안에서 단일로 수행될 기능을 선언할 때 사용함
                .tasklet(new RefreshTokenTasklet(refreshTokenRepository)) // Step 안에서 수행될 기능들을 명시
                .build();*/
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<RefreshToken> reader(@Value("#{jobParameters[requestDate]}") String requestDate) throws Exception {

        log.info("▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ reader value : " + requestDate + " ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");

        return new JpaPagingItemReaderBuilder<RefreshToken>()
                .pageSize(10)
                .queryString("SELECT r FROM RefreshToken r")
                .entityManagerFactory(entityManagerFactory)
                .name("JpaPagingItemReader")
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<RefreshToken, RefreshToken> processor(@Value("#{jobParameters[requestDate]}") String requestDate){

        log.info("▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ processor value : " + requestDate + " ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");

        return new ItemProcessor<RefreshToken, RefreshToken>() {
            @Override
            public RefreshToken process(RefreshToken refreshToken) throws Exception {

                log.info("==> processor refreshToken : " + refreshToken);
                log.info("==> processor value : " + requestDate);

                return refreshToken;
            }
        };
    }

    @Bean
    @StepScope
    public JpaItemWriter<RefreshToken> writer(@Value("#{jobParameters[requestDate]}") String requestDate){

        log.info("▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ writer value : " + requestDate + " ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");

        return new JpaItemWriterBuilder<RefreshToken>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Step simpleStep2() {
        return stepBuilderFactory.get("simpleStep2")
                .tasklet((contribution, chunkContext) -> {
                    log.info("▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ simpleStep2 is running ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
