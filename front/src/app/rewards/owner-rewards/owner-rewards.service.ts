import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type RewardType = 'Points' | 'Argent' | 'BonsDachats';

export interface OwnerRewardItem {
  announcementId: string;
  announcementName: string;
  panelistId: number;
  panelistFirstname: string;
  panelistLastname: string;
  panelistEmail: string;
  rewardType: RewardType;
  amount: string;        // BigDecimal -> string
  createdAt: string;     // ISO*/
}

@Injectable({ providedIn: 'root' })
export class OwnerRewardsService {
  private base = 'http://localhost:8081/feedback';

  constructor(private http: HttpClient) {}

  // toutes les annonces de l’annonceur connecté
  listAll(): Observable<OwnerRewardItem[]> {
    return this.http.get<OwnerRewardItem[]>(
      `${this.base}/owner/rewards`,
      { withCredentials: true } // IMPORTANT pour la session
    );
  }

  // une annonce précise
  listByAnnouncement(announcementId: string): Observable<OwnerRewardItem[]> {
    return this.http.get<OwnerRewardItem[]>(
      `${this.base}/owner/announces/${announcementId}/rewards`,
      { withCredentials: true }
    );
  }
}