package com.example.demo.job;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class Task implements Tasklet {

    Logger logger = LogManager.getLogger(Task.class);


    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        logger.info("Task");
        new Task2().execute(stepContribution, chunkContext);
        return RepeatStatus.FINISHED;
    }
}
