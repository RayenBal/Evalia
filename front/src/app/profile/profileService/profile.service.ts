import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../../auth/auth-service/auth.service'; 
import { UpdateMe ,MeProfile} from '../profile.service.types';
@Injectable({ providedIn: 'root' })
export class ProfileService {
  private base = 'http://localhost:8081/api/v1/profile';
  constructor(private http: HttpClient, private auth: AuthService) {}

  me(): Observable<MeProfile> {
    return this.http.get<MeProfile>(`${this.base}/me`, { headers: this.auth.authHeader });
  }

  updateMe(patch: UpdateMe): Observable<MeProfile> {
    return this.http.put<MeProfile>(`${this.base}/me`, patch, { headers: this.auth.authHeader });
  }

  changePassword(currentPassword: string, newPassword: string): Observable<void> {
    return this.http.put<void>(`${this.base}/password`, { currentPassword, newPassword },
      { headers: this.auth.authHeader });
  }
}
