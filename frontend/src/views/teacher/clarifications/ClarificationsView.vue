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
            <v-icon class="mr-2" v-on="on" @click="viewDiscussion(item)">
              book
            </v-icon>
          </template>
          <span>Add Entry</span>
        </v-tooltip>
      </template>
    </v-data-table>

    <show-discussion-dialog
      v-if="currentDiscussionEntry"
      v-model="discussionEntryDialog"
      :discussionEntry="currentDiscussionEntry"
      v-on:close-show-question-dialog="onCloseDiscussionEntryDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Vue, Component } from 'vue-property-decorator';
import Clarification from '@/models/management/Clarification';
import RemoteServices from '@/services/RemoteServices';
import editClarificationDialog from '@/views/student/quiz/editClarificationDialog.vue';
import DiscussionEntry from '@/models/management/DiscussionEntry';
import StatementClarification from '@/models/statement/StatementClarification';

@Component({
  components: {
    'edit-clarification-dialog': editClarificationDialog
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
    { text: 'Time', value: 'timestamp', align: 'left' },
    {
      text: 'Action',
      value: 'action',
      align: 'left',
      width: '5px',
      sortable: false
    }
  ];

  currentDiscussionEntry: DiscussionEntry | undefined;
  editDiscussionEntryDialog: boolean = false;
  discussionEntryDialog: boolean = false;

  async created() {
    try {
      this.clarifications = await RemoteServices.getAllClarifications();
    } catch (e) {
      await this.$store.dispatch('error', e);
    }
    await this.$store.dispatch('clearLoading');
  }

  addDiscussion(clarification: Clarification) {
    this.currentDiscussionEntry = new DiscussionEntry();
    this.currentDiscussionEntry.clarificationId = clarification.id;
    this.editDiscussionEntryDialog = true;
  }

  async viewDiscussion(clarification: Clarification) {
    let statementManager: StatementClarification =
      StatementClarification.getInstance;
    statementManager.clarification = clarification;
    statementManager.discussionEntries = clarification.discussionEntryDtoList;
    await this.$router.push({ name: 'clarification-discussion-view' });
  }

  async onCloseDiscussionEntryDialog() {
    this.currentDiscussionEntry = undefined;
    this.discussionEntryDialog = false;
  }
}
</script>
<style lang="scss" scoped></style>
