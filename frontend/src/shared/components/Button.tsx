import type { ButtonHTMLAttributes, PropsWithChildren } from 'react';

type Variant = 'primary' | 'secondary' | 'ghost';

interface ButtonProps
  extends PropsWithChildren<ButtonHTMLAttributes<HTMLButtonElement>> {
  variant?: Variant;
}

const baseClasses =
  'inline-flex items-center justify-center rounded-2xl px-5 py-2 text-sm font-semibold transition focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-slate-950 disabled:cursor-not-allowed disabled:opacity-60';

const variantClasses: Record<Variant, string> = {
  primary:
    'bg-gradient-to-r from-brand-500 to-brand-700 text-white shadow-lg hover:opacity-90 focus:ring-brand-500',
  secondary:
    'bg-slate-800 text-slate-100 hover:bg-slate-700 focus:ring-slate-500',
  ghost:
    'bg-transparent text-slate-200 hover:bg-slate-800/80 focus:ring-slate-400'
};

function Button({
  children,
  className = '',
  variant = 'primary',
  ...rest
}: ButtonProps) {
  const classes = [baseClasses, variantClasses[variant], className]
    .filter(Boolean)
    .join(' ');
  return (
    <button className={classes} {...rest}>
      {children}
    </button>
  );
}

export default Button;