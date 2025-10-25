import { Injectable } from '@angular/core';
import { recompenses,TypeRecompenses  } from '../recompensesModel/recompenses';
import { AuthService } from '../../auth/auth-service/auth.service';

import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';
export interface RewardSummary {
  text: string;                                  // ex: "50 pts · 1 bon · 30 €"
  items: { type: TypeRecompenses; amount: number; label?: string }[];
}
@Injectable({
  providedIn: 'root'
})

export class RecompensesService {

 private baseUrl = 'http://localhost:8081/recompenses';
  private get opts() { return { headers: this.auth.authHeader }; } // si tes routes sont protégées

  constructor(private http: HttpClient, private auth: AuthService) {}

  getAllRecompenses(): Observable<recompenses[]> {
    return this.http.get<recompenses[]>(`${this.baseUrl}/getAllRecompenses`);
  }

  getRecompensesById(id: string): Observable<recompenses> {
    return this.http.get<recompenses>(`${this.baseUrl}/getDetailsRecompenses/${id}`);
  }

  createRecompenses(recompense: recompenses): Observable<recompenses> {
    return this.http.post<recompenses>(`${this.baseUrl}/addRecomponses`, recompense);
  }

  updateRecompenses(id: string, recompense: recompenses): Observable<recompenses> {
    return this.http.put<recompenses>(`${this.baseUrl}/updateRecompenses/${id}`, recompense);
  }

  deleteRecompenses(id: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/deleteRecompenses/${id}`, { responseType: 'text' });
  }
    // ⬇️ adapte le path au tien: /byAnnouncement/{id} ou /announcement/{id}
  getForAnnouncement(announcementId: string): Observable<recompenses[]> {
    return this.http.get<recompenses[]>(
      `${this.baseUrl}/byAnnouncement/${announcementId}`, this.opts
    );
  }
  /** Public (paneliste) → IMPORTANT: renvoyer les ITEMS, pas un summary */
 /* getForAnnouncementPublic(announcementId: string): Observable<recompenses[]> {
    return this.http.get<recompenses[]>(
      `${this.baseUrl}/public/byAnnouncement/${announcementId}`, this.opts
    );
  }*/
   getForAnnouncementPublic(announcementId: string): Observable<recompenses[]> {
    // IMPORTANT: pas de headers d’auth pour une route publique
    return this.http.get<recompenses[]>(`${this.baseUrl}/byAnnouncement/${announcementId}`);
  }

  getForAnnouncementPublicall(announcementId: string) {
  // PAS d'opts ici si l’endpoint est public
  return this.http.get<recompenses[]>(
    `${this.baseUrl}/public/byAnnouncement/${announcementId}`
  );
}

  // Petit helper pour fabriquer un résumé lisible
  summarize(list: recompenses[]): RewardSummary {
    //const items: RewardSummary['items'] = list?.map(r => ({
        const items: RewardSummary['items'] = (list ?? []).map(r => ({
  
    type: r.typeRecompenses,
      amount: r.amount,
      label: r.label
    })) ?? [];
 const parts = items.map(it => {
      switch (it.type) {
        case TypeRecompenses.Points:      return `${it.amount} pts`;
        case TypeRecompenses.BonsDachats: return it.label ? it.label : `Bon (${it.amount})`;
        case TypeRecompenses.Argent:      return `${it.amount} dt`;
        default:                          return `${it.amount}`;
      }
    });

    return { text: parts.join(' · '), items };
  
  
}}