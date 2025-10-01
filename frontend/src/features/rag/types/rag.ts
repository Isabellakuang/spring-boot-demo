export interface RagQueryRequest {
  question: string;
  topK?: number;
  llmProvider?: 'openai' | 'claude';
  includeSources?: boolean;
}

export interface RagSource {
  id: string;
  document?: string;
  metadata?: Record<string, any>;
  score?: number;
  distance?: number;
}

export interface RagQueryResponse {
  answer: string;
  sources: RagSource[];
  confidence: number;
  usage?: Record<string, any>;
  queriedAt: string;
}

export interface DocumentIndexRequest {
  document: string;
  metadata: Record<string, any>;
  docId: string;
}