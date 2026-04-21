import { useState, useEffect } from 'react'
import { notificationApi } from '../api/client'

function Notifications() {
  const [notifications, setNotifications] = useState([])
  const [filter, setFilter] = useState('ALL')
  const [loading, setLoading] = useState(false)

  const load = async () => {
    setLoading(true)
    try {
      const { data } = filter === 'ALL'
        ? await notificationApi.getRecent(50)
        : await notificationApi.getByType(filter)
      setNotifications(data)
    } catch { /* ignore */ }
    setLoading(false)
  }

  useEffect(() => { load() }, [filter])

  return (
    <div>
      <h1 className="page-header">Notifications (Kafka + RabbitMQ)</h1>

      <div style={{ display: 'flex', gap: 8, marginBottom: 16 }}>
        {['ALL', 'USER_EVENT', 'FILE_EVENT'].map(t => (
          <button
            key={t}
            className={`btn ${filter === t ? 'btn-primary' : ''}`}
            style={filter !== t ? { background: '#eee' } : {}}
            onClick={() => setFilter(t)}
          >
            {t.replace('_', ' ')}
          </button>
        ))}
        <button className="btn" onClick={load} style={{ background: '#eee', marginLeft: 'auto' }}>Refresh</button>
      </div>

      <div className="card">
        {loading ? <div>Loading...</div> : (
          <table>
            <thead>
              <tr><th>Time</th><th>Type</th><th>Source</th><th>Message</th></tr>
            </thead>
            <tbody>
              {notifications.length === 0 ? (
                <tr><td colSpan={4} style={{ textAlign: 'center', color: '#888' }}>No notifications yet. Trigger some user or file operations first.</td></tr>
              ) : notifications.map((n, i) => (
                <tr key={n.id || i}>
                  <td style={{ fontSize: 12, whiteSpace: 'nowrap' }}>{n.occurredAt?.substring(0, 19)}</td>
                  <td>
                    <span className={`badge ${n.type === 'USER_EVENT' ? 'badge-user' : 'badge-file'}`}>
                      {n.type}
                    </span>
                  </td>
                  <td>
                    <span className={`badge ${n.source === 'kafka' ? 'badge-kafka' : 'badge-rabbitmq'}`}>
                      {n.source}
                    </span>
                  </td>
                  <td style={{ fontSize: 13 }}>{n.message}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  )
}

export default Notifications
