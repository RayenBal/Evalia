import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { question } from '../questionModel/question';

@Injectable({
  providedIn: 'root'
})
export class QuestionService {

  private baseUrl = 'http://localhost:8081/question';

  constructor(private http: HttpClient) {}

  getQuestionList(): Observable<question[]> {
    return this.http.get<question[]>(`${this.baseUrl}/all`);
  }

  createQuestion(question: question): Observable<any> {
    return this.http.post(`${this.baseUrl}/add`, question);
  }

  updateQuestion(id: string, question: question): Observable<any> {
    return this.http.put(`${this.baseUrl}/update/${id}`, question);
  }

  deleteQuestion(id: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/delete/${id}`, { responseType: 'text' });
  }

  getQuestion(id: string): Observable<question> {
    return this.http.get<question>(`${this.baseUrl}/get/${id}`);
  }

  getQuestionsByQuizId(id: string): Observable<question[]> {
    return this.http.get<question[]>(`${this.baseUrl}/byQuiz/${id}`);
  }
}
