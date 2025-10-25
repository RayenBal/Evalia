import { ResponsePaneliste } from "../../responsePaneliste/ResponsePanelisteModel/responsepaneliste";

export class question {
    

    idQuestion?: string;
  content?: string;
//responses: ResponsePaneliste[] = [];
   responses: ResponsePaneliste[];
   quiz?: {
    idQuiz: string;
  };

  constructor() {
    this.responses = [];
  }
}