package com.mun.todo.batch;

import com.mun.todo.batch.job.RefreshTokenChunkJobConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringRunner.class)
@SpringBatchTest // (1)
@SpringBootTest(classes={RefreshTokenChunkJobConfig.class, TestBatchConfig.class}) // (2)
public class BatchIntegrationTestJobConfigurationNewTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils; // 해당 빨간 줄은 그냥 intellij 에러..

    @Test
    public void JobParameter생성시점() throws Exception {

        //given
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addString("requestDate", "20180817");

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(builder.toJobParameters());

        //then
        Assert.assertThat(jobExecution.getStatus(), is(BatchStatus.COMPLETED));
    }
}