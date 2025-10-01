import React, { useState } from 'react';
import { useMutation, useQuery } from '@tanstack/react-query';
import { ragApi } from '../services/ragApi';
import type { RagQueryRequest } from '../types/rag';

export const RagPage: React.FC = () => {
  const [question, setQuestion] = useState('');
  const [topK, setTopK] = useState(5);

  const queryMutation = useMutation({
    mutationFn: (request: RagQueryRequest) => ragApi.query(request),
  });

  const statsQuery = useQuery({
    queryKey: ['rag-stats'],
    queryFn: ragApi.getStats,
  });

  const refreshMutation = useMutation({
    mutationFn: ragApi.refreshIndex,
    onSuccess: () => {
      alert('Index refreshed successfully!');
      statsQuery.refetch();
    },
  });

  const handleQuery = () => {
    queryMutation.mutate({
      question,
      topK,
      llmProvider: 'openai',
      includeSources: true,
    });
  };

  return (
    <div className="max-w-6xl mx-auto p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">RAG Knowledge Base</h1>
        <button
          onClick={() => refreshMutation.mutate()}
          disabled={refreshMutation.isPending}
          className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 disabled:bg-gray-400"
        >
          {refreshMutation.isPending ? 'Refreshing...' : 'Refresh Index'}
        </button>
      </div>

      {/* Stats Card */}
      {statsQuery.data && (
        <div className="grid grid-cols-4 gap-4 mb-6">
          <div className="bg-white p-4 rounded-lg shadow">
            <p className="text-gray-600 text-sm">Total Documents</p>
            <p className="text-2xl font-bold">{statsQuery.data.totalDocuments}</p>
          </div>
          <div className="bg-white p-4 rounded-lg shadow">
            <p className="text-gray-600 text-sm">Total Vectors</p>
            <p className="text-2xl font-bold">{statsQuery.data.totalVectors}</p>
          </div>
          <div className="bg-white p-4 rounded-lg shadow">
            <p className="text-gray-600 text-sm">Total Queries</p>
            <p className="text-2xl font-bold">{statsQuery.data.totalQueries}</p>
          </div>
          <div className="bg-white p-4 rounded-lg shadow">
            <p className="text-gray-600 text-sm">Avg Confidence</p>
            <p className="text-2xl font-bold">
              {(statsQuery.data.averageConfidence * 100).toFixed(1)}%
            </p>
          </div>
        </div>
      )}

      {/* Query Interface */}
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-xl font-semibold mb-4">Ask a Question</h2>

        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Question
            </label>
            <textarea
              value={question}
              onChange={(e) => setQuestion(e.target.value)}
              placeholder="What would you like to know?"
              className="w-full px-4 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500"
              rows={3}
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Top K Results: {topK}
            </label>
            <input
              type="range"
              min="1"
              max="10"
              value={topK}
              onChange={(e) => setTopK(parseInt(e.target.value))}
              className="w-full"
            />
          </div>

          <button
            onClick={handleQuery}
            disabled={!question || queryMutation.isPending}
            className="w-full bg-blue-600 text-white py-3 rounded hover:bg-blue-700 disabled:bg-gray-400"
          >
            {queryMutation.isPending ? 'Searching...' : 'Search'}
          </button>
        </div>

        {/* Answer Display */}
        {queryMutation.data && (
          <div className="mt-6 space-y-4">
            <div className="p-4 bg-blue-50 rounded border border-blue-200">
              <div className="flex justify-between items-start mb-2">
                <h3 className="text-lg font-semibold">Answer:</h3>
                <span className="text-sm text-gray-600">
                  Confidence: {(queryMutation.data.confidence * 100).toFixed(1)}%
                </span>
              </div>
              <p className="text-gray-800 whitespace-pre-wrap">
                {queryMutation.data.answer}
              </p>
            </div>

            {/* Sources */}
            {queryMutation.data.sources.length > 0 && (
              <div>
                <h3 className="text-lg font-semibold mb-2">Sources:</h3>
                <div className="space-y-2">
                  {queryMutation.data.sources.map((source, index) => (
                    <div
                      key={source.id || index}
                      className="p-3 bg-gray-50 rounded border border-gray-200"
                    >
                      <div className="flex justify-between items-start">
                        <p className="text-sm font-medium text-gray-700">
                          Source {index + 1}
                        </p>
                        <span className="text-xs text-gray-500">
                          Score: {source.score?.toFixed(3) || 
                                  (1 - (source.distance || 0)).toFixed(3)}
                        </span>
                      </div>
                      <p className="text-sm text-gray-600 mt-1">
                        {source.document || 
                         JSON.stringify(source.metadata, null, 2)}
                      </p>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};