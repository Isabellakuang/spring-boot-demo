import { create } from 'zustand';

export type ToastType = 'info' | 'success' | 'error';

export interface Toast {
  id: string;
  message: string;
  type: ToastType;
  duration: number;
  createdAt: number;
}

interface AppState {
  activeConversationId?: number;
  setActiveConversationId: (id?: number) => void;
  toasts: Toast[];
  addToast: (
    message: string,
    type?: ToastType,
    options?: { duration?: number }
  ) => string;
  removeToast: (id: string) => void;
  clearToasts: () => void;
}

const generateId = () => Math.random().toString(36).slice(2, 10);

export const useAppStore = create<AppState>((set) => ({
  activeConversationId: undefined,
  setActiveConversationId: (id) => set({ activeConversationId: id }),
  toasts: [],
  addToast: (message, type = 'info', options) => {
    const id = generateId();
    const toast: Toast = {
      id,
      message,
      type,
      duration: options?.duration ?? 4000,
      createdAt: Date.now()
    };
    set((state) => ({ toasts: [...state.toasts, toast] }));
    return id;
  },
  removeToast: (id) =>
    set((state) => ({
      toasts: state.toasts.filter((toast) => toast.id !== id)
    })),
  clearToasts: () => set({ toasts: [] })
}));

export const selectToasts = (state: AppState) => state.toasts;
export const selectRemoveToast = (state: AppState) => state.removeToast;
export const selectActiveConversationId = (state: AppState) =>
  state.activeConversationId;
export const selectSetActiveConversationId = (state: AppState) =>
  state.setActiveConversationId;