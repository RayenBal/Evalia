import { Injectable, NgZone } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, interval, of, Subject } from 'rxjs';
import { catchError, map, switchMap, takeUntil, tap } from 'rxjs/operators';
import { Notification } from '../notificationModel/notification';


// ⚠️ on utilise EventSourcePolyfill pour pouvoir passer des headers (token)
import { EventSourcePolyfill } from 'event-source-polyfill';

@Injectable({
  providedIn: 'root'
})
export class NotificationService { private baseUrl = 'http://localhost:8081/notifications';

 /** Store local en mémoire (état réactif) */
  private _items  = new BehaviorSubject<Notification[]>([]);
  private _unseen = new BehaviorSubject<number>(0);
  private _open   = new BehaviorSubject<boolean>(false);

  /** Gestion du flux SSE */
  private stop$ = new Subject<void>();
  private es?: EventSourcePolyfill;
  private connected = false;          // ✅ nouvel état


  /** Observables publics consommés par tes composants */
  readonly items$  = this._items.asObservable();
  readonly unseen$ = this._unseen.asObservable();
  readonly open$   = this._open.asObservable();

  constructor(private http: HttpClient, private zone: NgZone) {}
  

  // ---------------------------
  // Utils
  // ---------------------------

  /** Récupère le JWT du localStorage pour le SSE (l’intercepteur ne s’applique pas à EventSource). */
  private token(): string | null {
    try {
      if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
        return localStorage.getItem('auth_token');
      }
    } catch {}
    return null;
  }
  ensureConnected(): void {
  if (!this.connected) {
    this.initSse();
  }
}

  // ---------------------------
  // REST (HTTP)
  // ---------------------------

  /**
   * Charge la liste des notifications de l’utilisateur courant.
   * Le backend renvoie déjà un DTO plat: Notification[].
   * L’intercepteur ajoute l’Authorization automatiquement.
   */
  listMine() {
    return this.http
      .get<Notification[]>(`${this.baseUrl}/me`, { withCredentials: true })
      .pipe(tap(list => this._items.next(list ?? [])));
  }

  /** Récupère le compteur de notifications non lues. */
  fetchUnseenCount() {
    return this.http
      .get<{ count: number }>(`${this.baseUrl}/me/unseen-count`, { withCredentials: true })
      .pipe(tap(r => this._unseen.next(r?.count ?? 0)));
  }

  /** Marque une notification comme lue (optimistic update côté client). */
  markSeen(id: string) {
    return this.http
      .post<void>(`${this.baseUrl}/${id}/seen`, {}, { withCredentials: true })
      .pipe(
        tap(() => {
          const list = this._items.getValue().map(n => (n.id === id ? { ...n, seen: true } : n));
          this._items.next(list);
          this._unseen.next(Math.max(0, this._unseen.getValue() - 1));
        })
      );
  }

  /** Marque toutes les notifications comme lues. */
  markAllSeen() {
    return this.http
      .post<void>(`${this.baseUrl}/seen/all`, {}, { withCredentials: true })
      .pipe(
        tap(() => {
          const list = this._items.getValue().map(n => ({ ...n, seen: true }));
          this._items.next(list);
          this._unseen.next(0);
        })
      );
  }

  // ---------------------------
  // UI
  // ---------------------------

  /** Ouvre / ferme le dropdown (piloté depuis la cloche). */
  setOpen(v: boolean) {
    this._open.next(v);
  }

  // ---------------------------
  // SSE (temps réel)
  // ---------------------------

  /**
   * Initialise le flux:
   *  1) charge l’état initial (liste + compteur)
   *  2) ouvre la connexion SSE pour recevoir les nouvelles notifs en direct
   */


  initSse(): void {
  // évite les doublons
  this.destroySse();
  this.connected = false;           // on repart propre

  const token = this.token();
  if (!token) return;

  this.listMine().pipe(
    switchMap(() => this.fetchUnseenCount())
  ).subscribe({
    next: () => this.openSse(token),
    error: () => this.openSse(token)
  });
}
  /*initSse(): void {
    // Évite les connexions multiples
    this.destroySse();

    const token = this.token();
    if (!token) {
      // Pas connecté → pas de flux
      return;
    }

    // Pré-charge l'état, puis ouvre le flux
    this.listMine()
      .pipe(switchMap(() => this.fetchUnseenCount()))
      .subscribe({
        next: () => this.openSse(token),
        error: () => this.openSse(token) // on tente quand même d'ouvrir le flux
      });
  }*/

  /** Ouvre la connexion SSE en ajoutant Authorization via EventSourcePolyfill. */
  private openSse(token: string) {
    this.es = new EventSourcePolyfill(`${this.baseUrl}/stream`, {
      headers: { Authorization: `Bearer ${token}` },
      withCredentials: true
    });

    // Petit hello envoyé par Spring à l’ouverture (facultatif)
    //this.es.addEventListener('hello', () => { /* no-op */ });
  this.es.addEventListener('hello', () => {
    this.zone.run(() => this.connected = true);   // ✅ on marque connecté
  });
    // Réception d’une nouvelle notification
    this.es.addEventListener('notification', (e: MessageEvent) => {
      // Important: repasser dans la NgZone pour déclencher le change detection
      this.zone.run(() => {
        try {
          const n = JSON.parse((e as any).data) as Notification; // DTO plat
          this._items.next([n, ...this._items.getValue()]);
          if (!n.seen) this._unseen.next(this._unseen.getValue() + 1);
        } catch {
          // ignore parse errors
        }
      });
    });

    // (Optionnel) Keep-alive si le backend envoie des "ping"
    this.es.addEventListener('ping', () => { /* no-op */ });

    // (Simple) stratégie de reconnexion si le flux se ferme
    const sub = interval(10_000)
      .pipe(takeUntil(this.stop$))
      .subscribe(() => {
        // 2 = CLOSED
        if (this.es && (this.es as any).readyState === 2) {
          sub.unsubscribe();
          this.initSse();
        }
      });
  }

  /** Ferme proprement la connexion SSE. À appeler au destroy global si nécessaire. */
  destroySse(): void {
    this.stop$.next();
    if (this.es) {
      try {
        this.es.close();
      } catch {}
      this.es = undefined;
        this.connected = false; 
    }
  }
}