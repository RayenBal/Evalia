
export enum TypeReclamation {
  ReclamatioinAnnonceur = 'ReclamatioinAnnonceur',
  ReclamationTargetPanel = 'ReclamationTargetPanel'
}
export type TypeUser = 'Paneliste' | 'Announceur';
export enum Motif {
  Absence = 'Absence',
  Reportation = 'Reportation',
  Annulation = 'Annulation',
  MauvaisePriseEnCharge = 'MauvaisePriseEnCharge',
  ProblemesTechniques = 'ProblemesTechniques',
  SatisfactionGlobale = 'SatisfactionGlobale'
}

export class reclamation {
    
  idreclamation?: string;
  content?: string;
  //typeReclamation?: TypeReclamation;
  motif?: Motif;
 userType?: 'Paneliste' | 'Announceur';
  
}