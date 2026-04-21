import { useState, useEffect } from 'react'
import { userApi, notificationApi } from '../api/client'

function Dashboard() {
  const [userCount, setUserCount] = useState('-')
  const [notifCount, setNotifCount] = useState('-')

  useEffect(() => {
    userApi.count().then(r => setUserCount(r.data)).catch(() => setUserCount('N/A'))
    notificationApi.count().then(r => setNotifCount(r.data)).catch(() => setNotifCount('N/A'))
  }, [])

  return (
    <div>
      <h1 className="page-header">Dashboard</h1>
      <div className="stat-cards">
        <div className="stat-card">
          <div className="value">{userCount}</div>
          <div className="label">Total Users</div>
        </div>
        <div className="stat-card">
          <div className="value">{notifCount}</div>
          <div className="label">Notifications</div>
        </div>
        <div className="stat-card">
          <div className="value">6</div>
          <div className="label">Microservices</div>
        </div>
      </div>

      <div className="card">
        <h3 style={{ marginBottom: 12 }}>Architecture Overview</h3>
        <table>
          <thead>
            <tr><th>Service</th><th>Port</th><th>Technology</th><th>Description</th></tr>
          </thead>
          <tbody>
            <tr><td>API Gateway</td><td>8080</td><td>Spring Cloud Gateway + JWT</td><td>Central routing & authentication</td></tr>
            <tr><td>Auth Service</td><td>8084</td><td>JWT + MyBatis + PostgreSQL</td><td>Login, token refresh, validation</td></tr>
            <tr><td>User Service</td><td>8081</td><td>MyBatis + PostgreSQL + Kafka</td><td>Read-only user queries</td></tr>
            <tr><td>Cache Service</td><td>8082</td><td>Redis + Kafka + RabbitMQ</td><td>Event caching layer</td></tr>
            <tr><td>File Service</td><td>8083</td><td>AWS S3 + RabbitMQ</td><td>File listing & download</td></tr>
            <tr><td>Notification Service</td><td>8085</td><td>Kafka + RabbitMQ</td><td>Event notifications</td></tr>
          </tbody>
        </table>
      </div>
    </div>
  )
}

export default Dashboard
