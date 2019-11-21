package com.example.demo.job;

import com.example.demo.loadDataToDbStep.DBProcessor;
import com.example.demo.loadDataToDbStep.DBReader;
import com.example.demo.loadDataToDbStep.DBWriter;
import com.example.demo.loadDataToFileStep.FileProcessor;
import com.example.demo.loadDataToFileStep.FileReader;
import com.example.demo.model.Transaction;
import com.example.demo.model.User;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.io.IOException;

@Configuration
public class BatchJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final JobParametersIncrementer jobParametersIncrementer;

    private Resource outputXml;

    @Value("classpath:work/records.csv")
    Resource inputCsv;

    @Value("classpath:work/users.csv")
    Resource userResource;

    @Autowired
    DBProcessor dBProcessor;

    @Autowired
    DBWriter dBWriter;

    @Autowired
    FileProcessor fileProcessor;

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    JobRegistry jobRegistry;

    @Autowired
    public BatchJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, JobParametersIncrementer jobParametersIncrementer) throws IOException {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobParametersIncrementer = jobParametersIncrementer;

        this.outputXml = new FileSystemResource("classpath:work/output.xml");
    }

    @Bean
    public ItemWriter<Transaction> itemWriter() {
        StaxEventItemWriter<Transaction> itemWriter = new StaxEventItemWriter<>();
        itemWriter.setMarshaller(marshaller());
        itemWriter.setRootTagName("transactionRecord");
        itemWriter.setResource(outputXml);
        return itemWriter;
    }

    private Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Transaction.class);
        return marshaller;
    }

    @Bean
    public JobExecutionListener jobExecutionListener() {
        return new JobExecutionListener() {

            @Override
            public void beforeJob(JobExecution jobExecution) {
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                try {
                    String exitDescription = "XML output has been written in this file: " + outputXml.getFile().getAbsolutePath();
                    jobExecution.setExitStatus(new ExitStatus(jobExecution.getExitStatus().getExitCode(), exitDescription));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Bean
    public Step step1(ItemWriter<Transaction> itemWriter) {
        Step step = stepBuilderFactory
                .get("sampleStep")
                .<Transaction, Transaction>chunk(10)
                .reader(new FileReader(inputCsv))
                .processor(fileProcessor)
                .writer(itemWriter)
                .build();

        return step;
    }

    @Bean
    public Step step2() {
        Step step = stepBuilderFactory.get("file-load")
                .<User, User>chunk(10)
                .reader(new DBReader(userResource))
                .processor(dBProcessor)
                .writer(dBWriter)
                .build();

        return step;
    }

    @Bean
    public Step step3() {
        Step step = stepBuilderFactory.get("file-load")
                .tasklet(new Task())
                .build();

        return step;
    }

    @Bean(name = "fileBatchJob")
    public Job fileJob(ItemWriter<Transaction> itemWriter, JobExecutionListener jobExecutionListener) {

        return jobBuilderFactory
                .get("Load Data to File")
                .incrementer(jobParametersIncrementer)
                .flow(step1(itemWriter))
                .end()
                .listener(jobExecutionListener).build();
    }

    @Bean(name = "dbBatchJob")
    public Job dBJob() {

        return jobBuilderFactory.get("Load Data to Database")
                .incrementer(new RunIdIncrementer())
                .flow(step2())
                .end()
                .build();
    }

    @Bean
    public Flow initCheckFlow(ItemWriter<Transaction> itemWriter) {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("initCheckFlow");

        flowBuilder
                .start(step1(itemWriter))
                .next(step2())
                .end();

        return flowBuilder.build();
    }

    @Bean(name = "mixedJob")
    public Job mixedJob(@Qualifier("initCheckFlow") Flow initCheckFlow) {

        return jobBuilderFactory.get("mixedJob")
                .incrementer(new RunIdIncrementer())
                .start(initCheckFlow)
                .next(step2())
                .end()
                .build();
    }

    @Bean(name = "triggerMixedJob")
    public Job triggerMixedJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

        return jobBuilderFactory.get("triggerMixedJob")
                .incrementer(new RunIdIncrementer())
                .flow(step3())
                .end()
                .build();
    }
}