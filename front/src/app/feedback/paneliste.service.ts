import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth/auth-service/auth.service'; // adapte le chemin si besoin
import { Observable } from 'rxjs';
import { announce } from '../Announce/AnnounceModel/announce';

export type RewardType = 'Points' | 'Argent' | 'BonsDachats';

export interface MyFeedbackItem {
  announcementId: string;
  announcementName: string;
  rating?: number;
  comment?: string;
  createdAt: string; // ISO
}

export interface RewardItem {
  announcementId: string;
  announcementName: string;
  rewardType: RewardType;
  amount: number;
  createdAt: string;
  status:string;
}

export interface RewardTotals {
  Points?: number;
  Argent?: number;
  BonsDachats?: number;
}

export interface PanelistRewards {
  items: RewardItem[];
  totals: RewardTotals;
}

@Injectable({ providedIn: 'root' })
export class PanelisteService {
   private base = 'http://localhost:8081';

  constructor(private http: HttpClient) {}

  /** toutes les annonces (publiques) */
  allAnnounces() {
    return this.http.get<any[]>(`${this.base}/Announcement/getAllAnnounces`);
  }

  /** mes feedbacks (auth requis) */
  myFeedbacks(): Observable<MyFeedbackItem[]> {
    return this.http.get<MyFeedbackItem[]>(`${this.base}/feedback/me`);
  }

  /** mes récompenses (auth requis) */
  myRewards(): Observable<PanelistRewards> {
    return this.http.get<PanelistRewards>(`${this.base}/feedback/me/rewards`);
  }}