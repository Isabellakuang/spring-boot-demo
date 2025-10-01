import { AppProviders } from './providers';
import { AppRoutes } from './AppRoutes';
import { ErrorBoundary } from '../core/utils/errorBoundary';

function App() {
  return (
    <AppProviders>
      <ErrorBoundary>
        <AppRoutes />
      </ErrorBoundary>
    </AppProviders>
  );
}

export default App;