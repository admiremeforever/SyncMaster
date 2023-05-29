package com.infybuzz.config;

import java.io.IOException;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;


import com.infybuzz.processor.FirstItemProcessor;
import com.infybuzz.reader.FirstItemReader;
import com.infybuzz.source.entity.Student;


@Configuration
public class SampleJob {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private FirstItemReader firstItemReader;

	@Autowired
	private FirstItemProcessor firstItemProcessor;



	// @Qualifier used here because we have same types of 2 beans i.e
	// EntityManagerFactory here
	@Autowired
	@Qualifier("postgresqlEntityManagerFactory")
	private EntityManagerFactory postgresqlEntityManagerFactory;

	@Autowired
	@Qualifier("mysqlEntityManagerFactory")
	private EntityManagerFactory mysqlEntityManagerFactory;

	/*
	 * @Autowired private StudentService studentService;
	 */

//	@Autowired
//	private SkipListener skipListener;
//
//	@Autowired
//	private SkipListenerImpl skipListenerImpl;

	@Autowired
	@Qualifier("datasource")
	private DataSource datasource;

	@Autowired
	@Qualifier("universitydatasource")
	private DataSource universitydatasource;

	@Autowired
	@Qualifier("postgresdatasource")
	private DataSource postgresdatasource;
	
	@Autowired
	private JpaTransactionManager jpaTransactionManager;

	@Bean
	public Job chunkJob() {
		return jobBuilderFactory.get("Chunk Job").incrementer(new RunIdIncrementer()).start(firstChunkStep()).build();
	}

	private Step firstChunkStep() {
		return stepBuilderFactory.get("First Chunk Step")
				.<Student, com.infybuzz.destination.etity.Student>chunk(3)
				.reader(jpaCursorItemReader())
				.processor(firstItemProcessor)
				.writer(jpaItemWriter()).faultTolerant()
				//.skip(Throwable.class)
				//.skipLimit(100)
				//.retryLimit(3).retry(Throwable.class)
				//.listener(skipListenerImpl)
				.transactionManager(jpaTransactionManager)
				.build();
	}


	public JpaCursorItemReader<Student> jpaCursorItemReader() {
		JpaCursorItemReader<Student> jpaCursorItemReader = new JpaCursorItemReader<Student>();
		jpaCursorItemReader.setEntityManagerFactory(postgresqlEntityManagerFactory);
		jpaCursorItemReader.setQueryString("From Student");
		return jpaCursorItemReader;
	}

	public JpaItemWriter<com.infybuzz.destination.etity.Student> jpaItemWriter() {
		JpaItemWriter<com.infybuzz.destination.etity.Student> jpaItemWriter = new JpaItemWriter<com.infybuzz.destination.etity.Student>();
		jpaItemWriter.setEntityManagerFactory(mysqlEntityManagerFactory);
		return jpaItemWriter;
	}

}
