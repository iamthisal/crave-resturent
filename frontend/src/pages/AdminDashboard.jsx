import { useState } from 'react';
import AdminDishes from '../components/AdminDishes';
import AdminOrders from '../components/AdminOrders';
export default function AdminDashboard() {
  const [tab, setTab] = useState('dishes');
  return <><p className="eyebrow">CRAVE KITCHEN</p><h1>Admin dashboard</h1><div className="tabs" aria-label="Dashboard sections"><button className={tab === 'dishes' ? 'chip active' : 'chip'} aria-pressed={tab === 'dishes'} onClick={() => setTab('dishes')}>Dishes</button><button className={tab === 'orders' ? 'chip active' : 'chip'} aria-pressed={tab === 'orders'} onClick={() => setTab('orders')}>Orders</button></div>{tab === 'dishes' ? <AdminDishes/> : <AdminOrders/>}</>;
}
