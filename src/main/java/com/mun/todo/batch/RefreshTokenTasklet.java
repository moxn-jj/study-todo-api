package com.mun.todo.batch;

import com.mun.todo.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.LocalDateTime;

/**
 * 배치를 tasklet 방식으로 관리할 때 사용하는 클래스
 * BatchConfig 쪽에 아래와 같이 설정해주면 동작함
 * tasklet 방식은 단일 작업 기반으로 처리하는 것이 효율적일 때 사용
 * 대용량 처리에는 적합하지 못함. 이 때는 chunk 방식 처리할 것을 권장함
 *
 * return stepBuilderFactory.get("exampleStep")
 *        .tasklet(new RefreshTokenTasklet(refreshTokenRepository))
 *        .build();
 */
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenTasklet implements Tasklet {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        log.info("▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ Start Delete RefreshToken ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");

        Long deleteCnt = refreshTokenRepository.deleteRefreshTokensByModifiedAtBefore(LocalDateTime.now().minusMinutes(30));

        log.info("▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ End Delete RefreshToken : " + deleteCnt + " ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");
        return RepeatStatus.FINISHED;
    }
}
