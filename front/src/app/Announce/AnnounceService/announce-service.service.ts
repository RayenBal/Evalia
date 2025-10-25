import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject, catchError, forkJoin, map, tap } from 'rxjs';
import { announce } from '../AnnounceModel/announce';
import { Category } from '../../category/categoryModel/category';
import { RecompensePayload,RecompenseNew } from '../../recompenses/recompensesModel/recompenses';
//import { TokenStorageService } from 'src/app/service/token-storage-service.service';
@Injectable({
  providedIn: 'root'
})
export class AnnounceServiceService {
private baseUrl = 'http://localhost:8081/Announcement'
 constructor(private http: HttpClient) { }
//  private _refreshRequired = new Subject<void>();
  //get refreshRequired() {
   // return this._refreshRequired;
  //}

 getAnnounceList(): Observable<announce[]> {
    //return this.http.get<announce[]>(`${this.baseUrl}` + '/getAllAnnounces');
     return this.http.get<announce[]>(`${this.baseUrl}/getAllAnnounces`);
  }
getByCategory(categoryId: number | string) {
  const id = typeof categoryId === 'string' ? Number(categoryId) : categoryId;
  return this.http.get<announce[]>(`http://localhost:8081/Announcement/by-category/${id}`);
}


 createAnnounce(announceData: any): Observable<any> {
    //const headers = { 'Content-Type': 'application/json' };
    return this.http.post(`${this.baseUrl}/addAnnounce`, announceData);
  }
  

/*
    createAnnounce(announceData: announce): Observable<announce> {
  return this.http.post<announce>(`${this.baseUrl}/addAnnounce`, announceData);
  
}*/

 deleteAnnounce(id: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/deleteAnnouncement/${id}`, { responseType: 'text' });
  }


  getAnnounce(id: string): Observable<announce> {
  return this.http.get<announce>(`${this.baseUrl}/getDetailsAnnouncement/${id}`);
}

/*updateAnnouncee(id: String, announce: announce): Observable<Object> {
    return this.http.put(`${this.baseUrl}/modifierAnnounce/${id}`, announce);
  }*/
  updateAnnounceWithImages(id: string, formData: FormData): Observable<any> {
  return this.http.post(`${this.baseUrl}/updateAnnounceWithImages/${id}`, formData);
}

   uploadPhoto(id: string, file: File): Observable<any> {
    const uploadUrl = `${this.baseUrl}/uploadAnnounce/${id}`;
    

    const formData: FormData = new FormData();
    formData.append('photo', file, file.name);

    return this.http.post(uploadUrl, formData);
  }

   getPhoto(photo: String): string {
    const photoUrl = `${this.baseUrl}/downloadannounce/${photo}`;

    return `${this.baseUrl}/downloadannounce/${photo}`;
  }

 getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>('http://localhost:8081/categories/getAllCategory');
  }
  getMyAnnounces() {
return this.http.get<announce[]>(`${this.baseUrl}/mine`);}}
