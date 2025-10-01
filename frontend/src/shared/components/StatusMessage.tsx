interface StatusMessageProps {
  type?: 'info' | 'error' | 'success';
  message: string;
}

const classes: Record<NonNullable<StatusMessageProps['type']>, string> = {
  info: 'text-cyan-300',
  error: 'text-amber-400',
  success: 'text-emerald-300'
};

function StatusMessage({ type = 'info', message }: StatusMessageProps) {
  return (
    <p className={`${classes[type]} text-sm font-medium`} role="status">
      {message}
    </p>
  );
}

export default StatusMessage;