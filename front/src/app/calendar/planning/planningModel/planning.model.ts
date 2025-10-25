// src/app/models/appointment.model.ts
export type AppointmentStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED' ;

export interface SlimUser {
  id_user: number;
  firstname?: string;
  lastname?: string;
  email?: string;
}

export interface SlimAnnounce {
  idAnnouncement: string;
  announceName?: string;
}

export class Planning {
  id!: string;
  announcement!: SlimAnnounce;
  owner!: SlimUser;
  panelist!: SlimUser | null;
  startsAt!: string; // ISO string from API
  endsAt!: string;   // ISO string from API
  status!: AppointmentStatus;
  panelistId?: number | null;
     constructor(planning?: Planning) {
      if (planning) {
        this.id = planning.id;
  
        // Validate the date before trying to convert it
        const isValidDate = Date.parse(planning.startsAt);
        if (!isNaN(isValidDate)) {
          this.startsAt = new Date(planning.startsAt).toISOString().split('T')[0];
        } else {
        
          this.startsAt = ""; // Set a default value or handle accordingly
        }
   const isValidDateEnd = Date.parse(planning.endsAt);
        if (!isNaN(isValidDate)) {
          this.endsAt = new Date(planning.endsAt).toISOString().split('T')[0];
        } else {
        
          this.endsAt = ""; // Set a default value or handle accordingly
        }
        this.panelist = planning.panelist;
        this.status = planning.status;
        this.announcement = planning.announcement;
        this.owner = planning.owner;
      }
    }
}


export interface UpdatePlanningDto {
  announcementId?: string | null;
  panelistId?: number | null;     // <= null ou <=0 pour “retirer”
  startsAt?: string | null;       // ISO (yyyy-MM-ddTHH:mm:ss)
  endsAt?: string | null;         // ISO
  status?: AppointmentStatus | null;
}

// Helper
export function fullName(u?: SlimUser | null) {
  if (!u) return '';
  return [u.firstname, u.lastname].filter(Boolean).join(' ');


  
}
