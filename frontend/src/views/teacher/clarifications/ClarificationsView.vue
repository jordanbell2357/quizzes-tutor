<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="clarifications"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
        </v-card-title>
      </template>

      <template v-slot:item.title="{ item }">
        <div
          @click="showClarificationDialog(item)"
          @contextmenu="editClarificationDialog(item, $event)"
          class="clickableTitle"
        >
          {{ item.title }}
        </div>
      </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon class="mr-2" v-on="on" @click="addDiscussion(item)"
              >add_box</v-icon
            >
          </template>
          <span>Add Answer</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon class="mr-2" v-on="on" @click="viewDiscussion(item)">
              book
            </v-icon>
          </template>
          <span>View Discussion</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <add-discussion-entry-dialog
      v-if="currentAddDiscussionEntry"
      v-model="addDiscussionEntryDialog"
      :clarificationId="currentClarificationId"
      :discussionEntry="currentAddDiscussionEntry"
      v-on:close-add-discussion-dialog="onCloseAddDiscussionEntryDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Vue, Component } from 'vue-property-decorator';
import Clarification from '@/models/management/Clarification';
import RemoteServices from '@/services/RemoteServices';
import EditClarificationDialog from '@/views/student/quiz/EditClarificationDialog.vue';
import DiscussionEntry from '@/models/management/DiscussionEntry';
import StatementClarification from '@/models/statement/StatementClarification';
import Question from '@/models/management/Question';
import AddDiscussionEntryDialog from '@/views/teacher/clarifications/AddDiscussionEntryDialog.vue';

@Component({
  components: {
    'edit-clarification-dialog': EditClarificationDialog,
    'add-discussion-entry-dialog': AddDiscussionEntryDialog
  }
})
export default class ClarificationsView extends Vue {
  clarifications: Clarification[] = [];
  search: string = '';

  headers: object = [
    { text: 'Title', value: 'title', align: 'center', width: '10%' },
    { text: 'Question', value: 'question', align: 'left' },
    { text: 'User', value: 'username', align: 'left' },
    { text: 'Last Entry', value: 'lastDiscussionEntry', align: 'left' },
    { text: 'Time', value: 'timeOfLastEntry', align: 'left' },
    {
      text: 'Action',
      value: 'action',
      align: 'left',
      width: '5px',
      sortable: false
    }
  ];

  currentShowDiscussionEntry: DiscussionEntry | null = null;
  currentAddDiscussionEntry: DiscussionEntry | null = null;
  addDiscussionEntryDialog: boolean = false;
  discussionEntryDialog: boolean = false;
  currentClarificationId: number | null = null;
  clarificationDialog: boolean = false;
  editClarification: boolean = false;

  async created() {
    try {
      this.clarifications = await RemoteServices.getAllClarifications();
    } catch (e) {
      await this.$store.dispatch('error', e);
    }
    await this.$store.dispatch('clearLoading');
  }

  addDiscussion(clarification: Clarification) {
    this.currentAddDiscussionEntry = new DiscussionEntry();
    this.currentClarificationId = clarification.id;
    this.currentAddDiscussionEntry.clarificationId = clarification.id;
    this.addDiscussionEntryDialog = true;
  }

  async viewDiscussion(clarification: Clarification) {
    let statementManager: StatementClarification =
      StatementClarification.getInstance;
    statementManager.clarification = clarification;
    statementManager.discussionEntries = clarification.discussionEntryDtoList;
    await this.$router.push({ name: 'clarification-discussion-view' });
  }

  showClarificationDialog(clarification: Clarification) {
    this.currentClarificationId = clarification.id;
    this.clarificationDialog = true;
  }

  editClarificationDialog(clarification: Clarification, e?: Event) {
    if (e) e.preventDefault();
    this.currentClarificationId = clarification.id;
    this.editClarification = true;
  }

  async onCloseDiscussionEntryDialog() {
    this.currentShowDiscussionEntry = null;
    this.discussionEntryDialog = false;
  }

  async onCloseAddDiscussionEntryDialog(discussionEntry: DiscussionEntry) {
    for (let i = 0; i != this.clarifications.length; i = i + 1) {
      if (this.clarifications[i].id == this.currentClarificationId) {
        let clarification = new Clarification();
        clarification = this.clarifications[i];
        clarification.lastDiscussionEntry = discussionEntry.message;
        clarification.timeOfLastEntry = discussionEntry.dateTime;
        this.clarifications.splice(i, 1, clarification);
        break;
      }
    }
    this.currentAddDiscussionEntry = null;
    this.currentClarificationId = null;
    this.addDiscussionEntryDialog = false;
  }
}
</script>
<style lang="scss" scoped></style>
