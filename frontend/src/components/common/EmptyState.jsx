import React from 'react';
import { FileQuestion } from 'lucide-react';
import Button from './Button';

const EmptyState = ({
  icon: Icon = FileQuestion,
  title,
  description,
  action,
  actionLabel,
  onAction,
}) => {
  return (
    <div className="flex flex-col items-center justify-center py-12 px-4 text-center">
      <div className="w-16 h-16 mb-4 rounded-full bg-gray-100 dark:bg-gray-800 flex items-center justify-center">
        <Icon className="w-8 h-8 text-gray-400 dark:text-gray-600" />
      </div>
      
      {title && (
        <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
          {title}
        </h3>
      )}
      
      {description && (
        <p className="text-sm text-gray-600 dark:text-gray-400 max-w-md mb-6">
          {description}
        </p>
      )}
      
      {action && actionLabel && onAction && (
        <Button onClick={onAction} icon={action}>
          {actionLabel}
        </Button>
      )}
    </div>
  );
};

export default EmptyState;
