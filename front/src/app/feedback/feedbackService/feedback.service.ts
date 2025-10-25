import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { feedback } from '../feedbackModel/feedback'; 

@Injectable({
  providedIn: 'root'
})
export class FeedbackService {

 private baseUrl = 'http://localhost:8081/feedback';
  constructor(private http: HttpClient) {}

  /** ✅ envoie simple sans image (multipart mais seulement des champs texte) */
  createSimple(
    announcementId: string,
   // panelistId: number,
    rating: number | null,
    comment: string | null
  ): Observable<feedback> {
    const form = new FormData();
    //form.append('panelistId', String(panelistId));
    if (rating != null) form.append('rating', String(rating));
   if (comment != null) form.append('comment', comment);

    return this.http.post<feedback>(
      `${this.baseUrl}/simple/announces/${announcementId}`,
      form,
      { withCredentials: true } 
    );
  }

  listByAnnouncement(announcementId: string): Observable<feedback[]> {
    return this.http.get<feedback[]>(
      `${this.baseUrl}/announces/${announcementId}`,
      { withCredentials: true } 
    );
  }

     getMine(announcementId: string) {
    return this.http.get<feedback>(
      `${this.baseUrl}/mine/announces/${announcementId}`,
      { observe: 'response', withCredentials: true }   // 👈
    );
  }

  // PUT update explicite (JSON)
  updateMine(announcementId: string, rating: number|null, comment: string|null) {
    return this.http.put<feedback>(`${this.baseUrl}/mine/announces/${announcementId}`, { rating, comment });
  }
/*  listByAnnouncement(announcementId: string): Observable<feedback[]> {
    return this.http.get<feedback[]>(`${this.base}/announces/${announcementId}`);
  }*/
}
