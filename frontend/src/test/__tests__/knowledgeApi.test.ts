import { afterEach, describe, expect, it, vi } from 'vitest';
import { axiosClient } from '../../core/api/axiosClient';
import { fetchFaqs, searchFaq } from '../../features/knowledge/services/knowledgeApi';
import type { Faq } from '../../features/knowledge/types/faq';

vi.mock('../../core/api/axiosClient', () => ({
  axiosClient: {
    get: vi.fn()
  }
}));

const mockedAxios = axiosClient as unknown as {
  get: ReturnType<typeof vi.fn>;
};

describe('knowledgeApi', () => {
  afterEach(() => {
    vi.resetAllMocks();
  });

  it('fetches FAQ list', async () => {
    const fakeFaqs: Faq[] = [
      { id: 1, question: 'Q1', answer: 'A1', tags: ['tag1'] }
    ];
    mockedAxios.get.mockResolvedValueOnce({ data: fakeFaqs });

    const result = await fetchFaqs();
    expect(mockedAxios.get).toHaveBeenCalledWith('/knowledge/faq');
    expect(result).toEqual(fakeFaqs);
  });

  it('searches FAQ', async () => {
    const fakeFaq: Faq = {
      id: 2,
      question: 'Q2',
      answer: 'A2',
      tags: ['tag2']
    };
    mockedAxios.get.mockResolvedValueOnce({ data: fakeFaq });

    const result = await searchFaq('sample');
    expect(mockedAxios.get).toHaveBeenCalledWith('/knowledge/faq/search', {
      params: { query: 'sample' }
    });
    expect(result).toEqual(fakeFaq);
  });
});