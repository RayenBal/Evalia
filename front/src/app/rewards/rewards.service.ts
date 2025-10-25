import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { RewardStatus } from './ownerRewardItem/owner-rewards.service';
export type RewardType = 'Points' | 'Argent' | 'BonsDachats';

export interface PanelistRewardItem {
  announcementId: string;
  announcementName: string;
  rewardType: RewardType;
  amount: string;   // BigDecimal en string
  status: RewardStatus;
  createdAt: string;
}
export interface PanelistRewardsDto {
  items: PanelistRewardItem[];
  totals: Record<RewardType, string>;
}

@Injectable({ providedIn: 'root' })
export class RewardsService {
  private baseUrl = 'http://localhost:8081/feedback/me/rewards';
  constructor(private http: HttpClient) {}
  getMine(): Observable<PanelistRewardsDto> {
    return this.http.get<PanelistRewardsDto>(
      this.baseUrl,
      { withCredentials: true }                  // 👈
    ).pipe(
      map(dto => ({
        items: dto.items ?? [],
        totals: dto.totals ?? {} as any
      }))
    );
  }
  /*getMine(): Observable<PanelistRewardsDto> {
    return this.http.get<PanelistRewardsDto>(this.baseUrl).pipe(
      map(dto => ({
        items: dto.items ?? [],
        totals: dto.totals ?? {} as any
      }))
    );
  }*/
}
