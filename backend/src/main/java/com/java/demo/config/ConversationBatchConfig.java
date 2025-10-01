@Configuration
@EnableBatchProcessing
public class ConversationBatchConfig {

    @Bean
    public Job conversationImportJob(JobRepository jobRepository,
                                     JobCompletionNotificationListener listener,
                                     Step conversationStep) {
        return new JobBuilder("conversationImportJob", jobRepository)
            .listener(listener)
            .start(conversationStep)
            .build();
    }

    @Bean
    public Step conversationStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 ItemReader<ConversationCsv> reader,
                                 ItemProcessor<ConversationCsv, Conversation> processor,
                                 ItemWriter<Conversation> writer) {
        return new StepBuilder("conversationStep", jobRepository)
            .<ConversationCsv, Conversation>chunk(100)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .transactionManager(transactionManager)
            .build();
    }
}