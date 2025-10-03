import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { errorMessage } from '../api';
export default function Login() {
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState(''); const [busy, setBusy] = useState(false);
  const { authenticate } = useAuth(); const navigate = useNavigate(); const location = useLocation();
  async function submit(event) {
    event.preventDefault(); setError(''); setBusy(true);
    try { const user = await authenticate('login', form); navigate(user.role === 'ADMIN' ? '/admin' : location.state?.from || '/', { replace: true }); }
    catch (err) { setError(errorMessage(err)); } finally { setBusy(false); }
  }
  return <section className="auth-card"><p className="eyebrow">WELCOME BACK</p><h1>Good food awaits.</h1><p className="muted">Sign in to order your Crave favourites.</p><form onSubmit={submit}><label>Email<input type="email" autoComplete="email" required value={form.email} onChange={e => setForm({ ...form, email: e.target.value })}/></label><label>Password<input type="password" autoComplete="current-password" required maxLength={72} value={form.password} onChange={e => setForm({ ...form, password: e.target.value })}/></label>{error && <p role="alert" className="error">{error}</p>}<button disabled={busy}>{busy ? 'Signing in…' : 'Sign in'}</button></form><p>New to Crave? <Link to="/register">Create an account</Link></p></section>;
}
