import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import api, { errorMessage, money } from '../api';
export default function Checkout() {
  const { user } = useAuth(); const { items, total, clear } = useCart(); const navigate = useNavigate();
  const [form, setForm] = useState({ fullName: user.name, phone: '', deliveryAddress: '', note: '' });
  const [busy, setBusy] = useState(false); const [error, setError] = useState('');
  async function submit(event) {
    event.preventDefault(); if (busy) return; setBusy(true); setError('');
    try {
      const { data } = await api.post('/orders', { ...form, items: items.map(item => ({ dishId: item.id, quantity: item.quantity })) });
      clear(); navigate('/my-orders', { state: { message: `Order #${data.id} placed! Please pay ${money(data.total)} on delivery.` }, replace: true });
    } catch (err) { setError(errorMessage(err)); } finally { setBusy(false); }
  }
  function change(event) { setForm({ ...form, [event.target.name]: event.target.value }); }
  if (!items.length) return <div className="empty-state"><h1>Your cart is empty</h1><Link to="/">Browse the menu</Link></div>;
  return <><p className="eyebrow">ONE LAST STEP</p><h1>Let’s bring it home.</h1><div className="two-column"><form className="panel" onSubmit={submit}><h2>Delivery details</h2><label>Full name<input name="fullName" required maxLength={100} autoComplete="name" value={form.fullName} onChange={change}/></label><label>Phone number<input name="phone" type="tel" required pattern="[+0-9() \-]{7,25}" autoComplete="tel" value={form.phone} onChange={change}/></label><label>Delivery address<textarea name="deliveryAddress" required maxLength={500} autoComplete="street-address" value={form.deliveryAddress} onChange={change}/></label><label>Order note <span className="muted">(optional)</span><textarea name="note" maxLength={1000} placeholder="Any delivery instructions?" value={form.note} onChange={change}/></label><p className="payment-notice">Payment method: Cash on Delivery - please pay the rider when your order arrives.</p>{error && <p role="alert" className="error">{error}</p>}<button disabled={busy}>{busy ? 'Placing order…' : 'Place order'}</button></form><aside className="panel summary"><h2>Your order</h2>{items.map(item => <div className="summary-line" key={item.id}><span>{item.quantity} × {item.name}</span><strong>{money(item.price * item.quantity)}</strong></div>)}<div className="summary-line"><span>Delivery</span><span>Free</span></div><div className="summary-line total"><span>Total</span><strong>{money(total)}</strong></div><p className="muted">Final prices and availability are checked when you place your order.</p><Link to="/cart">← Back to cart</Link></aside></div></>;
}
