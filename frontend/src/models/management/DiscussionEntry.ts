import { ISOtoString } from '@/services/ConvertDateService';

export default class DiscussionEntry {
  id: number | null = null;
  clarificationId: number | null = null;
  message: string = '';
  userId: number | null = null;
<<<<<<< HEAD
  dateTime: string = '';
=======
  dateTime: number = Date.now();
>>>>>>> clarification
  username: string = '';

  constructor(jsonObj?: DiscussionEntry) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.clarificationId = jsonObj.clarificationId;
      this.message = jsonObj.message;
      this.userId = jsonObj.userId;
<<<<<<< HEAD
      this.dateTime = ISOtoString(jsonObj.dateTime);
=======
      this.dateTime = new Date(jsonObj.dateTime);
>>>>>>> clarification
      this.username = jsonObj.username;
    }
  }
}
