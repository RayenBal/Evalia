export interface ResponsePaneliste {
 IdResponsePaneliste?: string;
 
  content: string;
  //isCorrect: boolean;
  question?: {
    idQuestion: string; // ou juste 'id' si tu l'as renommé
  };
}
