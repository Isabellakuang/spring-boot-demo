import { afterEach, describe, expect, it } from 'vitest';
import { useAppStore } from '../../core/state/appStore';

describe('appStore', () => {
  afterEach(() => {
    useAppStore.setState({
      activeConversationId: undefined,
      toasts: []
    });
  });

  it('sets and clears active conversation id', () => {
    useAppStore.getState().setActiveConversationId(5);
    expect(useAppStore.getState().activeConversationId).toBe(5);
    useAppStore.getState().setActiveConversationId(undefined);
    expect(useAppStore.getState().activeConversationId).toBeUndefined();
  });

  it('adds and removes toast', () => {
    const id = useAppStore.getState().addToast('Hello', 'success');
    expect(useAppStore.getState().toasts).toHaveLength(1);
    useAppStore.getState().removeToast(id);
    expect(useAppStore.getState().toasts).toHaveLength(0);
  });
});