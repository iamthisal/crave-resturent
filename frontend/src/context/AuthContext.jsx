import { createContext, useContext, useEffect, useState } from 'react';
import api from '../api';
const AuthContext = createContext(null);
function savedUser() {
  try { return localStorage.getItem('crave-token') ? JSON.parse(localStorage.getItem('crave-user')) : null; }
  catch { return null; }
}
export function AuthProvider({ children }) {
  const [user, setUser] = useState(savedUser);
  const [token, setToken] = useState(() => localStorage.getItem('crave-token'));
  function logout() {
    localStorage.removeItem('crave-token'); localStorage.removeItem('crave-user'); setUser(null); setToken(null);
  }
  useEffect(() => {
    window.addEventListener('crave-logout', logout);
    return () => window.removeEventListener('crave-logout', logout);
  }, []);
  async function authenticate(path, values) {
    const { data } = await api.post(`/auth/${path}`, values);
    localStorage.setItem('crave-token', data.token); localStorage.setItem('crave-user', JSON.stringify(data.user));
    setUser(data.user); setToken(data.token); return data.user;
  }
  return <AuthContext.Provider value={{ user, token, logout, authenticate }}>{children}</AuthContext.Provider>;
}
export const useAuth = () => useContext(AuthContext);
