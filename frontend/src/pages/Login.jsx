import { useState } from 'react'
import { authApi } from '../api/client'

function Login({ onLogin }) {
  const [coCd, setCoCd] = useState('')
  const [usrId, setUsrId] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const { data } = await authApi.login(coCd, usrId, password)
      localStorage.setItem('accessToken', data.accessToken)
      localStorage.setItem('refreshToken', data.refreshToken)
      onLogin({ usrId: data.usrId, coCd: data.coCd, usrNm: data.usrNm })
    } catch (err) {
      setError(err.response?.data?.message || err.response?.data?.error || 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-container">
      <form className="login-box" onSubmit={handleSubmit}>
        <h2>Microservices Login</h2>
        {error && <div className="error-msg">{error}</div>}
        <div className="form-group">
          <label>Company Code</label>
          <input value={coCd} onChange={e => setCoCd(e.target.value)} placeholder="e.g. DEMO" required />
        </div>
        <div className="form-group">
          <label>User ID</label>
          <input value={usrId} onChange={e => setUsrId(e.target.value)} placeholder="e.g. admin" required />
        </div>
        <div className="form-group">
          <label>Password</label>
          <input type="password" value={password} onChange={e => setPassword(e.target.value)} required />
        </div>
        <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: 8 }} disabled={loading}>
          {loading ? 'Signing in...' : 'Sign In'}
        </button>
      </form>
    </div>
  )
}

export default Login
