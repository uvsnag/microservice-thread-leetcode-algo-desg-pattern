import { useState, useEffect } from 'react'
import { cacheApi } from '../api/client'

function Cache() {
  const [entries, setEntries] = useState({})
  const [fileActivities, setFileActivities] = useState([])
  const [newKey, setNewKey] = useState('')
  const [newValue, setNewValue] = useState('')
  const [error, setError] = useState('')

  const loadEntries = async () => {
    try {
      const { data } = await cacheApi.getEntries()
      setEntries(data || {})
    } catch (err) {
      setError('Failed to load cache entries: ' + (err.response?.data?.message || err.message))
    }
  }

  const loadFileActivities = async () => {
    try {
      const { data } = await cacheApi.getFileActivities()
      setFileActivities(data || [])
    } catch { /* ignore */ }
  }

  const addEntry = async () => {
    if (!newKey || !newValue) return
    try {
      await cacheApi.setEntry(newKey, newValue)
      setNewKey('')
      setNewValue('')
      loadEntries()
    } catch (err) {
      setError('Failed to add entry: ' + (err.response?.data?.message || err.message))
    }
  }

  useEffect(() => {
    loadEntries()
    loadFileActivities()
  }, [])

  return (
    <div>
      <h1 className="page-header">Cache (Redis)</h1>

      <div className="card">
        <h3 style={{ marginBottom: 12 }}>Add Cache Entry</h3>
        <div style={{ display: 'flex', gap: 8 }}>
          <input placeholder="Key" value={newKey} onChange={e => setNewKey(e.target.value)} style={{ flex: 1, padding: 8, border: '1px solid #ddd', borderRadius: 4 }} />
          <input placeholder="Value" value={newValue} onChange={e => setNewValue(e.target.value)} style={{ flex: 2, padding: 8, border: '1px solid #ddd', borderRadius: 4 }} />
          <button className="btn btn-primary" onClick={addEntry}>Set</button>
          <button className="btn" onClick={loadEntries} style={{ background: '#eee' }}>Refresh</button>
        </div>
      </div>

      {error && <div className="error-msg">{error}</div>}

      <div className="card">
        <h3 style={{ marginBottom: 12 }}>Cache Entries</h3>
        <table>
          <thead><tr><th>Key</th><th>Value</th></tr></thead>
          <tbody>
            {Object.keys(entries).length === 0 ? (
              <tr><td colSpan={2} style={{ textAlign: 'center', color: '#888' }}>No entries</td></tr>
            ) : Object.entries(entries).map(([k, v]) => (
              <tr key={k}><td>{k}</td><td>{v}</td></tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="card">
        <h3 style={{ marginBottom: 12 }}>Recent File Activities (from RabbitMQ)</h3>
        <table>
          <thead><tr><th>#</th><th>Activity Data</th></tr></thead>
          <tbody>
            {fileActivities.length === 0 ? (
              <tr><td colSpan={2} style={{ textAlign: 'center', color: '#888' }}>No file activities</td></tr>
            ) : fileActivities.map((a, i) => (
              <tr key={i}><td>{i + 1}</td><td style={{ fontSize: 12, fontFamily: 'monospace' }}>{a}</td></tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

export default Cache
