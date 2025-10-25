/*export type NotificationType = 'REWARD_GAINED' | 'FEEDBACK_RECEIVED';

export interface Notification {
  id: string;
  message: string;
  type: NotificationType;
  seen: boolean;
  createdAt: string;          // ISO
  // l’API renvoie un objet announcement ; on le “aplatit”
  announcementId?: string | null;
  announcementName?: string | null;
}
export interface AnnounceLite {
  idAnnouncement: string;
  announceName?: string;
}*/
export type NotificationType = 'REWARD_GAINED' | 'FEEDBACK_RECEIVED';

export interface Notification {
  id: string;                  // idnotif
  message: string;
  type: NotificationType;
  seen: boolean;
  createdAt: string;           // ISO date string
  announcementId?: string | null;
  announcementName?: string | null;
}