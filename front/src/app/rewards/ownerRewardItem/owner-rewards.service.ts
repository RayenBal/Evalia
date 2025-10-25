
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type RewardType = 'Points' | 'Argent' | 'BonsDachats';
export type RewardStatus = 'PAS_ENCORE' | 'TRANSFERE';

export interface OwnerRewardItem {
  earnedRewardId: string;
  announcementId: string;
  announcementName: string;
  panelistId: number;
  panelistFirstname: string;
  panelistLastname: string;
  panelistEmail: string;
  panelistIban?: string | null;
  rewardType: RewardType;
  amount: string;
  status: RewardStatus;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class OwnerRewardsService {
  private base = 'http://localhost:8081/feedback';

  constructor(private http: HttpClient) {}

  listAll(): Observable<OwnerRewardItem[]> {
    return this.http.get<OwnerRewardItem[]>(
      `${this.base}/owner/rewards`,
      { withCredentials: true }
    );
  }

  listByAnnouncement(announcementId: string): Observable<OwnerRewardItem[]> {
    return this.http.get<OwnerRewardItem[]>(
      `${this.base}/owner/announces/${announcementId}/rewards`,
      { withCredentials: true }
    );
  }

  updateStatus(earnedRewardId: string, status: RewardStatus): Observable<void> {
    return this.http.put<void>(
      `${this.base}/owner/rewards/${earnedRewardId}/status`,
      { status },
      { withCredentials: true }
    );
  }
}
