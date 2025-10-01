import { useQuery } from '@tanstack/react-query'
import { useState } from 'react'
import { Search, BookOpen } from 'lucide-react'
import { faqsApi } from '../api/faqs'

export default function FaqPage() {
  const [searchQuery, setSearchQuery] = useState('')
  
  const { data: faqs, isLoading, error } = useQuery({
    queryKey: ['faqs'],
    queryFn: faqsApi.getAll,
  })

  const filteredFaqs = faqs?.filter(faq =>
    faq.question.toLowerCase().includes(searchQuery.toLowerCase()) ||
    faq.answer.toLowerCase().includes(searchQuery.toLowerCase())
  )

  if (isLoading) {
    return <div className="text-center py-12">Loading FAQs...</div>
  }

  if (error) {
    return <div className="text-center py-12 text-red-600">Error loading FAQs</div>
  }

  return (
    <div className="px-4">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-4">Knowledge Base</h1>
        <div className="relative max-w-xl">
          <Search className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
          <input
            type="text"
            placeholder="Search FAQs..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
          />
        </div>
      </div>

      <div className="space-y-4">
        {filteredFaqs?.map((faq) => (
          <div key={faq.id} className="bg-white p-6 rounded-lg shadow-md border border-gray-200">
            <div className="flex items-start">
              <BookOpen className="w-5 h-5 text-primary-600 mt-1 mr-3 flex-shrink-0" />
              <div className="flex-1">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">
                  {faq.question}
                </h3>
                <p className="text-gray-700">{faq.answer}</p>
                {faq.category && (
                  <span className="inline-block mt-3 px-3 py-1 bg-primary-100 text-primary-700 text-sm rounded-full">
                    {faq.category}
                  </span>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>

      {filteredFaqs?.length === 0 && (
        <div className="text-center py-12 text-gray-500">
          No FAQs found matching your search.
        </div>
      )}
    </div>
  )
}
