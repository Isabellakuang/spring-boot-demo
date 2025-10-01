// LlmProxyController.java
@RestController
@RequestMapping("/api/llm")
public class LlmProxyController {

    private final LlmService llmService;

    public LlmProxyController(LlmService llmService) {
        this.llmService = llmService;
    }

    @PostMapping("/chat")
    public Mono<LlmResponse> chat(@Valid @RequestBody LlmRequest request) {
        return llmService.chat(request);
    }
}