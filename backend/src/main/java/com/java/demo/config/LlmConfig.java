// LlmConfig.java
@Configuration
@ConfigurationProperties(prefix = "demo.llm")
public class LlmConfig {
    private String openaiApiKey;
    private String openaiModel = "gpt-4o-mini";
    private String pineconeApiKey;
    private String pineconeEnvironment;
    private String pineconeIndex;

    // getters/setters
}