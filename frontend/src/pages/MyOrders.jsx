import { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import api, { errorMessage } from '../api';
import OrderCard from '../components/OrderCard';
export default function MyOrders() {
  const [orders, setOrders] = useState([]); const [error, setError] = useState(''); const [loading, setLoading] = useState(true);
  const location = useLocation();
  async function load() {
    setLoading(true); setError('');
    try { const { data } = await api.get('/orders/my'); setOrders(data); }
    catch (err) { setError(errorMessage(err)); } finally { setLoading(false); }
  }
  useEffect(() => { load(); }, []);
  return <><div className="section-heading"><div><p className="eyebrow">FROM OUR KITCHEN TO YOUR DOOR</p><h1>My orders</h1></div><button className="secondary" disabled={loading} onClick={load}>Refresh orders</button></div>{location.state?.message && <p role="status" className="success">{location.state.message}</p>}{error && <p role="alert" className="error">{error}</p>}{loading ? <p role="status">Loading orders…</p> : !orders.length && !error ? <div className="empty-state"><h2>Your first craving starts here.</h2><Link to="/">Explore the menu</Link></div> : <div className="orders-grid">{orders.map(order => <OrderCard key={order.id} order={order}/>)}</div>}</>;
}
