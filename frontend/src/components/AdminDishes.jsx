import { useEffect, useState } from 'react';
import api, { errorMessage, money } from '../api';
const emptyDish = { name: '', description: '', price: '', imageUrl: '', category: '', available: true };
export default function AdminDishes() {
  const [dishes, setDishes] = useState([]); const [form, setForm] = useState(emptyDish); const [editId, setEditId] = useState(null);
  const [error, setError] = useState(''); const [notice, setNotice] = useState(''); const [busy, setBusy] = useState(false); const [loading, setLoading] = useState(true);
  async function load() { const { data } = await api.get('/dishes'); setDishes(data); }
  useEffect(() => { load().catch(err => setError(errorMessage(err))).finally(() => setLoading(false)); }, []);
  async function submit(event) {
    event.preventDefault(); setBusy(true); setError(''); setNotice('');
    try {
      const values = { ...form, price: Number(form.price) };
      if (editId) await api.put(`/dishes/${editId}`, values); else await api.post('/dishes', values);
      setNotice(editId ? 'Dish updated.' : 'Dish added.'); setEditId(null); setForm(emptyDish); await load();
    } catch (err) { setError(errorMessage(err)); } finally { setBusy(false); }
  }
  async function remove(dish) {
    if (!window.confirm(`Delete ${dish.name}?`)) return;
    setBusy(true); setError(''); setNotice('');
    try { await api.delete(`/dishes/${dish.id}`); if (editId === dish.id) { setEditId(null); setForm(emptyDish); } setNotice('Dish deleted.'); await load(); }
    catch (err) { setError(errorMessage(err)); } finally { setBusy(false); }
  }
  function change(event) { setForm({ ...form, [event.target.name]: event.target.type === 'checkbox' ? event.target.checked : event.target.value }); }
  return <>{error && <p className="error" role="alert">{error}</p>}<p className="notice" role="status">{notice}</p><div className="admin-layout"><form className="panel" onSubmit={submit}><h2>{editId ? 'Edit dish' : 'Add a dish'}</h2><label>Name<input name="name" required maxLength={100} value={form.name} onChange={change}/></label><label>Description<textarea name="description" required maxLength={1000} value={form.description} onChange={change}/></label><div className="form-row"><label>Price (Rs.)<input name="price" type="number" required min="0.01" max="99999999.99" step="0.01" value={form.price} onChange={change}/></label><label>Category<input name="category" required maxLength={60} value={form.category} onChange={change}/></label></div><label>Image URL<input name="imageUrl" type="url" pattern="https?://.*" maxLength={2000} placeholder="https://…" value={form.imageUrl} onChange={change}/></label><label className="checkbox-label"><input name="available" type="checkbox" checked={form.available} onChange={change}/>Available to order</label><div className="actions"><button disabled={busy}>{busy ? 'Saving…' : editId ? 'Save changes' : 'Add dish'}</button>{editId && <button type="button" className="secondary" onClick={() => { setEditId(null); setForm(emptyDish); }}>Cancel</button>}</div></form><section className="panel"><h2>Menu dishes <span className="muted">({dishes.length})</span></h2>{loading && <p role="status">Loading dishes…</p>}{!loading && !dishes.length && <p>No dishes yet. Add the first one.</p>}<div className="table-scroll"><table><thead><tr><th>Dish</th><th>Price</th><th>Available</th><th>Actions</th></tr></thead><tbody>{dishes.map(dish => <tr key={dish.id}><td><strong>{dish.name}</strong><small>{dish.category}</small></td><td>{money(dish.price)}</td><td>{dish.available ? 'Yes' : 'No'}</td><td><div className="actions"><button className="secondary small" disabled={busy} onClick={() => { setEditId(dish.id); setForm({ name: dish.name, description: dish.description, price: dish.price, imageUrl: dish.imageUrl || '', category: dish.category, available: dish.available }); }}>Edit</button><button className="danger small" disabled={busy} onClick={() => remove(dish)}>Delete</button></div></td></tr>)}</tbody></table></div></section></div></>;
}
