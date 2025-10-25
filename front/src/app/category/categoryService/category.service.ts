import { Injectable } from '@angular/core';
import { Category } from '../categoryModel/category';
import { HttpClient } from '@angular/common/http';
import { Observable} from 'rxjs';
import { announce } from '../../Announce/AnnounceModel/announce';
@Injectable({
  providedIn: 'root'
})
export class CategoryService {

 private baseUrl = 'http://localhost:8081/categories'
  constructor(private http: HttpClient) { }
//private readonly API = 'http://localhost:8081/categories';
 
 /* getcategorieList(): Observable<Category[]> {
      return this.http.get<Category[]>(`${this.baseUrl}` + '/getAllCategory');
   }*/
   getcategorieList(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.baseUrl}/getAllCategory`);
  }
 
 
   getCategory(id: string): Observable<Category> {
   return this.http.get<Category>(`${this.baseUrl}/getDetailsCategory/${id}`);
 }
   getAnnouncesByCategory(id: string | number): Observable<announce[]> {
    return this.http.get<announce[]>(`${this.baseUrl}/${id}/announces`);
  }
}
