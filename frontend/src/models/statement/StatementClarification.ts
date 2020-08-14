import DiscussionEntry from '@/models/management/DiscussionEntry';
import Clarification from '@/models/management/Clarification';
import RemoteServices from '@/services/RemoteServices';

export default class StatementClarification {
  discussionEntries: DiscussionEntry[] = [];
  clarification: Clarification | undefined;

  private static _clarification: StatementClarification = new StatementClarification();

  static get getInstance(): StatementClarification {
    return this._clarification;
  }

  async getDiscussionEntry() {
    if (this.clarification) {
      let a = await RemoteServices.getClarificationById(this.clarification.id);
      this.discussionEntries = a.discussionEntryDtoList;
    }
  }

  isEmpty(): boolean {
    return this.discussionEntries == [];
  }
}
