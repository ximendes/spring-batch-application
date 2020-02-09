package com.example.batch.batch_application.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.batch.core.BatchStatus.*;
@Slf4j
@Component
public class CustomJobListener extends JobExecutionListenerSupport {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == STARTED) {
            log.info("ORDER BATCH PROCESS STARTED...!");
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == COMPLETED) {
            log.info("ORDER BATCH PROCESS COMPLETED...!");
        }
    }
}
