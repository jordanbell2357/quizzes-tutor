export default class DiscussionEntry {
  id: number | null = null;
  clarificationId: number | null = null;
  message: string = '';
  userId: number | null = null;
  dateTime: number = Date.now();
  username: string = '';

  constructor(jsonObj?: DiscussionEntry) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.clarificationId = jsonObj.clarificationId;
      this.message = jsonObj.message;
      this.userId = jsonObj.userId;
      this.dateTime = new Date(jsonObj.dateTime);
      this.username = jsonObj.username;
    }
  }
}
