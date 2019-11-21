package com.example.demo.controller;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/loadData")
public class LoadController {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job fileBatchJob;

    @Autowired
    Job dbBatchJob;

    @GetMapping("/db")
    public BatchStatus loadDataToDb() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        Map<String, JobParameter> map = new HashMap<>();
        map.put("time", new JobParameter(System.currentTimeMillis()));

        JobParameters parameters = new JobParameters(map);

        JobExecution exe = jobLauncher.run(dbBatchJob, parameters);
        System.out.println("Job Execution" + exe.getStatus());

        while (exe.isRunning()) {
            System.out.println("Running");
        }

        return exe.getStatus();
    }

    @GetMapping("/file")
    public BatchStatus loadDataToFile() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        Map<String, JobParameter> map = new HashMap<>();
        map.put("time", new JobParameter(System.currentTimeMillis()));

        JobParameters parameters = new JobParameters(map);

        JobExecution exe = jobLauncher.run(fileBatchJob, parameters);
        System.out.println("Job Execution" + exe.getStatus());

        while (exe.isRunning()) {
            System.out.println("Running");
        }

        return exe.getStatus();
    }
}
