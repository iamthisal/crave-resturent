import { NavLink, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
export default function Navbar() {
  const { user, logout } = useAuth(); const { count } = useCart();
  return <header className="site-header"><nav className="navbar" aria-label="Main navigation"><Link className="brand" to="/">crave<span>RESTAURANT</span></Link><div className="nav-links"><NavLink to="/">Menu</NavLink>{user?.role !== 'ADMIN' && <NavLink to="/cart">Cart <span className="cart-count">{count}</span></NavLink>}{user?.role === 'USER' && <NavLink to="/my-orders">My orders</NavLink>}{user?.role === 'ADMIN' && <NavLink to="/admin">Dashboard</NavLink>}{user ? <><span className="nav-user">Hi, {user.name}</span><button className="secondary small" onClick={logout}>Logout</button></> : <NavLink className="nav-login" to="/login">Sign in <span aria-hidden="true">↗</span></NavLink>}</div></nav></header>;
}
