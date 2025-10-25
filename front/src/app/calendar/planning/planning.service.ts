// src/app/services/appointment.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Planning,AppointmentStatus,UpdatePlanningDto  } from './planningModel/planning.model';
import { Observable } from 'rxjs';

const API = 'http://localhost:8081/plannings'
; // adapte si tu as un prefix comme environment.apiUrl


export interface SlotCreateDto {
  announcementId: string;
  slots: { startsAt: string; endsAt: string ; panelistId: string}[];
  
}

export interface AssignPanelistDto {
  panelistId: number;
}

export interface UpdateStatusDto {
  status: AppointmentStatus;
}

@Injectable({ providedIn: 'root' })
export class PlanningService {

  //private baseUrl = 'http://localhost:8081/plannings'
  constructor(private http: HttpClient) {}

  // Calendriers
  listMine(id : Number): Observable<Planning[]> {
    return this.http.get<Planning[]>(`${API}/mine/`+id, { withCredentials: true });
  }
  listOwner(id : Number): Observable<Planning[]> {
    return this.http.get<Planning[]>(`${API}/owner/`+id, { withCredentials: true });
  }
  listByAnnouncement(annId: string): Observable<Planning[]> {
    return this.http.get<Planning[]>(`${API}/by-announcement/${annId}`, { withCredentials: true });
  }
  // *** NEW: PUT update complet ***
  // *** NEW: PUT update complet ***
  update(id: string, dto: UpdatePlanningDto): Observable<Planning> {
    return this.http.put<Planning>(`${API}/${id}`, dto, { withCredentials: true });
  }

  // *** NEW: DELETE ***
  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${API}/${id}`, { withCredentials: true });
  }
  // Planning
  createSlots(id : Number,dto: SlotCreateDto): Observable<Planning[]> {
    return this.http.post<Planning[]>(`${API}/slots/`+id, dto, { withCredentials: true });
  }
  assign(appointmentId: string, body: AssignPanelistDto): Observable<Planning> {
    return this.http.post<Planning>(`${API}/${appointmentId}/assign`, body, { withCredentials: true });
  }

   /*updateStatus(appointmentId: string, body: UpdateStatusDto): Observable<Planning> {
    return this.http.patch<Planning>(`${API}/${appointmentId}/status`, body, { withCredentials: true });
  }*/

  // planning.service.ts
/*updateStatus(appointmentId: string,  body: { status: AppointmentStatus }, actorId: number): Observable<Planning> {
  return this.http.patch<Planning>(
    `${API}/${appointmentId}/status?actorId=${actorId}`,
    body,
    { withCredentials: true }
  );
}*/
updateStatus(appointmentId: string, body: UpdateStatusDto, actorId: number) {
  return this.http.patch<Planning>(
    `${API}/${appointmentId}/status?actorId=${actorId}`,
    body,
    { withCredentials: true }
  );
}
}
