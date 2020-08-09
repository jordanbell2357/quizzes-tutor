<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="clarifications"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      multi-sort
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
          <template v-slot:item.action="{ item }">
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-icon
                  small
                  class="mr-2"
                  v-on="on"
                  @click="addDiscussion(item)"
                  >add_box</v-icon
                >
              </template>
              <span>Add Answer</span>
            </v-tooltip>
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-icon
                  small
                  class="mr-2"
                  v-on="on"
                  @click="viewDiscussion(item)"
                  color="white"
                  >book
                </v-icon>
              </template>
              <span>Thread</span>
            </v-tooltip>
          </template>
        </v-card-title>
      </template>
    </v-data-table>
  </v-card>
</template>
<script lang="ts">
import { Vue, Component } from 'vue-property-decorator';
import Clarification from '@/models/management/Clarification';
import RemoteServices from '@/services/RemoteServices';
import editClarificationDialog from '@/views/student/quiz/editClarificationDialog.vue';
import DiscussionEntry from '@/models/management/DiscussionEntry';

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
    { text: 'Last Entry', value: 'lastDiscussionEntry', align: 'left' }
  ];
  currentDiscussionEntry: DiscussionEntry | undefined;
  editDiscussionEntryDialog: boolean = false;

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
}
</script>
<style lang="scss" scoped></style>
