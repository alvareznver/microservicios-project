import axios from 'axios'

const REACT_APP_AUTHORS_API_URL = process.env.REACT_APP_AUTHORS_API_URL || 'http://localhost:8001/api'
const REACT_APP_PUBLICATIONS_API_URL = process.env.REACT_APP_PUBLICATIONS_API_URL || 'http://localhost:8002/api'

const authorsApi = axios.create({
  baseURL: REACT_APP_AUTHORS_API_URL,
  timeout: 5000
})

const publicationsApi = axios.create({
  baseURL: REACT_APP_PUBLICATIONS_API_URL,
  timeout: 5000
})

export const authorService = {
  createAuthor: (data) => authorsApi.post('/authors', data),
  getAuthor: (id) => authorsApi.get(`/authors/${id}`),
  listAuthors: (page = 0, size = 10) => authorsApi.get(`/authors?page=${page}&size=${size}`),
  updateAuthor: (id, data) => authorsApi.put(`/authors/${id}`, data),
  deleteAuthor: (id) => authorsApi.delete(`/authors/${id}`)
}

export const publicationService = {
  createPublication: (data) => publicationsApi.post('/publications', data),
  getPublication: (id) => publicationsApi.get(`/publications/${id}`),
  listPublications: (page = 0, size = 10) => publicationsApi.get(`/publications?page=${page}&size=${size}`),
  listByAuthor: (authorId, page = 0, size = 10) => 
    publicationsApi.get(`/publications/author/${authorId}?page=${page}&size=${size}`),
  changeStatus: (id, status) => publicationsApi.patch(`/publications/${id}/status?status=${status}`)
}

export default {
  authorService,
  publicationService
}
