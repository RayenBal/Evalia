import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpErrorResponse, HttpParams,HttpHeaders  } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { SlimUser } from '../../calendar/planning/planningModel/planning.model';

export type TypeUser = 'Paneliste' | 'Announceur';

export interface RegisterRequest {
  firstname: string;
  lastname: string;
  email: string;
  password: string;
  numTelephone: string;
  typeUser: TypeUser;
  companyName?: string;
  jobTitle?: string;
  age?: number;
  deliveryAddress?: string;
  ageRange?: '18_25'|'26_35'|'36_45'|'46_60'|'60_plus';
  iban?: string;
 // role: string; // ⚠️ requis par ton backend
}

export interface AuthResponse {
  token: string;
  message: string;
  pending: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

private readonly API = 'http://localhost:8081/api/v1/auth';
private readonly baseUrl = 'http://localhost:8081/user';
  private readonly API_PW = `${this.API}/password`;

   constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  // ---------- Helpers SSR-safe ----------
  private get isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  /** Accès localStorage protégés */
  private lsGet(key: string): string | null {
    try { return this.isBrowser ? localStorage.getItem(key) : null; } catch { return null; }
  }
  private lsSet(key: string, val: string): void {
    try { if (this.isBrowser) localStorage.setItem(key, val); } catch {}
  }
  private lsRemove(key: string): void {
    try { if (this.isBrowser) localStorage.removeItem(key); } catch {}
  }

  /** Base64URL decode compatible navigateur + Node/SSR */
  private b64urlDecode(input: string): string {
    const pad = '='.repeat((4 - (input.length % 4)) % 4);
    const base64 = (input + pad).replace(/-/g, '+').replace(/_/g, '/');
    const g: any = globalThis as any;
    try {
      if (g.atob) return g.atob(base64);
      if (g.Buffer) return g.Buffer.from(base64, 'base64').toString('binary');
    } catch {}
    return '';
  }

  private decodeJwt<T = any>(token: string): T {
    const base64 = token.split('.')[1] || '';
    const json = this.b64urlDecode(base64);
    return json ? JSON.parse(json) as T : ({} as T);
  }

  // ---------- Register / Login etc. (inchangé) ----------


 // ---------- Register / Login etc. ----------
  // (garde ton ancienne méthode 'register' si tu veux — mais le backend attend maintenant du multipart)
  registerFormData(fd: FormData): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API}/register`, fd).pipe(
      catchError(this.handleError)
    );
  }

  /** Helper pour construire le FormData attendu par le backend Spring */
  buildRegisterFormData(data: RegisterRequest, registreCommerce?: File | null): FormData {
    const fd = new FormData();
    // clés = noms du DTO Spring RegisterRequest
    fd.append('firstname', data.firstname);
    fd.append('lastname', data.lastname);
    fd.append('email', data.email);
    fd.append('password', data.password);
    fd.append('numTelephone', data.numTelephone);
    fd.append('typeUser', data.typeUser);
    if (data.deliveryAddress != null)  fd.append('deliveryAddress', String(data.deliveryAddress)); 

    if (data.companyName != null) fd.append('companyName', String(data.companyName));
    if (data.jobTitle != null)   fd.append('jobTitle', String(data.jobTitle));
    if (data.ageRange != null)   fd.append('ageRange', String(data.ageRange));
    if (data.age != null)        fd.append('age', String(data.age)); 
    if (data.iban != null)           fd.append('iban', data.iban);
    if (registreCommerce)        fd.append('registreCommerce', registreCommerce);
    

    return fd;
  }







  
/*  register(body: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API}/register`, body).pipe(
      catchError(this.handleError)
    );
  }*/

  verifyEmail(code: string): Observable<string> {
    const params = new HttpParams().set('code', code);
    return this.http.get(`${this.API}/verify`, { params, responseType: 'text' }).pipe(
      catchError(this.handleErrorText)
    );
  }

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API}/authenticate`, { email, password }).pipe(
      catchError(this.handleError)
    );
  }

  confirmOtp(email: string, code: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API}/authenticate/first-login/verify`, { email, code }).pipe(
      catchError(this.handleError)
    );
  }

  forgotPassword(email: string): Observable<void> {
    return this.http.post<void>(`${this.API_PW}/forgot`, { email }).pipe(
      catchError(this.handleError)
    );
  }

  resetPassword(code: string, newPassword: string): Observable<void> {
    return this.http.post<void>(`${this.API_PW}/reset`, { code, newPassword }).pipe(
      catchError(this.handleError)
    );
  }

  // ---------- Token helpers (même signature, version SSR-safe) ----------
  setToken(token: string) { this.lsSet('auth_token', token); }
  getToken(): string | null { return this.lsGet('auth_token'); }
  isAuthenticated(): boolean { return !!this.getToken(); }
  clearToken() { this.lsRemove('auth_token'); }

  // ---------- Dérivés ----------
  get userType(): TypeUser | null {
    // Essaye d’abord le cache utilisateur si présent
    const raw = this.lsGet('user');
    if (raw) { try { return (JSON.parse(raw)?.typeUser as TypeUser) ?? null; } catch {} }

    // Sinon décode le JWT
    const t = this.getToken(); if (!t) return null;
    try {
      const payload = this.decodeJwt<any>(t);
      return (payload?.typeUser as TypeUser) ?? null;
    } catch { return null; }
  }

  get isPaneliste(): boolean { return this.userType === 'Paneliste'; }

  /** id utilisateur éventuel depuis le JWT */
  get userId(): number | null {
    const t = this.getToken(); if (!t) return null;
    try {
      const p: any = this.decodeJwt(t);
      const raw = p.id_user ?? p.userId ?? p.id ?? p.sub;
      const n = typeof raw === 'string' ? Number(raw) : Number(raw);
      return Number.isFinite(n) ? n : null;
    } catch { return null; }
  }

  /** email éventuel depuis le JWT */
  get email(): string | null {
    const t = this.getToken(); if (!t) return null;
    try {
      const p: any = this.decodeJwt(t);
      const e = p.email ?? (typeof p.sub === 'string' && p.sub.includes('@') ? p.sub : null);
      return e ?? null;
    } catch { return null; }
  }
get authHeader(): { [header: string]: string } {
  const tok = this.getToken();
  return tok ? { Authorization: `Bearer ${tok}` } : {};
}
  /*get authHeader() {
    const tok = this.getToken();
    return tok ? { Authorization: `Bearer ${tok}` } : {};
  }*/
/* get authHeaders(): HttpHeaders {
  const tok = this.getToken();
  return new HttpHeaders(tok ? { Authorization: `Bearer ${tok}` } : {});
}
get httpOptions() {
  return { headers: this.authHeaders };
}*/
  // ---------- Errors (inchangé) ----------
  private handleError = (err: HttpErrorResponse) => {
    const msg = err.error?.message || err.error || err.statusText || 'Erreur réseau';
    return throwError(() => new Error(msg));
  };

  private handleErrorText = (err: HttpErrorResponse) => {
    const msg = (typeof err.error === 'string' ? err.error : (err.statusText || 'Erreur'));
    return throwError(() => new Error(msg));
  };
  
   getPaneList(): Observable<SlimUser[]> {
       return this.http.get<SlimUser[]>(`${this.API}/getPanelist`);
    }
    getUserById(id : Number): Observable<SlimUser> {
       return this.http.get<SlimUser>(`${this.API}/getPanelist/`+id);
    }
}