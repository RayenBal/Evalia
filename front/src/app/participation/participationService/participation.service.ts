import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
export interface QuizAttempt {
  idAttempt: string;
  status: 'STARTED' | 'SUBMITTED';
}
export interface AttemptAnswerView {
  questionId: string;
  questionContent: string;
  selectedResponseId: string;
  selectedResponseContent: string;
  freeText: string | null;
}

export interface QuizAttemptView {
  idAttempt: string;
  quizId: string;
  quizContent: string;
  panelistEmail: string;
  panelistName: string;
  startedAt: string;
  deliveryAddress:string;
  numTelephone:string;

 // submittedAt: string;
  //status: 'STARTED'|'SUBMITTED';
   submittedAt: string | null;      // le back renvoie submittedAt, pas startedAt/panelistName/quizId
  status: string; 
  answers: AttemptAnswerView[];
}



@Injectable({
  providedIn: 'root'
})
export class ParticipationService {

 private base = 'http://localhost:8081/participation';
  constructor(private http: HttpClient) {}

  /*startAttempt(announceId: string, quizId: string, panelistId: number): Observable<QuizAttempt> {
    return this.http.post<QuizAttempt>(
      `${this.base}/announces/${announceId}/quizzes/${quizId}/start?panelistId=${panelistId}`,
      {}
    );
  }

  submitRaw(attemptId: string, answers: any[]): Observable<QuizAttempt> {
    return this.http.post<QuizAttempt>(`${this.base}/attempts/${attemptId}/submit-raw`, answers);
  }*/
/*startAttempt(announceId: string, quizId: string) {
  return this.http.post<any>(`${this.base}/announces/${announceId}/quizzes/${quizId}/start`, null);
}
submitRaw(attemptId: string, answers: any[]) {
  return this.http.post<any>(`${this.base}/attempts/${attemptId}/submit-raw`, answers);
}
getAttemptsForAnnounce(announceId: string) {
  return this.http.get<QuizAttemptView[]>(
    `${this.base}/announces/${announceId}/attempts` 
  );
}*/

startAttempt(announceId: string, quizId: string) {
  return this.http.post<any>(
    `${this.base}/announces/${announceId}/quizzes/${quizId}/start`,
    null,
    { withCredentials: true }
  );
}
submitRaw(attemptId: string, answers: any[]) {
  return this.http.post<any>(
    `${this.base}/attempts/${attemptId}/submit-raw`,
    answers,
    { withCredentials: true }
  );
}
getAttemptsForAnnounce(announceId: string) {
  return this.http.get<QuizAttemptView[]>(
    `${this.base}/announces/${announceId}/attempts`,
    { withCredentials: true } // optionnel si route ouverte, mais safe
  );
}
getAttemptsForAnnounceAndQuiz(announceId: string, quizId: string) {
  return this.http.get<QuizAttemptView[]>(
    `${this.base}/announces/${announceId}/quizzes/${quizId}/attempts`,
    { withCredentials: true }
  );
}
}