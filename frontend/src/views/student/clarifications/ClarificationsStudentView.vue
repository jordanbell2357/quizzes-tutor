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
            <v-icon
              class="mr-2"
              v-on="on"
              @click="showClarificationDialog(item)"
            >
              book
            </v-icon>
          </template>
          <span>View Discussion</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <add-discussion-entry-dialog
      v-if="currentDiscussionEntry"
      v-model="addDiscussionEntryDialog"
      :clarificationId="currentClarificationId"
      :discussionEntry="currentDiscussionEntry"
      v-on:close-add-discussion-dialog="onCloseAddDiscussionEntryDialog"
    />
    <show-clarification-dialog
      v-if="currentClarification"
      v-model="clarificationDialog"
      :clarification="currentClarification"
      v-on:close-clarification-dialog="onCloseClarificationDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Vue, Component } from 'vue-property-decorator';
import Clarification from '@/models/management/Clarification';
import RemoteServices from '@/services/RemoteServices';
import EditClarificationDialog from '@/views/student/clarifications/EditClarificationDialog.vue';
import DiscussionEntry from '@/models/management/DiscussionEntry';
import StatementClarification from '@/models/statement/StatementClarification';
import Question from '@/models/management/Question';
import AddDiscussionEntryDialog from '@/views/teacher/clarifications/AddDiscussionEntryDialog.vue';
import ShowClarificationDialog from '@/views/teacher/clarifications/ShowClarificationDialog.vue';

@Component({
  components: {
    'edit-clarification-dialog': EditClarificationDialog,
    'add-discussion-entry-dialog': AddDiscussionEntryDialog,
    'show-clarification-dialog': ShowClarificationDialog
  }
})
export default class ClarificationsStudentView extends Vue {
  clarifications: Clarification[] = [];
  search: string = '';

  headers: object = [
    {
      text: 'Action',
      value: 'action',
      align: 'left',
      width: '5px',
      sortable: false
    },
    { text: 'Title', value: 'title', align: 'center', width: '10%' },
    { text: 'Question', value: 'question', align: 'left' },
    { text: 'User', value: 'username', align: 'left' },
    { text: 'Last Entry', value: 'lastDiscussionEntry', align: 'left' },
    { text: 'Time', value: 'timeOfLastEntry', align: 'left' }
  ];

  currentDiscussionEntry: DiscussionEntry | null = null;
  currentClarification: Clarification | null = null;
  addDiscussionEntryDialog: boolean = false;
  discussionEntryDialog: boolean = false;
  currentClarificationId: number | null = null;
  clarificationDialog: boolean = false;
  editClarification: boolean = false;

  async created() {
    try {
      this.clarifications = await RemoteServices.getPersonalClarifications();
    } catch (e) {
      await this.$store.dispatch('error', e);
    }
    await this.$store.dispatch('clearLoading');
  }

  addDiscussion(clarification: Clarification) {
    this.currentDiscussionEntry = new DiscussionEntry();
    this.currentClarificationId = clarification.id;
    this.currentDiscussionEntry.clarificationId = clarification.id;
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
    this.currentClarification = clarification;
    this.clarificationDialog = true;
  }

  editClarificationDialog(clarification: Clarification, e?: Event) {
    if (e) e.preventDefault();
    this.currentClarificationId = clarification.id;
    this.editClarification = true;
  }

  async onCloseClarificationDialog() {
    this.currentClarification = null;
    this.clarificationDialog = false;
  }

  async onCloseAddDiscussionEntryDialog(discussionEntry: DiscussionEntry) {
    for (let i = 0; i != this.clarifications.length; i = i + 1) {
      if (this.clarifications[i].id == this.currentClarificationId) {
        let clarification = new Clarification();
        clarification = this.clarifications[i];
        clarification.discussionEntryDtoList.push(discussionEntry);
        clarification.lastDiscussionEntry = discussionEntry.message;
        clarification.timeOfLastEntry = discussionEntry.dateTime;
        this.clarifications.splice(i, 1, clarification);
        break;
      }
    }
    this.currentDiscussionEntry = null;
    this.currentClarificationId = null;
    this.addDiscussionEntryDialog = false;
  }
}
</script>
<style lang="scss" scoped></style>
