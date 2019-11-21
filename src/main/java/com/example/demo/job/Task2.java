package com.example.demo.job;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;



public class Task2 implements Tasklet {

    Logger logger = LogManager.getLogger(Task2.class);

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        logger.info("Task2");

        return RepeatStatus.FINISHED;
    }
}
