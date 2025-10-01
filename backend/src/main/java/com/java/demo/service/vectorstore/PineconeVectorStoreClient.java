// PineconeVectorStoreClient.java
@Component
public class PineconeVectorStoreClient implements VectorStoreClient {

    private final PineconeClient pineconeClient;
    private final LlmConfig config;
    private final EmbeddingClient embeddingClient;

    public PineconeVectorStoreClient(PineconeClient pineconeClient, LlmConfig config, EmbeddingClient embeddingClient) {
        this.pineconeClient = pineconeClient;
        this.config = config;
        this.embeddingClient = embeddingClient;
    }

    @Override
    public List<VectorDocument> similaritySearch(String query, int topK) {
        float[] embedding = embeddingClient.embed(query);
        return pineconeClient.query(config.getPineconeIndex(), embedding, topK);
    }

    @Override
    public void upsertDocuments(List<VectorDocument> documents) {
        List<VectorRecord> records = documents.stream()
            .map(doc -> new VectorRecord(
                doc.id(),
                embeddingClient.embed(doc.content()),
                Map.of("source", doc.source())
            ))
            .toList();
        pineconeClient.upsert(config.getPineconeIndex(), records);
    }
}