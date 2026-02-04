import React, { useState } from 'react'
import './App.css'
import AuthorsTab from './components/AuthorsTab'
import PublicationsTab from './components/PublicationsTab'

function App() {
  const [activeTab, setActiveTab] = useState('authors')

  return (
    <div className="app">
      <header className="header">
        <h1>📚 Editorial Management System</h1>
        <p>Manage Authors and Publications</p>
      </header>

      <nav className="nav">
        <button 
          className={`nav-btn ${activeTab === 'authors' ? 'active' : ''}`}
          onClick={() => setActiveTab('authors')}
        >
          👥 Authors
        </button>
        <button 
          className={`nav-btn ${activeTab === 'publications' ? 'active' : ''}`}
          onClick={() => setActiveTab('publications')}
        >
          📄 Publications
        </button>
      </nav>

      <main className="main-content">
        {activeTab === 'authors' && <AuthorsTab />}
        {activeTab === 'publications' && <PublicationsTab />}
      </main>
    </div>
  )
}

export default App
