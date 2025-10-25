import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
export interface FeedbackOwnerDto {
  idFeedback: string;
  rating: number | null;
  comment: string | null;
  createdAt: string;
  panelistId: number;
  panelistName: string;
  panelistEmail: string;
  announcementId: string;
}
export interface FeedbackStatsDto {
  total: number;
  average: number;
  stars1?: number; stars2?: number; stars3?: number; stars4?: number; stars5?: number;
}
//export interface FeedbackStatsDto { count: number; avg: number | null; }
//export interface OwnerFeedbackItemDto { stats: FeedbackStatsDto; items: FeedbackOwnerDto[]; }
export interface OwnerFeedbackItemDto {
  idFeedback: string;
  createdAt: string;         // ISO string
  rating: number | null;
  comment: string | null;
  panelistId: number;
  panelistFirstname: string | null;
  panelistLastname: string | null;
}
@Injectable({
  providedIn: 'root'
})
export class FeedbackOwnerService {
  private baseUrl = 'http://localhost:8081/feedback';
  constructor(private http: HttpClient) {}

/*  byAnnouncement(announcementId: string) {
    return this.http.get<OwnerFeedbackResponse>(`${this.base}/announces/${announcementId}`);
  }
  allMine() {
    return this.http.get<FeedbackOwnerDto[]>(`${this.base}/all`);
  }*/

stats(announcementId: string) {
    return this.http.get<FeedbackStatsDto>(
      `${this.baseUrl}/announces/${announcementId}/stats`,
      { withCredentials: true }
    );
  }

  list(announcementId: string) {
    return this.http.get<OwnerFeedbackItemDto[]>(
      `${this.baseUrl}/owner/announces/${announcementId}`,
      { withCredentials: true }
    );
  }

}