import { question } from "../../question/questionModel/question";

export class quiz {
    
  
   idQuiz?: string;
  content?: string;
  //questions: question[] = [];
questions: question[];
  constructor() {
    this.questions = [];
  }
  
  
}