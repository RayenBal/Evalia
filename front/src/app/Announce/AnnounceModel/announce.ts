import { Category } from "../../category/categoryModel/category";
import { quiz } from "../../quiz/quizModel/quiz";
import { RecompenseNew } from "../../recompenses/recompensesModel/recompenses"; 
export class ResponsePaneliste {
  content!: string;
 // isCorrect!: boolean;
}

export class Question {
  content!: string;
  responses!: ResponsePaneliste[];
}

export class Quiz {
  content!: string;
  questions!: Question[];
}
export class announce {
    
  idAnnouncement?: string;
  announceName?:string;
  content?: string;
  image?: string;
 testModes: ('HOME_DELIVERY' | 'OFFICE_TESTING')[] = [];
productImages?: string;
  deliveryAddress?: string | null;
 // estimatedDeliveryDate?: string | null;
  officeAddress?: string | null;
  //timeSlots?: string | null;
  category?: Category;
quizList?: Quiz[];
  /*verifiedById?: number;
  verifiedAt?: string;*/ // ou Date, selon ton usage
 recompensesList?: RecompenseNew[];
  constructor(init?: Partial<announce>) {
    Object.assign(this, init);
  }
 /* quizList?: {
  content: string;
  questions: {
    content: string;
    responses: {
      content: string;
      isCorrect: boolean;
    }[];
  }[];
}[];*/
}