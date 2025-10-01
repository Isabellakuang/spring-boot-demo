export interface LlmRequest {
  prompt: string;
  model?: string;
  provider?: 'openai' | 'claude';
  temperature?: number;
  maxTokens?: number;
}

export interface LlmResponse {
  success: boolean;
  content: string;
  model: string;
  usage?: {
    promptTokens?: number;
    completionTokens?: number;
    totalTokens?: number;
    inputTokens?: number;
    outputTokens?: number;
  };
  error?: string;
  generatedAt: string;
}

export interface LlmModel {
  id: string;
  name: string;
  provider: string;
  description: string;
}