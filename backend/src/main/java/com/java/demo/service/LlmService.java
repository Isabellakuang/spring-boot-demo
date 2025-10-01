// LlmService.java
@Service
public class LlmService {
    private final WebClient openAiClient;
    private final VectorStoreClient vectorStoreClient;
    private final LlmConfig config;

    public LlmService(WebClient.Builder builder, VectorStoreClient vectorStoreClient, LlmConfig config) {
        this.openAiClient = builder
            .baseUrl("https://api.openai.com/v1")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + config.getOpenaiApiKey())
            .build();
        this.vectorStoreClient = vectorStoreClient;
        this.config = config;
    }

    public Mono<LlmResponse> chat(LlmRequest request) {
        List<VectorDocument> relevantDocs = vectorStoreClient.similaritySearch(request.query(), 4);
        String context = relevantDocs.stream()
            .map(VectorDocument::content)
            .collect(Collectors.joining("\n---\n"));

        return openAiClient.post()
            .uri("/chat/completions")
            .bodyValue(Map.of(
                "model", config.getOpenaiModel(),
                "messages", List.of(
                    Map.of("role", "system", "content", "You are Vera assistant..."),
                    Map.of("role", "user", "content",
                        "Context:\n" + context + "\n\nQuestion:\n" + request.query())
                )))
            .retrieve()
            .bodyToMono(OpenAiResponse.class)
            .map(resp -> new LlmResponse(resp.choices().get(0).message().content(), relevantDocs));
    }
}