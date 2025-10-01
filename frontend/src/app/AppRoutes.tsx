import { Route, Routes } from 'react-router-dom';
import Layout from '../shared/components/Layout';
import KnowledgePage from '../features/knowledge/pages/KnowledgePage';
import ConversationPage from '../features/conversations/pages/ConversationPage';

export function AppRoutes() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route
          path="/"
          element={
            <div className="grid gap-6 lg:grid-cols-[1fr_1.2fr]">
              <KnowledgePage />
              <ConversationPage />
            </div>
          }
        />
        <Route path="/knowledge" element={<KnowledgePage />} />
        <Route path="/conversations" element={<ConversationPage />} />
      </Route>
    </Routes>
  );
}