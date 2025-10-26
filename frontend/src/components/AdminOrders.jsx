import { useEffect, useState } from "react";
import api, { errorMessage } from "../api";
import OrderCard, { statuses } from "./OrderCard";
export default function AdminOrders() {
  const [orders, setOrders] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(null);
  const [notice, setNotice] = useState("");
  async function load() {
    setLoading(true);
    setError("");
    try {
      const { data } = await api.get("/orders");
      setOrders(data);
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => {
    load();
  }, []);
  async function update(order, status) {
    setBusy(order.id);
    setError("");
    setNotice("");
    try {
      const { data } = await api.put(`/orders/${order.id}/status`, { status });
      setOrders((current) =>
        current.map((item) => (item.id === order.id ? data : item)),
      );
      setNotice(`Order #${order.id} is now ${status.toLowerCase()}.`);
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setBusy(null);
    }
  }
  return (
    <>
      <div className="section-heading">
        <h2>Customer orders</h2>
        <button
          className="secondary"
          disabled={loading || busy !== null}
          onClick={load}
        >
          Refresh orders
        </button>
      </div>
      {error && (
        <p className="error" role="alert">
          {error}
        </p>
      )}
      <p className="notice" role="status">
        {notice}
      </p>
      {loading ? (
        <p role="status">Loading orders…</p>
      ) : !orders.length ? (
        <div className="empty-state">No orders yet.</div>
      ) : (
        <div className="orders-grid">
          {orders.map((order) => (
            <OrderCard key={order.id} order={order}>
              <label>
                Update status
                <select
                  value={order.status}
                  disabled={busy === order.id || order.status === "DELIVERED"}
                  onChange={(e) => update(order, e.target.value)}
                >
                  {statuses.map((status, index) => (
                    <option
                      key={status}
                      value={status}
                      disabled={
                        index !== statuses.indexOf(order.status) &&
                        index !== statuses.indexOf(order.status) + 1
                      }
                    >
                      {status}
                    </option>
                  ))}
                </select>
              </label>
            </OrderCard>
          ))}
        </div>
      )}
    </>
  );
}
