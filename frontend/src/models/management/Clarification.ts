import DiscussionEntry from '@/models/management/DiscussionEntry';
import { ISOtoString } from '@/services/ConvertDateService';

export default class Clarification {
  id: number | null = null;
  questionAnswerId: number | null = null;
  title: string = '';
  question: string = '';
  discussionEntryDtoList: Array<DiscussionEntry> = [];
  username: string | null = null;
  lastDiscussionEntry: string | undefined = '';
  timeOfLastEntry: string | undefined = '';
  timeOfCreation: string | undefined = '';

  constructor(jsonObj?: Clarification) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.questionAnswerId = jsonObj.questionAnswerId;
      this.title = jsonObj.title;
      this.discussionEntryDtoList = this.discussionLoader(
        jsonObj.discussionEntryDtoList
      );
      this.question = jsonObj.question;
      this.username = jsonObj.username;
      this.lastDiscussionEntry =
        jsonObj.discussionEntryDtoList[
          jsonObj.discussionEntryDtoList.length - 1
        ].message;
      this.timeOfLastEntry = ISOtoString(
        jsonObj.discussionEntryDtoList[
          jsonObj.discussionEntryDtoList.length - 1
        ].dateTime
      );
      this.timeOfCreation = ISOtoString(
        jsonObj.discussionEntryDtoList[0].dateTime
      );
    }
  }

  discussionLoader(discussionEntryDtoList?: Array<DiscussionEntry>) {
    let discussionEntry: DiscussionEntry[] = [];
    if (discussionEntryDtoList) {
      for (let i = 0; i < discussionEntryDtoList.length; i++) {
        discussionEntry.push(new DiscussionEntry(discussionEntryDtoList[i]));
      }
    }
    return discussionEntry;
  }
}
