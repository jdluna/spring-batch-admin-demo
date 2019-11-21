package com.example.demo.admin;

import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
// Can't use @EnableBatchProcessing because there will be a conflict with the beans that are already declared in Spring Batch Admin XML webapp config
// This resource will remove data-source-context.xml from imports to avoid circular dependency
@ImportResource({"classpath:batch-admin-webapp-config-override.xml"})
public class AdminConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public AdminConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public JobRepository jobRepository(MapJobRepositoryFactoryBean factoryBean) throws Exception{
//        return (JobRepository) factoryBean.getObject();
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
//        factory.setTransactionManager(transactionManager);
//        factory.setIsolationLevelForCreate("ISOLATION_REPEATABLE_READ");
        factory.setTransactionManager(transactionManager);
        factory.afterPropertiesSet();
        factory.setIsolationLevelForCreate("ISOLATION_DEFAULT");
        return factory.getObject();
    }

    @Bean
    public JobBuilderFactory jobBuilderFactory() {
        return new JobBuilderFactory(jobRepository);
    }

    @Bean
    public StepBuilderFactory stepBuilderFactory() {
        return new StepBuilderFactory(jobRepository, transactionManager);
    }

    @Bean
    public JobParametersIncrementer jobParametersIncrementer() {
        return new RunIdIncrementer();
    }

    /*
     * This method will trigger Admin UI in localhost
     */
    @Bean
    public ServletRegistrationBean adminServletRegistrationBean() {
        XmlWebApplicationContext applicationContext = new XmlWebApplicationContext();
        applicationContext.setConfigLocation("classpath:/org/springframework/batch/admin/web/resources/servlet-config.xml");

        DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);

        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet, "/*");
        servletRegistrationBean.setName("batch-admin");

        return servletRegistrationBean;
    }
}
