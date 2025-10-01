import { renderHook, waitFor } from '@testing-library/react';
import { describe, it, vi, afterEach, expect } from 'vitest';
import { createElement } from 'react';
import {
  appendMessage,
  createConversation,
  fetchConversation
} from '../../features/conversations/services/conversationApi';
import { useConversation } from '../../features/conversations/hooks/useConversation';
import {
  QueryClient,
  QueryClientProvider
} from '@tanstack/react-query';

vi.mock('../../features/conversations/services/conversationApi');

const mockedApi = {
  fetchConversation: fetchConversation as unknown as ReturnType<typeof vi.fn>,
  createConversation: createConversation as unknown as ReturnType<typeof vi.fn>,
  appendMessage: appendMessage as unknown as ReturnType<typeof vi.fn>
};

function createWrapper() {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false }
    }
  });

  const Wrapper = ({ children }: { children: React.ReactNode }) =>
    createElement(QueryClientProvider, { client: queryClient }, children);
  
  return Wrapper;
}

describe('useConversation', () => {
  afterEach(() => {
    vi.resetAllMocks();
  });

  it('fetches conversation when ID provided', async () => {
    mockedApi.fetchConversation.mockResolvedValueOnce({
      conversationId: 42,
      subject: 'Test',
      status: 'OPEN',
      startedAt: new Date().toISOString(),
      messages: []
    });

    const { result } = renderHook(() => useConversation(42), {
      wrapper: createWrapper()
    });

    await waitFor(() =>
      expect(result.current.conversationQuery.isSuccess).toBe(true)
    );
    expect(mockedApi.fetchConversation).toHaveBeenCalledWith(42);
  });

  it('creates conversation via mutation', async () => {
    mockedApi.createConversation.mockResolvedValueOnce({ conversationId: 11 });

    const { result } = renderHook(() => useConversation(), {
      wrapper: createWrapper()
    });

    const response = await result.current.createConversationMutation.mutateAsync(
      {
        subject: 'Subject',
        customerEmail: 'user@example.com',
        initialMessage: 'Hello'
      }
    );

    expect(response).toEqual({ conversationId: 11 });
    expect(mockedApi.createConversation).toHaveBeenCalled();
  });
});
