import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import ConversationStarter from '../../features/conversations/components/ConversationStarter';

describe('ConversationStarter', () => {
  it('submits form with user input', async () => {
    const user = userEvent.setup();
    const handleSubmit = vi.fn();

    render(<ConversationStarter onSubmit={handleSubmit} loading={false} />);

    await user.type(screen.getByLabelText(/Subject/i), 'Flight change');
    await user.type(
      screen.getByLabelText(/Customer Email/i),
      'user@example.com'
    );
    await user.type(
      screen.getByLabelText(/Initial Message/i),
      'Need help with my booking'
    );

    await user.click(screen.getByRole('button', { name: /Start Conversation/i }));

    expect(handleSubmit).toHaveBeenCalledWith({
      subject: 'Flight change',
      customerEmail: 'user@example.com',
      initialMessage: 'Need help with my booking'
    });
  });
});