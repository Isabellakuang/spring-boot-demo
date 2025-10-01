import { useParams } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { Send, ArrowLeft } from 'lucide-react'
import { Link } from 'react-router-dom'
import { conversationsApi } from '../api/conversations'
import { format } from 'date-fns'
import type { MessageRequest } from '../types'

export default function ConversationDetailPage() {
  const { id } = useParams<{ id: string }>()
  const queryClient = useQueryClient()
  const [newMessage, setNewMessage] = useState('')

  const { data: conversation, isLoading } = useQuery({
    queryKey: ['conversation', id],
    queryFn: () => conversationsApi.getById(Number(id)),
    enabled: !!id,
  })

  const sendMessageMutation = useMutation({
    mutationFn: (message: MessageRequest) => conversationsApi.addMessage(Number(id), message),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['conversation', id] })
      setNewMessage('')
    },
  })

  const handleSendMessage = (e: React.FormEvent) => {
    e.preventDefault()
    if (newMessage.trim()) {
      sendMessageMutation.mutate({
        sender: 'AGENT',
        content: newMessage,
      })
    }
  }

  if (isLoading) {
    return <div className="text-center py-12">Loading conversation...</div>
  }

  if (!conversation) {
    return <div className="text-center py-12">Conversation not found</div>
  }

  return (
    <div className="px-4 max-w-4xl mx-auto">
      <Link to="/conversations" className="flex items-center text-primary-600 hover:text-primary-700 mb-6">
        <ArrowLeft className="w-4 h-4 mr-2" />
        Back to Conversations
      </Link>

      <div className="bg-white rounded-lg shadow-md border border-gray-200 mb-6 p-6">
        <h1 className="text-2xl font-bold text-gray-900 mb-2">{conversation.subject}</h1>
        <div className="flex items-center gap-4 text-sm text-gray-600">
          <span>Customer: {conversation.customerEmail}</span>
          <span>Status: <span className="font-semibold">{conversation.status}</span></span>
          <span>Started: {format(new Date(conversation.startedAt), 'PPp')}</span>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow-md border border-gray-200 p-6 mb-6">
        <h2 className="text-lg font-semibold mb-4">Messages</h2>
        <div className="space-y-4 mb-6">
          {conversation.messages.map((message) => (
            <div
              key={message.id}
              className={`p-4 rounded-lg ${
                message.sender === 'CUSTOMER'
                  ? 'bg-blue-50 ml-0 mr-12'
                  : message.sender === 'AGENT'
                  ? 'bg-green-50 ml-12 mr-0'
                  : 'bg-gray-50 mx-12'
              }`}
            >
              <div className="flex justify-between items-start mb-2">
                <span className="font-semibold text-sm">
                  {message.sender === 'CUSTOMER' ? 'Customer' : message.sender === 'AGENT' ? 'Agent' : 'System'}
                </span>
                <span className="text-xs text-gray-500">
                  {format(new Date(message.createdAt), 'PPp')}
                </span>
              </div>
              <p className="text-gray-700">{message.content}</p>
            </div>
          ))}
        </div>

        <form onSubmit={handleSendMessage} className="flex gap-3">
          <input
            type="text"
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            placeholder="Type your message..."
            className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
          />
          <button
            type="submit"
            disabled={!newMessage.trim() || sendMessageMutation.isPending}
            className="px-6 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 disabled:opacity-50 flex items-center"
          >
            <Send className="w-4 h-4 mr-2" />
            Send
          </button>
        </form>
      </div>
    </div>
  )
}
