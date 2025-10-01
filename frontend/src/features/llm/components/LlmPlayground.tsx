import React, { useState } from 'react';
import { useLlm } from '../hooks/useLlm';
import type { LlmRequest } from '../types/llm';

export const LlmPlayground: React.FC = () => {
  const { generate, isGenerating, generateData, models } = useLlm();
  
  const [prompt, setPrompt] = useState('');
  const [provider, setProvider] = useState<'openai' | 'claude'>('openai');
  const [model, setModel] = useState('gpt-4-turbo-preview');
  const [temperature, setTemperature] = useState(0.7);

  const handleGenerate = () => {
    const request: LlmRequest = {
      prompt,
      provider,
      model,
      temperature,
      maxTokens: 1000,
    };
    generate(request);
  };

  return (
    <div className="max-w-4xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">LLM Playground</h1>

      <div className="bg-white rounded-lg shadow p-6 space-y-6">
        {/* Provider Selection */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Provider
          </label>
          <div className="flex gap-4">
            <button
              onClick={() => setProvider('openai')}
              className={`px-4 py-2 rounded ${
                provider === 'openai'
                  ? 'bg-blue-600 text-white'
                  : 'bg-gray-200 text-gray-700'
              }`}
            >
              OpenAI
            </button>
            <button
              onClick={() => setProvider('claude')}
              className={`px-4 py-2 rounded ${
                provider === 'claude'
                  ? 'bg-purple-600 text-white'
                  : 'bg-gray-200 text-gray-700'
              }`}
            >
              Claude
            </button>
          </div>
        </div>

        {/* Model Selection */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Model
          </label>
          <select
            value={model}
            onChange={(e) => setModel(e.target.value)}
            className="w-full px-4 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500"
          >
            {models?.[provider]?.map((m: any) => (
              <option key={m} value={m}>
                {m}
              </option>
            ))}
          </select>
        </div>

        {/* Temperature */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Temperature: {temperature}
          </label>
          <input
            type="range"
            min="0"
            max="1"
            step="0.1"
            value={temperature}
            onChange={(e) => setTemperature(parseFloat(e.target.value))}
            className="w-full"
          />
        </div>

        {/* Prompt Input */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Prompt
          </label>
          <textarea
            value={prompt}
            onChange={(e) => setPrompt(e.target.value)}
            placeholder="Enter your prompt here..."
            className="w-full px-4 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500"
            rows={6}
          />
        </div>

        {/* Generate Button */}
        <button
          onClick={handleGenerate}
          disabled={!prompt || isGenerating}
          className="w-full bg-blue-600 text-white py-3 rounded hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed"
        >
          {isGenerating ? 'Generating...' : 'Generate'}
        </button>

        {/* Response */}
        {generateData && (
          <div className="mt-6 p-4 bg-gray-50 rounded border border-gray-200">
            <h3 className="text-lg font-semibold mb-2">Response:</h3>
            <p className="whitespace-pre-wrap text-gray-800">
              {generateData.content}
            </p>
            
            {generateData.usage && (
              <div className="mt-4 text-sm text-gray-600">
                <p>Tokens used: {generateData.usage.totalTokens || 
                  (generateData.usage.inputTokens + generateData.usage.outputTokens)}</p>
                <p>Model: {generateData.model}</p>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};