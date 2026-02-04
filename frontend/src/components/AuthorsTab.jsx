import React, { useState, useEffect } from 'react'
import { authorService } from '../api'
import './AuthorsTab.css'

function AuthorsTab() {
  const [authors, setAuthors] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    biography: '',
    organization: ''
  })

  useEffect(() => {
    loadAuthors()
  }, [])

  const loadAuthors = async () => {
    setLoading(true)
    setError(null)
    try {
      const response = await authorService.listAuthors(0, 100)
      setAuthors(response.data.content || [])
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load authors')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    try {
      await authorService.createAuthor(formData)
      setFormData({
        firstName: '',
        lastName: '',
        email: '',
        biography: '',
        organization: ''
      })
      setShowForm(false)
      await loadAuthors()
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create author')
    }
  }

  return (
    <div className="tab-container">
      <div className="tab-header">
        <h2>👥 Authors Management</h2>
        <button 
          className="btn btn-primary"
          onClick={() => setShowForm(!showForm)}
        >
          {showForm ? '✕ Cancel' : '+ New Author'}
        </button>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {showForm && (
        <form className="form" onSubmit={handleSubmit}>
          <div className="form-grid">
            <input
              type="text"
              name="firstName"
              placeholder="First Name"
              value={formData.firstName}
              onChange={handleInputChange}
              required
              className="form-input"
            />
            <input
              type="text"
              name="lastName"
              placeholder="Last Name"
              value={formData.lastName}
              onChange={handleInputChange}
              required
              className="form-input"
            />
            <input
              type="email"
              name="email"
              placeholder="Email"
              value={formData.email}
              onChange={handleInputChange}
              required
              className="form-input"
            />
            <input
              type="text"
              name="organization"
              placeholder="Organization"
              value={formData.organization}
              onChange={handleInputChange}
              className="form-input"
            />
          </div>
          <textarea
            name="biography"
            placeholder="Biography"
            value={formData.biography}
            onChange={handleInputChange}
            rows="4"
            className="form-textarea"
          />
          <button type="submit" className="btn btn-success">Create Author</button>
        </form>
      )}

      {loading && <div className="loading">Loading authors...</div>}

      <div className="grid">
        {authors.map(author => (
          <div key={author.id} className="card">
            <h3>{author.firstName} {author.lastName}</h3>
            <p className="email">{author.email}</p>
            {author.organization && <p className="organization">{author.organization}</p>}
            {author.biography && <p className="biography">{author.biography}</p>}
            <div className="card-meta">
              <span>ID: {author.id}</span>
              <span className="status active">Active</span>
            </div>
          </div>
        ))}
      </div>

      {!loading && authors.length === 0 && (
        <div className="empty">
          <p>No authors found. Create your first author!</p>
        </div>
      )}
    </div>
  )
}

export default AuthorsTab
