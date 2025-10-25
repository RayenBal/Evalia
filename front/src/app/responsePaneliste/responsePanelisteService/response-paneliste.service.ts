import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ResponsePaneliste } from '../ResponsePanelisteModel/responsepaneliste';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ResponsePanelisteService {

  private baseUrl = 'http://localhost:8081/reponsepaneliste';

  constructor(private http: HttpClient) {}

  // 🔹 Ajouter une réponse
  addResponse(response: ResponsePaneliste): Observable<ResponsePaneliste> {
    return this.http.post<ResponsePaneliste>(`${this.baseUrl}/add`, response);
  }

  // 🔹 Mettre à jour une réponse
  updateResponse(id: string, response: ResponsePaneliste): Observable<ResponsePaneliste> {
    return this.http.put<ResponsePaneliste>(`${this.baseUrl}/update/${id}`, response);
  }

  // 🔹 Récupérer une réponse par ID
  getResponseById(id: string): Observable<ResponsePaneliste> {
    return this.http.get<ResponsePaneliste>(`${this.baseUrl}/get/${id}`);
  }

  // 🔹 Supprimer une réponse
  deleteResponse(id: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/delete/${id}`, { responseType: 'text' });
  }

  // 🔹 Récupérer toutes les réponses
  getAllResponses(): Observable<ResponsePaneliste[]> {
    return this.http.get<ResponsePaneliste[]>(`${this.baseUrl}/all`);
  }

  // 🔹 Récupérer les réponses d'une question
  getResponsesByQuestionId(questionId: string): Observable<ResponsePaneliste[]> {
    return this.http.get<ResponsePaneliste[]>(`${this.baseUrl}/byQuestion/${questionId}`);
  }
}
