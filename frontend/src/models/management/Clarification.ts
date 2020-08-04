import DiscussionEntry from '@/models/management/DiscussionEntry';

export default class Clarification {
  id: number | null = null;
  questionAnswerId: number | null = null;
  title: string = '';
  question: string = '';
  discussionEntryDtoList: Array<DiscussionEntry> = [];
  username: string | null = null;

  constructor(jsonObj?: Clarification) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.questionAnswerId = jsonObj.questionAnswerId;
      this.title = jsonObj.title;
      this.discussionEntryDtoList = jsonObj.discussionEntryDtoList;
      this.question = jsonObj.question;
      this.username = jsonObj.username;
    }
  }
}
