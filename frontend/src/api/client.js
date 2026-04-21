import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_BASE_URL || '';

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
});

// Attach JWT token to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle 401 responses
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authApi = {
  login: (coCd, usrId, password) => api.post('/auth/login', { coCd, usrId, password }),
  refresh: (refreshToken) => api.post('/auth/refresh', { refreshToken }),
  validate: () => api.get('/auth/validate'),
};

export const userApi = {
  getAll: () => api.get('/users'),
  getByKey: (coCd, usrId) => api.get(`/users/${coCd}/${usrId}`),
  getByCompany: (coCd) => api.get(`/users/company/${coCd}`),
  search: (keyword) => api.get('/users/search', { params: { keyword } }),
  getByAge: (age) => api.get(`/users/by-age/${age}`),
  count: () => api.get('/users/count'),
};

export const cacheApi = {
  getEntries: () => api.get('/cache/entries'),
  setEntry: (key, value) => api.post('/cache/entries', null, { params: { key, value } }),
  getEntry: (key) => api.get(`/cache/entries/${key}`),
  getCachedUser: (id) => api.get(`/cache/users/${id}`),
  getFileActivities: () => api.get('/cache/file-activities'),
};

export const fileApi = {
  list: (folder) => api.get('/files', { params: folder ? { folder } : {} }),
  download: (folder, filename) =>
    api.get('/files/download', { params: { folder, filename }, responseType: 'blob' }),
};

export const notificationApi = {
  getRecent: (limit = 50) => api.get('/notifications', { params: { limit } }),
  getByType: (type) => api.get('/notifications/by-type', { params: { type } }),
  count: () => api.get('/notifications/count'),
};

export const learningApi = {
  run: (section, item) => api.get(`/api/learning/${section}/${item}`),
  index: (section) => api.get(`/api/learning/${section}`),
};

export default api;
