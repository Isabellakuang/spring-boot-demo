import ConversationStarter from '../components/ConversationStarter';
import ConversationPanel from '../components/ConversationPanel';
import { useConversation } from '../hooks/useConversation';
import { useToast } from '../../../shared/hooks/useToast';
import StatusMessage from '../../../shared/components/StatusMessage';
import {
  selectActiveConversationId,
  selectSetActiveConversationId,
  useAppStore
} from '../../../core/state/appStore';

function ConversationPage() {
  const notify = useToast().notify;

  const activeConversationId = useAppStore(selectActiveConversationId);
  const setActiveConversationId = useAppStore(selectSetActiveConversationId);

  const {
    conversationQuery,
    createConversationMutation,
    appendMessageMutation
  } = useConversation(activeConversationId);

  const createConversation = async (
    payload: Parameters<typeof createConversationMutation.mutateAsync>[0]
  ) => {
    try {
      const result = await createConversationMutation.mutateAsync(payload);
      setActiveConversationId(result.conversationId);
      notify('Conversation created successfully.', 'success');
    } catch (error) {
      notify((error as Error).message, 'error');
    }
  };

  const appendMessage = async (message: { sender: string; content: string }) => {
    if (!activeConversationId) return;
    try {
      await appendMessageMutation.mutateAsync({
        conversationId: activeConversationId,
        message
      });
      notify('Message sent.', 'success');
    } catch (error) {
      notify((error as Error).message, 'error');
    }
  };

  const resetConversation = () => {
    setActiveConversationId(undefined);
  };

  return (
    <section className="space-y-5">
      {!activeConversationId && (
        <ConversationStarter
          onSubmit={createConversation}
          loading={createConversationMutation.isPending}
        />
      )}

      {activeConversationId && (
        <ConversationPanel
          conversation={conversationQuery.data}
          loading={conversationQuery.isFetching || appendMessageMutation.isPending}
          onRefresh={() => conversationQuery.refetch()}
          onReset={resetConversation}
          onSendMessage={appendMessage}
        />
      )}

      {conversationQuery.error instanceof Error && (
        <StatusMessage type="error" message={conversationQuery.error.message} />
      )}
    </section>
  );
}

export default ConversationPage;