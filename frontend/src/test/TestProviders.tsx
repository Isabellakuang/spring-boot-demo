import { ReactNode } from 'react';
import { MemoryRouter } from 'react-router-dom';
import {
  QueryClient,
  QueryClientProvider
} from '@tanstack/react-query';

interface TestProvidersProps {
  children: ReactNode;
  initialEntries?: string[];
}

export function TestProviders({
  children,
  initialEntries = ['/']
}: TestProvidersProps) {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false }
    }
  });

  return (
    <MemoryRouter initialEntries={initialEntries}>
      <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
    </MemoryRouter>
  );
}