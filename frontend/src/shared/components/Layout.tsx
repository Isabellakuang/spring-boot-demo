import { Outlet } from 'react-router-dom';
import { useEffect } from 'react';
import Navbar from './Navbar';
import {
  selectRemoveToast,
  selectToasts,
  useAppStore
} from '../../core/state/appStore';

function Layout() {
  const toasts = useAppStore(selectToasts);
  const removeToast = useAppStore(selectRemoveToast);

  useEffect(() => {
    const timers = toasts.map((toast) =>
      setTimeout(() => removeToast(toast.id), toast.duration)
    );
    return () => {
      timers.forEach(clearTimeout);
    };
  }, [toasts, removeToast]);

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100">
      <Navbar />
      <main className="mx-auto max-w-6xl px-4 py-8">
        <Outlet />
      </main>
      <div className="fixed bottom-6 right-6 flex max-w-xs flex-col gap-3">
        {toasts.map((toast) => (
          <div
            key={toast.id}
            className="rounded-2xl border border-slate-800 bg-slate-900/90 px-4 py-3 text-sm shadow-2xl"
          >
            <div className="flex items-start justify-between gap-3">
              <span
                className={
                  toast.type === 'success'
                    ? 'text-emerald-300'
                    : toast.type === 'error'
                    ? 'text-amber-400'
                    : 'text-cyan-300'
                }
              >
                {toast.message}
              </span>
              <button
                onClick={() => removeToast(toast.id)}
                className="text-xs text-slate-500 hover:text-slate-300"
              >
                Close
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default Layout;