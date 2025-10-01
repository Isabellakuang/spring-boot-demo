import { render, screen } from '@testing-library/react';
import FaqCard from '../../features/knowledge/components/FaqCard';
import type { Faq } from '../../features/knowledge/types/faq';

const mockFaq: Faq = {
  id: 1,
  question: 'How do I change my flight?',
  answer: 'Use the manage booking portal.',
  tags: ['flight', 'change']
};

describe('FaqCard', () => {
  it('renders FAQ content', () => {
    render(<FaqCard faq={mockFaq} />);
    expect(screen.getByText(/change my flight/i)).toBeInTheDocument();
    expect(screen.getByText(/manage booking portal/i)).toBeInTheDocument();
    expect(screen.getByText('#flight')).toBeInTheDocument();
  });
});