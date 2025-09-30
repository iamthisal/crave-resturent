import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
export default function App() {
  return <BrowserRouter><Navbar/><main className="container"><Routes><Route path="*" element={<h1>Welcome to Crave</h1>}/></Routes></main></BrowserRouter>;
}
