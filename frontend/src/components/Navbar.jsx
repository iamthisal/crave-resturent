import { NavLink, Link } from 'react-router-dom';
export default function Navbar() {
  return <header className="site-header"><nav className="navbar"><Link className="brand" to="/">crave<span>RESTAURANT</span></Link><div className="nav-links"><NavLink to="/">Menu</NavLink><NavLink to="/cart">Cart</NavLink><NavLink to="/login">Login</NavLink></div></nav></header>;
}
