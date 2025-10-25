import { announce } from "../../Announce/AnnounceModel/announce";

export class Category {
  idcategory?: string;
  nameCategory?: string;
  announces?: announce[];
    constructor() {
      this.announces = [];
    }
}
