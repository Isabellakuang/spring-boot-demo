import { axiosClient } from '../../../core/api/axiosClient';
import { API_ROUTES } from '../../../core/constants/routes';
import type { Faq } from '../types/faq';

export async function fetchFaqs(): Promise<Faq[]> {
  const { data } = await axiosClient.get<Faq[]>(API_ROUTES.KNOWLEDGE_FAQ);
  return data;
}

export async function searchFaq(query: string): Promise<Faq | null> {
  const { data } = await axiosClient.get<Faq | null>(
    API_ROUTES.KNOWLEDGE_SEARCH,
    {
      params: { query }
    }
  );
  return data;
}