import { ISOtoString } from '@/services/ConvertDateService';

export default class DiscussionEntry {
  id: number | null = null;
  clarificationId: number | null = null;
  message: string = '';
  userId: number | null = null;
  dateTime: string = '';
  username: string = '';

  constructor(jsonObj?: DiscussionEntry) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.clarificationId = jsonObj.clarificationId;
      this.message = jsonObj.message;
      this.userId = jsonObj.userId;
      this.dateTime = ISOtoString(jsonObj.dateTime);
      this.username = jsonObj.username;
    }
  }
}
