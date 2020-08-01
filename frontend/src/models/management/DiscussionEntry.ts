export default class DiscussionEntry {
  id: number | null = null;
  clarificationId: number | null = null;
  message: string = '';
  userId: number | null = null;
  timestamp: Date = new Date();
  username: string = '';

  constructor(jsonObj?: DiscussionEntry) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.clarificationId = jsonObj.clarificationId;
      this.message = jsonObj.message;
      this.userId = jsonObj.userId;
      this.timestamp = new Date(jsonObj.timestamp);
      this.username = jsonObj.username;
    }
  }
}
