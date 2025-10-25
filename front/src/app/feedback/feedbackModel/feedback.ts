export class feedback {
    
  
   idFeedback?: string;
  comment?: string | null;
  rating?: number | null; // 1..5
  createdAt?: string

  // selon ce que renvoie ton backend, tu peux typer plus finement
  announcement?: any | null;
  //panelist?: any | null;
}

