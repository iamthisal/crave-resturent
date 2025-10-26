import { Link } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { useAuth } from "../context/AuthContext";
import { money } from "../api";
import DishImage from "../components/DishImage";
export default function Cart() {
  const { items, update, remove, total } = useCart();
  const { user } = useAuth();
  return (
    <>
      <p className="eyebrow">YOUR NEXT GOOD MEAL</p>
      <h1>Your cart</h1>
      {!items.length ? (
        <div className="empty-state">
          <h2>A little hungry?</h2>
          <p>Your cart is waiting for something delicious.</p>
          <Link className="button" to="/">
            Browse the menu
          </Link>
        </div>
      ) : (
        <div className="two-column">
          <div className="cart-list">
            {items.map((item) => (
              <article className="cart-item" key={item.id}>
                <DishImage src={item.imageUrl} name={item.name} />
                <div>
                  <h3>{item.name}</h3>
                  <p>{money(item.price)}</p>
                  <button
                    className="text-button"
                    onClick={() => remove(item.id)}
                  >
                    Remove
                  </button>
                </div>
                <label>
                  Quantity
                  <input
                    type="number"
                    min="1"
                    max="99"
                    value={item.quantity}
                    onChange={(e) => update(item.id, Number(e.target.value))}
                  />
                </label>
                <strong>{money(item.price * item.quantity)}</strong>
              </article>
            ))}
          </div>
          <aside className="panel summary">
            <h2>Order summary</h2>
            <div className="summary-line">
              <span>Subtotal</span>
              <strong>{money(total)}</strong>
            </div>
            <div className="summary-line">
              <span>Delivery</span>
              <span>Free</span>
            </div>
            <div className="summary-line total">
              <span>Total</span>
              <strong>{money(total)}</strong>
            </div>
            <p className="muted">
              Cash on delivery. Prices are confirmed at checkout.
            </p>
            {user?.role === "ADMIN" ? (
              <p>Sign in with a customer account to order.</p>
            ) : (
              <Link className="button" to="/checkout">
                Continue to checkout →
              </Link>
            )}
            <Link className="back-link" to="/">
              Keep exploring the menu
            </Link>
          </aside>
        </div>
      )}
    </>
  );
}
