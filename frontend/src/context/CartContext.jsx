import { createContext, useContext, useEffect, useState } from 'react';
const CartContext = createContext(null);
function loadCart() {
  try {
    const saved = JSON.parse(localStorage.getItem('crave-cart') || '[]');
    return Array.isArray(saved) ? saved.filter(item => Number.isInteger(item.id) && typeof item.name === 'string' && Number.isFinite(item.price) && item.price > 0 && Number.isInteger(item.quantity) && item.quantity > 0 && item.quantity <= 99) : [];
  } catch { return []; }
}
export function CartProvider({ children }) {
  const [items, setItems] = useState(loadCart);
  useEffect(() => { localStorage.setItem('crave-cart', JSON.stringify(items)); }, [items]);
  function add(dish) {
    setItems(current => current.some(item => item.id === dish.id)
      ? current.map(item => item.id === dish.id ? { ...item, price: Number(dish.price), quantity: Math.min(99, item.quantity + 1) } : item)
      : [...current, { id: dish.id, name: dish.name, price: Number(dish.price), imageUrl: dish.imageUrl, quantity: 1 }]);
  }
  function update(id, quantity) {
    if (!Number.isInteger(quantity) || quantity < 1 || quantity > 99) return;
    setItems(current => current.map(item => item.id === id ? { ...item, quantity } : item));
  }
  const remove = id => setItems(current => current.filter(item => item.id !== id));
  const clear = () => setItems([]);
  const count = items.reduce((sum, item) => sum + item.quantity, 0);
  const total = items.reduce((sum, item) => sum + item.price * item.quantity, 0);
  return <CartContext.Provider value={{ items, add, update, remove, clear, count, total }}>{children}</CartContext.Provider>;
}
export const useCart = () => useContext(CartContext);
