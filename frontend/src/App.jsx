import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';
import Login from './pages/Login';
import Register from './pages/Register';
import Menu from './pages/Menu';
import Cart from './pages/Cart';
import Checkout from './pages/Checkout';
import MyOrders from './pages/MyOrders';
import AdminDashboard from './pages/AdminDashboard';
export default function App() {
  return <BrowserRouter><AuthProvider><CartProvider><Navbar/><main className="container"><Routes><Route path="/" element={<Menu/>}/><Route path="/login" element={<Login/>}/><Route path="/register" element={<Register/>}/><Route path="/cart" element={<Cart/>}/><Route path="/checkout" element={<ProtectedRoute role="USER"><Checkout/></ProtectedRoute>}/><Route path="/my-orders" element={<ProtectedRoute role="USER"><MyOrders/></ProtectedRoute>}/><Route path="/admin" element={<ProtectedRoute role="ADMIN"><AdminDashboard/></ProtectedRoute>}/><Route path="*" element={<div className="empty-state"><h1>Page not found</h1><Link to="/">Back to menu</Link></div>}/></Routes></main><footer className="site-footer"><Link className="brand" to="/">crave<span>RESTAURANT</span></Link><p>Good food. Great cravings.</p><span>Made with care, delivered to you.</span></footer></CartProvider></AuthProvider></BrowserRouter>;
}
