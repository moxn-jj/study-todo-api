package com.mun.todo.batch.job;

import javax.persistence.EntityManagerFactory;

import com.mun.todo.entity.RefreshToken;
import com.mun.todo.repository.RefreshTokenRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 리프레시 토큰을 삭제하는 배치
 * Chunk 방식으로 처리
 *
 * 구현하고 싶은 기능이 특정 시간 이전에 생성된 데이터를 삭제하는 것이라서
 * chunk 방식보다 tasklet으로 처리하는 것이 효율적으로 보임
 * 애초에 chunk 방식으로 구현하려고 했던 이유는 chunk 방식으로 배치를 구현해보고 싶었기 때문
 */
@Slf4j
@Configuration // spring batch의 job은 Configuration으로 등록
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = "refreshTokenChunkJob")
public class RefreshTokenChunkJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 빌더로 초기화
     * @return
     * @throws Exception
     */
    @Bean
    public Job refreshTokenChunkJob() throws Exception {
        return jobBuilderFactory.get("refreshTokenChunkJob") // refreshTokenChunkJob이라는 이름으로 batch job 생성
                .start(refreshTokenChunkStep1(null)) // job 안에는 여러 step이 존재함, step 안에는 tasklet이나 reader, processor, writer 묶음이 존재함
                .next(refreshTokenChunkStep2()) // next()를 이용하여 step을 순차적으로 처리할 수 있음
                .build();
    }

    @Bean
    @JobScope
    public Step refreshTokenChunkStep1(@Value("#{jobParameters[requestDate]}") String requestDate) throws Exception {

        log.info("▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ refreshTokenChunkStep1 value : " + requestDate + " ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");

        return stepBuilderFactory.get("refreshTokenChunkStep1") // refreshTokenChunkStep이라는 이름으로 batch step 생성
                .<RefreshToken, RefreshToken> chunk(10)
                .reader(reader(null))
                .processor(processor(null))
                .writer(writer(null))
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<RefreshToken> reader(@Value("#{jobParameters[requestDate]}") String requestDate) throws Exception {

        log.info("▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ reader value : " + requestDate + " ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");

        LocalDateTime oneMinutesAgo = LocalDateTime.now().minusMinutes(1);

        return new JpaPagingItemReaderBuilder<RefreshToken>()
                .pageSize(10)
                .queryString("SELECT r FROM RefreshToken r WHERE e.modifiedAt <= :oneMinutesAgo")
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
    public ItemWriter<RefreshToken> writer(@Value("#{jobParameters[requestDate]}") String requestDate){

        log.info("▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ writer value : " + requestDate + " ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");

        return items -> {
            for (RefreshToken item : items) {

                log.info("▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ delete value : " + item.getValue() + " ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");
                refreshTokenRepository.deleteRefreshTokensByKey(item.getKey());
            }
        };
    }

/*    @Bean
    @StepScope
    public JpaItemWriter<RefreshToken> writer(@Value("#{jobParameters[requestDate]}") String requestDate){

        log.info("▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ writer value : " + requestDate + " ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");

        return new JpaItemWriterBuilder<RefreshToken>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }*/

    /**
     * 단순 테스트를 위한 메소드
     * @return
     */
    @Bean
    public Step refreshTokenChunkStep2() {
        return stepBuilderFactory.get("refreshTokenChunkStep2")
                .tasklet((contribution, chunkContext) -> {
                    log.info("▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ refreshTokenChunkStep2 is running ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
