import { afterEach, describe, expect, it, vi } from 'vitest';
import { axiosClient } from '../../core/api/axiosClient';
import {
  appendMessage,
  createConversation,
  fetchConversation
} from '../../features/conversations/services/conversationApi';

vi.mock('../../core/api/axiosClient', () => ({
  axiosClient: {
    get: vi.fn(),
    post: vi.fn()
  }
}));

const mockedAxios = axiosClient as unknown as {
  get: ReturnType<typeof vi.fn>;
  post: ReturnType<typeof vi.fn>;
};

describe('conversationApi', () => {
  afterEach(() => {
    vi.resetAllMocks();
  });

  it('creates a conversation', async () => {
    mockedAxios.post.mockResolvedValueOnce({ data: { conversationId: 7 } });
    const result = await createConversation({
      subject: 'Test',
      customerEmail: 'test@example.com',
      initialMessage: 'Hello'
    });
    expect(mockedAxios.post).toHaveBeenCalledWith('/conversations', {
      subject: 'Test',
      customerEmail: 'test@example.com',
      initialMessage: 'Hello'
    });
    expect(result).toEqual({ conversationId: 7 });
  });

  it('fetches conversation', async () => {
    mockedAxios.get.mockResolvedValueOnce({
      data: {
        conversationId: 1,
        subject: 'Hello',
        status: 'OPEN',
        startedAt: new Date().toISOString(),
        messages: []
      }
    });
    await fetchConversation(1);
    expect(mockedAxios.get).toHaveBeenCalledWith('/conversations/1');
  });

  it('appends message', async () => {
    mockedAxios.post.mockResolvedValueOnce({ data: {} });
    await appendMessage(3, { sender: 'CUSTOMER', content: 'Hi' });
    expect(mockedAxios.post).toHaveBeenCalledWith('/conversations/3/messages', {
      sender: 'CUSTOMER',
      content: 'Hi'
    });
  });
});