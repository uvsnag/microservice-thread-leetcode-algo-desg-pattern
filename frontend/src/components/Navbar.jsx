import { NavLink } from 'react-router-dom'

function Navbar({ user, onLogout }) {
  return (
    <div className="sidebar">
      <h2>AWS Microservices</h2>
      <NavLink to="/" className={({ isActive }) => isActive ? 'active' : ''}>
        Dashboard
      </NavLink>
      <NavLink to="/users" className={({ isActive }) => isActive ? 'active' : ''}>
        Users
      </NavLink>
      <NavLink to="/files" className={({ isActive }) => isActive ? 'active' : ''}>
        Files (S3)
      </NavLink>
      <NavLink to="/cache" className={({ isActive }) => isActive ? 'active' : ''}>
        Cache (Redis)
      </NavLink>
      <NavLink to="/notifications" className={({ isActive }) => isActive ? 'active' : ''}>
        Notifications
      </NavLink>
      <NavLink to="/learning" className={({ isActive }) => isActive ? 'active' : ''}>
        Learning
      </NavLink>
      <div style={{ marginTop: 'auto', padding: '20px', borderTop: '1px solid #333', position: 'absolute', bottom: 0, width: 220 }}>
        <div style={{ fontSize: 12, color: '#888', marginBottom: 4 }}>{user.usrNm || user.usrId}</div>
        <div style={{ fontSize: 11, color: '#666' }}>{user.coCd}</div>
        <button onClick={onLogout} style={{ marginTop: 8, background: 'none', border: '1px solid #555', color: '#ccc', padding: '4px 12px', borderRadius: 4, cursor: 'pointer', fontSize: 12 }}>
          Logout
        </button>
      </div>
    </div>
  )
}

export default Navbar
