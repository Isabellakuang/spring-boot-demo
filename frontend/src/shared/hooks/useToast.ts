import { useCallback } from 'react';
import {
  ToastType,
  selectRemoveToast,
  useAppStore
} from '../../core/state/appStore';

export function useToast() {
  const addToast = useAppStore((state) => state.addToast);
  const removeToast = useAppStore(selectRemoveToast);

  const notify = useCallback(
    (message: string, type: ToastType = 'info', options?: { duration?: number }) =>
      addToast(message, type, options),
    [addToast]
  );

  return { notify, removeToast };
}