import { useState } from 'react'
import { fileApi } from '../api/client'

function Files() {
  const [files, setFiles] = useState([])
  const [folder, setFolder] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const listFiles = async () => {
    setLoading(true)
    setError('')
    try {
      const { data } = await fileApi.list(folder || undefined)
      setFiles(data)
    } catch (err) {
      setError('Failed to list files: ' + (err.response?.data?.message || err.message))
    } finally {
      setLoading(false)
    }
  }

  const downloadFile = async (fileKey) => {
    try {
      const parts = fileKey.split('/')
      const filename = parts.pop()
      const fileFolder = parts.join('/')
      const { data } = await fileApi.download(fileFolder || undefined, filename)
      const url = window.URL.createObjectURL(new Blob([data]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', filename)
      document.body.appendChild(link)
      link.click()
      link.remove()
      window.URL.revokeObjectURL(url)
    } catch (err) {
      setError('Download failed: ' + (err.response?.data?.message || err.message))
    }
  }

  return (
    <div>
      <h1 className="page-header">Files (AWS S3)</h1>
      <div className="search-bar">
        <input
          placeholder="Folder (optional, e.g. documents)"
          value={folder}
          onChange={e => setFolder(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && listFiles()}
        />
        <button className="btn btn-primary" onClick={listFiles}>List Files</button>
      </div>

      {error && <div className="error-msg">{error}</div>}

      <div className="card">
        {loading ? <div>Loading...</div> : (
          <table>
            <thead>
              <tr><th>#</th><th>File Key</th><th>Actions</th></tr>
            </thead>
            <tbody>
              {files.length === 0 ? (
                <tr><td colSpan={3} style={{ textAlign: 'center', color: '#888' }}>No files. Click "List Files" to load from S3.</td></tr>
              ) : files.map((f, i) => (
                <tr key={i}>
                  <td>{i + 1}</td>
                  <td>{f}</td>
                  <td>
                    <button className="btn btn-primary" onClick={() => downloadFile(f)}>Download</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  )
}

export default Files
