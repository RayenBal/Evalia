// src/app/recompenses/recompensesModel/recompenses.ts

export enum TypeRecompenses {
  BonsDachats = 'BonsDachats',
  Points = 'Points',
  Argent = 'Argent',
}


export class RecompenseNew {

  typeRecompenses!: TypeRecompenses;
  amount!: number;
  label?: string;

  constructor(init?: Partial<RecompenseNew>) {
    Object.assign(this, init);
  }
}




export type RecompensePayload = RecompenseNew ;


export class recompenses extends RecompenseNew{


  constructor(init?: Partial<recompenses>) {
    super(init)
  }


  
}
