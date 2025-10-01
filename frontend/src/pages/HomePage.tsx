import { Link } from 'react-router-dom'
import { MessageSquare, BookOpen, TrendingUp } from 'lucide-react'

export default function HomePage() {
  return (
    <div className="px-4 py-8">
      <div className="text-center mb-12">
        <h1 className="text-4xl font-bold text-gray-900 mb-4">
          Welcome to Customer Support System
        </h1>
        <p className="text-xl text-gray-600 max-w-2xl mx-auto">
          Enterprise-grade customer support with intelligent FAQ matching and conversation management
        </p>
      </div>

      <div className="grid md:grid-cols-3 gap-8 max-w-5xl mx-auto">
        <Link
          to="/conversations"
          className="bg-white p-6 rounded-lg shadow-md hover:shadow-lg transition-shadow border border-gray-200"
        >
          <div className="flex items-center justify-center w-12 h-12 bg-primary-100 rounded-lg mb-4">
            <MessageSquare className="w-6 h-6 text-primary-600" />
          </div>
          <h2 className="text-xl font-semibold text-gray-900 mb-2">
            Conversations
          </h2>
          <p className="text-gray-600">
            Manage customer conversations with real-time messaging and intelligent responses
          </p>
        </Link>

        <Link
          to="/faqs"
          className="bg-white p-6 rounded-lg shadow-md hover:shadow-lg transition-shadow border border-gray-200"
        >
          <div className="flex items-center justify-center w-12 h-12 bg-primary-100 rounded-lg mb-4">
            <BookOpen className="w-6 h-6 text-primary-600" />
          </div>
          <h2 className="text-xl font-semibold text-gray-900 mb-2">
            Knowledge Base
          </h2>
          <p className="text-gray-600">
            Browse and search FAQs with intelligent text similarity matching
          </p>
        </Link>

        <div className="bg-white p-6 rounded-lg shadow-md border border-gray-200">
          <div className="flex items-center justify-center w-12 h-12 bg-primary-100 rounded-lg mb-4">
            <TrendingUp className="w-6 h-6 text-primary-600" />
          </div>
          <h2 className="text-xl font-semibold text-gray-900 mb-2">
            Analytics
          </h2>
          <p className="text-gray-600">
            Track performance metrics and customer satisfaction (Coming Soon)
          </p>
        </div>
      </div>

      <div className="mt-16 bg-primary-50 rounded-lg p-8 max-w-4xl mx-auto">
        <h2 className="text-2xl font-bold text-gray-900 mb-4">
          Key Features
        </h2>
        <ul className="space-y-3 text-gray-700">
          <li className="flex items-start">
            <span className="text-primary-600 mr-2">✓</span>
            <span>Real-time conversation management with message history</span>
          </li>
          <li className="flex items-start">
            <span className="text-primary-600 mr-2">✓</span>
            <span>Intelligent FAQ matching using text similarity algorithms</span>
          </li>
          <li className="flex items-start">
            <span className="text-primary-600 mr-2">✓</span>
            <span>Redis caching for improved performance</span>
          </li>
          <li className="flex items-start">
            <span className="text-primary-600 mr-2">✓</span>
            <span>RESTful API with OpenAPI documentation</span>
          </li>
          <li className="flex items-start">
            <span className="text-primary-600 mr-2">✓</span>
            <span>Enterprise-grade monitoring and health checks</span>
          </li>
        </ul>
      </div>
    </div>
  )
}
