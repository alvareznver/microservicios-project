import React, { useState, useEffect } from 'react'
import { publicationService, authorService } from '../api'
import './PublicationsTab.css'

function PublicationsTab() {
  const [publications, setPublications] = useState([])
  const [authors, setAuthors] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [formData, setFormData] = useState({
    title: '',
    content: '',
    authorId: ''
  })
  const [statusFormData, setStatusFormData] = useState({
    publicationId: null,
    newStatus: '',
    editorName: '',
    reviewComments: '',
    rejectionReason: ''
  })

  useEffect(() => {
    loadPublications()
    loadAuthors()
  }, [])

  const loadPublications = async () => {
    setLoading(true)
    setError(null)
    try {
      const response = await publicationService.listPublications(0, 100)
      setPublications(response.data.content || [])
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load publications')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const loadAuthors = async () => {
    try {
      const response = await authorService.listAuthors(0, 100)
      setAuthors(response.data.content || [])
    } catch (err) {
      console.error('Failed to load authors', err)
    }
  }

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handleStatusChange = (e) => {
    const { name, value } = e.target
    setStatusFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    try {
      await publicationService.createPublication(formData)
      setFormData({
        title: '',
        content: '',
        authorId: ''
      })
      setShowForm(false)
      await loadPublications()
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create publication')
    }
  }

  const handleStatusSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    try {
      // First update publication with additional info if needed
      if (statusFormData.newStatus === 'APPROVED') {
        // Add editor name and other info
      } else if (statusFormData.newStatus === 'REJECTED') {
        // Add rejection reason
      }
      
      await publicationService.changeStatus(
        statusFormData.publicationId,
        statusFormData.newStatus
      )
      
      setStatusFormData({
        publicationId: null,
        newStatus: '',
        editorName: '',
        reviewComments: '',
        rejectionReason: ''
      })
      await loadPublications()
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update publication status')
    }
  }

  const getStatusColor = (status) => {
    const colors = {
      'DRAFT': '#757575',
      'IN_REVIEW': '#ff9800',
      'APPROVED': '#4caf50',
      'PUBLISHED': '#2196f3',
      'REJECTED': '#f44336',
      'REQUIRES_CHANGES': '#ff5722'
    }
    return colors[status] || '#999'
  }

  const getAuthorName = (authorId) => {
    const author = authors.find(a => a.id === authorId)
    return author ? `${author.firstName} ${author.lastName}` : `Author #${authorId}`
  }

  return (
    <div className="tab-container">
      <div className="tab-header">
        <h2>📄 Publications Management</h2>
        <button 
          className="btn btn-primary"
          onClick={() => setShowForm(!showForm)}
        >
          {showForm ? '✕ Cancel' : '+ New Publication'}
        </button>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {showForm && (
        <form className="form" onSubmit={handleSubmit}>
          <div className="form-grid">
            <input
              type="text"
              name="title"
              placeholder="Publication Title"
              value={formData.title}
              onChange={handleInputChange}
              required
              className="form-input"
            />
            <select
              name="authorId"
              value={formData.authorId}
              onChange={handleInputChange}
              required
              className="form-input"
            >
              <option value="">Select Author</option>
              {authors.map(author => (
                <option key={author.id} value={author.id}>
                  {author.firstName} {author.lastName}
                </option>
              ))}
            </select>
          </div>
          <textarea
            name="content"
            placeholder="Publication Content"
            value={formData.content}
            onChange={handleInputChange}
            required
            rows="6"
            className="form-textarea"
          />
          <button type="submit" className="btn btn-success">Create Publication</button>
        </form>
      )}

      {loading && <div className="loading">Loading publications...</div>}

      <div className="publications-list">
        {publications.map(pub => (
          <div key={pub.id} className="pub-card">
            <div className="pub-header">
              <h3>{pub.title}</h3>
              <span 
                className="status-badge"
                style={{ backgroundColor: getStatusColor(pub.status) }}
              >
                {pub.status.replace(/_/g, ' ')}
              </span>
            </div>
            
            <p className="pub-author">Author: {getAuthorName(pub.authorId)}</p>
            
            <div className="pub-content">
              {pub.content.substring(0, 150)}...
            </div>

            {pub.author && (
              <div className="author-info">
                <strong>Author Details:</strong> {pub.author.email}
              </div>
            )}

            <div className="pub-actions">
              {pub.status === 'DRAFT' && (
                <button 
                  className="btn btn-warning"
                  onClick={() => setStatusFormData({
                    publicationId: pub.id,
                    newStatus: 'IN_REVIEW',
                    editorName: '',
                    reviewComments: '',
                    rejectionReason: ''
                  })}
                >
                  Send to Review
                </button>
              )}
              {pub.status === 'IN_REVIEW' && (
                <div className="status-options">
                  <button 
                    className="btn btn-success"
                    onClick={() => setStatusFormData({
                      publicationId: pub.id,
                      newStatus: 'APPROVED',
                      editorName: '',
                      reviewComments: '',
                      rejectionReason: ''
                    })}
                  >
                    Approve
                  </button>
                  <button 
                    className="btn btn-danger"
                    onClick={() => setStatusFormData({
                      publicationId: pub.id,
                      newStatus: 'REJECTED',
                      editorName: '',
                      reviewComments: '',
                      rejectionReason: ''
                    })}
                  >
                    Reject
                  </button>
                </div>
              )}
              {pub.status === 'APPROVED' && (
                <button 
                  className="btn btn-success"
                  onClick={() => setStatusFormData({
                    publicationId: pub.id,
                    newStatus: 'PUBLISHED',
                    editorName: '',
                    reviewComments: '',
                    rejectionReason: ''
                  })}
                >
                  Publish
                </button>
              )}
            </div>

            <div className="pub-meta">
              <span>ID: {pub.id}</span>
              <span>{new Date(pub.createdAt).toLocaleDateString()}</span>
            </div>
          </div>
        ))}
      </div>

      {!loading && publications.length === 0 && (
        <div className="empty">
          <p>No publications found. Create your first publication!</p>
        </div>
      )}

      {statusFormData.publicationId && (
        <div className="modal-overlay" onClick={() => setStatusFormData({ publicationId: null })}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>Change Publication Status</h3>
            <form onSubmit={handleStatusSubmit}>
              {statusFormData.newStatus === 'REJECTED' && (
                <textarea
                  name="rejectionReason"
                  placeholder="Rejection Reason"
                  value={statusFormData.rejectionReason}
                  onChange={handleStatusChange}
                  required
                  className="form-textarea"
                  rows="4"
                />
              )}
              {statusFormData.newStatus === 'IN_REVIEW' && (
                <input
                  type="text"
                  name="editorName"
                  placeholder="Editor Name"
                  value={statusFormData.editorName}
                  onChange={handleStatusChange}
                  className="form-input"
                />
              )}
              <div className="modal-actions">
                <button type="submit" className="btn btn-success">Confirm</button>
                <button 
                  type="button" 
                  className="btn btn-primary"
                  onClick={() => setStatusFormData({ publicationId: null })}
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

export default PublicationsTab
