package com.example.batch.batch_application.configurebatch;

import com.example.batch.batch_application.listener.CustomJobListener;
import com.example.batch.batch_application.mapper.OrderFieldMapper;
import com.example.batch.batch_application.processor.OrderProcessor;
import com.example.batch.batch_application.entity.Order;
import com.example.batch.batch_application.repository.OrderRepository;
import com.example.batch.batch_application.repository.ReferenciaRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import static com.example.batch.batch_application.constants.BatchJobConstant.*;


@Configuration
public class BatchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private OrderFieldMapper fieldMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReferenciaRepository referenciaRepository;

    @Autowired
    private EntityManagerFactory entityManager;

    @Value("${app.csv.fileHeaders}")
    private String[] names;

    @Bean
    public FlatFileItemReader<Order> reader() {
        return new FlatFileItemReaderBuilder<Order>()
                .name(ORDER_ITEM_READER)
                .resource(new ClassPathResource("csv/orders.csv"))
                .linesToSkip(1)
                .delimited()
                .names(names)
                .lineMapper(lineMapper())
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Order>() {{
                    setTargetType(Order.class);
                }})
                .build();
    }

//  reader para consumir os dados da base de dados
//    @Bean
//    public RepositoryItemReader<Order> reader(){
//        RepositoryItemReader<Order> itemReader = new RepositoryItemReader<Order>();
//        itemReader.setRepository(orderRepository);
//        itemReader.setMethodName("findAll");
//        return itemReader;
//    }

    @Bean
    public LineMapper<Order> lineMapper() {
        final DefaultLineMapper<Order> defaultLineMapper = new DefaultLineMapper<>();
        final DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(names);

        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldMapper);

        return defaultLineMapper;
    }

    @Bean
    public OrderProcessor processor() {
        return new OrderProcessor();
    }

// writer para salvar utlizando repository
//    @Bean
//    public RepositoryItemWriter<Order> writer(){
//        RepositoryItemWriter writer = new RepositoryItemWriter();
//        writer.setRepository(orderRepository);
//        writer.setMethodName("saveAndFlush");
//        return writer;
//    }

// writer uitlizando JPA
//    @Bean
//    public JpaItemWriter<Order> writer(){
//        JpaItemWriter writer = new JpaItemWriter();
//        writer.setEntityManagerFactory(entityManager);
//        return writer;
//    }

    // writer uitlizando JDBC
    @Bean
    public JdbcBatchItemWriter<Order> writer(final DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Order>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO orders (order_ref, amount, order_date, note) VALUES (:orderRef, :amount, :orderDate, :note)")
                .dataSource(dataSource)
                .build();
    }



    @Bean
    public Step step(JdbcBatchItemWriter<Order> writer) {
        return stepBuilderFactory.get(BATCH_STEP)
                .<Order, Order>chunk(100)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }



    @Bean
    public Job job(CustomJobListener listener, Step step) {
        return jobBuilderFactory.get(ORDER_PROCESS_JOB)
                                .incrementer(new RunIdIncrementer())
                                .listener(listener)
                                .flow(step)
                                .end()
                                .build();
    }


}
