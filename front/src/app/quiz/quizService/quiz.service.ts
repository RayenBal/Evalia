
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { quiz } from '../quizModel/quiz'; 
@Injectable({
  providedIn: 'root'
})
export class QuizService {

private baseUrl = 'http://localhost:8081/quiz';

  constructor(private http: HttpClient) { }

  // Récupérer tous les quiz
  getQuizList(): Observable<quiz[]> {
    return this.http.get<quiz[]>(`${this.baseUrl}/getAllQuiz`);
  }

  // Ajouter un quiz
  /*createQuiz(quizData: quiz): Observable<any> {
    return this.http.post(`${this.baseUrl}/addQuiz`, quizData, {
      headers: { 'Content-Type': 'application/json' }
    });
  }*/
createQuiz(quizData: quiz, announceId?: string): Observable<any> {
  const url = announceId
    ? `${this.baseUrl}/addQuiz?announceId=${announceId}`
    : `${this.baseUrl}/addQuiz`;
  return this.http.post(url, quizData, { headers: { 'Content-Type': 'application/json' } });
}

  // Supprimer un quiz
  deleteQuiz(id: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/deleteQuiz/${id}`, { responseType: 'text' });
  }

 


  // Détails d'un quiz
  getQuiz(id: string): Observable<quiz> {
    return this.http.get<quiz>(`${this.baseUrl}/getDetailsQuiz/${id}`);
  }

  // Mise à jour d’un quiz
  updateQuiz(id: string, quiz: quiz): Observable<Object> {
    return this.http.put(`${this.baseUrl}/updateQuiz/${id}`, quiz);
  }
}
