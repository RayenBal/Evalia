import { Injectable } from '@angular/core';
import { reclamation } from '../reclamationModel/reclamation';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject, catchError, forkJoin, map, tap } from 'rxjs';
import { AuthService } from '../../auth/auth-service/auth.service';
@Injectable({
  providedIn: 'root'
})
export class ReclamationService {

private baseUrl = 'http://localhost:8081/reclamation'
 constructor(private http: HttpClient, private auth: AuthService) { }
//  private _refreshRequired = new Subject<void>();
  //get refreshRequired() {
   // return this._refreshRequired;
  //}
private get opts() { return { headers: this.auth.authHeader }; }


 
  createReclamation(body: reclamation) {
    return this.http.post<reclamation>(`${this.baseUrl}/addReclamation`, body, this.opts);
  }
  updateReclamation(id: string, body: reclamation) {
    return this.http.put<reclamation>(`${this.baseUrl}/updateReclamation/${id}`, body, this.opts);
  }
  listMine() {
    return this.http.get<reclamation[]>(`${this.baseUrl}/mine`, this.opts);
  }
  getOne(id: string) {
    return this.http.get<reclamation>(`${this.baseUrl}/getDetailsReclamation/${id}`, this.opts);
  }
  delete(id: string) {
    return this.http.delete<void>(`${this.baseUrl}/deleteReclamation/${id}`, this.opts);
  }
}
















 /*getReclamationList(): Observable<reclamation[]> {

     return this.http.get<reclamation[]>(`${this.baseUrl}/getAllReclamation`);
  }

  


 createReclamation(reclamationData: any): Observable<any> {
    const headers = { 'Content-Type': 'application/json' };
    return this.http.post(`${this.baseUrl}` + '/addReclamation', reclamationData, { headers });
  }


 deleteReclamation(id: String): Observable<any> {
    return this.http.delete(`${this.baseUrl}/deleteReclamation/${id}`, { responseType: 'text' });
  }

 getReclamation(id: string): Observable<reclamation> {
    return this.http.get<reclamation>(`${this.baseUrl}/getDetailsReclamation/${id}`);
  }
updateReclamation(id: String, reclamation: reclamation): Observable<Object> {
    return this.http.put(`${this.baseUrl}/updateReclamation/${id}`, reclamation);
  }

*/   
 

