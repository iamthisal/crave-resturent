import axios from "axios";
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080/api",
});
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("crave-token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (
      error.response?.status === 401 &&
      !error.config.url.startsWith("/auth/")
    ) {
      localStorage.removeItem("crave-token");
      localStorage.removeItem("crave-user");
      window.dispatchEvent(new Event("crave-logout"));
    }
    return Promise.reject(error);
  },
);
export function errorMessage(error) {
  return (
    error.response?.data?.message ||
    (error.response?.status === 403
      ? "You do not have permission for this action."
      : "Unable to complete the request. Check that the backend is running.")
  );
}
export const money = (amount) =>
  `Rs. ${Number(amount).toLocaleString("en-LK", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
export default api;
