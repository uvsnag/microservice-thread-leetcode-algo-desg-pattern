import { useState, useEffect } from 'react'
import { userApi } from '../api/client'

function Users() {
  const [users, setUsers] = useState([])
  const [keyword, setKeyword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const loadUsers = async () => {
    setLoading(true)
    setError('')
    try {
      const { data } = await userApi.getAll()
      setUsers(data)
    } catch (err) {
      setError('Failed to load users: ' + (err.response?.data?.message || err.message))
    } finally {
      setLoading(false)
    }
  }

  const searchUsers = async () => {
    if (!keyword.trim()) { loadUsers(); return }
    setLoading(true)
    setError('')
    try {
      const { data } = await userApi.search(keyword)
      setUsers(data)
    } catch (err) {
      setError('Search failed: ' + (err.response?.data?.message || err.message))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { loadUsers() }, [])

  return (
    <div>
      <h1 className="page-header">Users (PostgreSQL + MyBatis)</h1>
      <div className="search-bar">
        <input
          placeholder="Search by name, email, or user ID..."
          value={keyword}
          onChange={e => setKeyword(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && searchUsers()}
        />
        <button className="btn btn-primary" onClick={searchUsers}>Search</button>
        <button className="btn" onClick={loadUsers} style={{ background: '#eee' }}>Reset</button>
      </div>

      {error && <div className="error-msg">{error}</div>}

      <div className="card">
        {loading ? <div>Loading...</div> : (
          <table>
            <thead>
              <tr>
                <th>User ID</th>
                <th>Company</th>
                <th>Name</th>
                <th>Full Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Age</th>
                <th>Active</th>
                <th>Location</th>
              </tr>
            </thead>
            <tbody>
              {users.length === 0 ? (
                <tr><td colSpan={9} style={{ textAlign: 'center', color: '#888' }}>No users found</td></tr>
              ) : users.map((u, i) => (
                <tr key={i}>
                  <td>{u.usrId}</td>
                  <td>{u.coCd}</td>
                  <td>{u.usrNm}</td>
                  <td>{u.fullNm}</td>
                  <td>{u.usrEml}</td>
                  <td>{u.mphnNo}</td>
                  <td>{u.age}</td>
                  <td>{u.actFlg === 'Y' ? '✓' : '✗'}</td>
                  <td>{u.ctyNm || u.locCd}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  )
}

export default Users
