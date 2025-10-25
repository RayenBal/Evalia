import { TypeUser } from '../auth/auth-service/auth.service';

export interface MeProfile {
  id_user: number;
  firstname: string;
  lastname: string;
  email: string;
  numTelephone?: string | null;
  typeUser: TypeUser;
  companyName?: string | null; // Announceur
  jobTitle?: string | null;    // Paneliste
  ageRange?: '18_25'|'26_35'|'36_45'|'46_60'|'60_plus' | null;
  iban?: string | null;
  deliveryAddress?: string | null;
  
  verified: boolean;
  enabled: boolean;
  needsAdminValidation: boolean;
  firstLoginCompleted: boolean;
}

export type UpdateMe =
  Partial<Pick<MeProfile,'firstname'|'lastname'|'numTelephone'|'companyName'|'jobTitle'|'ageRange'|'iban'|'deliveryAddress'>>;
