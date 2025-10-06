import { useState } from 'react';
export default function DishImage({ src, name }) {
  const [failed, setFailed] = useState(false);
  return src && !failed ? <img className="dish-image" src={src} alt={name} loading="lazy" onError={() => setFailed(true)}/> : <div className="dish-image image-placeholder" role="img" aria-label={name}>Made with care</div>;
}
