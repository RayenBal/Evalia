// Angular 15+ : intercepteur fonctionnel
import { HttpInterceptorFn } from '@angular/common/http';

//const API_BASES = ['http://localhost:8081']; // tes backends
const API_BASES = [
  'http://localhost:8081/api',
  'http://localhost:8081/participation',
  'http://localhost:8081/Announcement',
  'http://localhost:8081/feedback',
  'http://localhost:8081/notifications',
  'http://localhost:8081/categories',
];
const PUBLIC_ENDPOINTS = [
  'http://localhost:8081/api/ai/chat',
];

//const BACKEND_ROOT = 'http://localhost:8081/';
export const authInterceptor: HttpInterceptorFn = (req, next) => {
   const isPublic = PUBLIC_ENDPOINTS.some(b => req.url.startsWith(b));

  const isApiCall = API_BASES.some(b => req.url.startsWith(b));
    if (isPublic) {
    return next(req);
  }
  if (!isApiCall) return next(req);

  // Vérifier si nous sommes dans le navigateur (et non côté serveur)
  if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
    const token = localStorage.getItem('auth_token');
    if (token) {
      req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
    }
  }
  return next(req);
};
