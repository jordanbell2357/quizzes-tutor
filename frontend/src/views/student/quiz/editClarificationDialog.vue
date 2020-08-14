<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          {{
            editClarification && editClarification.id === null
              ? 'New ClarificationRequest'
              : 'Edit ClarificationRequest'
          }}
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editClarification">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field v-model="editClarification.title" label="Message" />
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" @click="$emit('dialog', false)"
          >Cancel
        </v-btn>
        <v-btn color="blue darken-1" @click="saveClarification">Send </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Clarification from '@/models/management/Clarification';
import StatementAnswer from '@/models/statement/StatementAnswer';
import DiscussionEntry from '@/models/management/DiscussionEntry';
import StatementQuestion from '@/models/statement/StatementQuestion';

@Component
export default class EditClarificationDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Clarification, required: true })
  readonly clarification!: Clarification;
  @Prop(StatementAnswer) readonly answer!: StatementAnswer;
  @Prop(StatementQuestion) readonly question!: StatementQuestion;
  editClarification!: Clarification;
  discussionEntry!: DiscussionEntry;

  created() {
    this.editClarification = new Clarification(this.clarification);
    this.editClarification.discussionEntryDtoList = new Array<
      DiscussionEntry
    >();
  }

  async saveClarification() {
    if (this.editClarification && !this.editClarification.title) {
      await this.$store.dispatch('error', 'Clarification must have a message');
      return;
    }
    if (this.editClarification) {
      try {
        this.editClarification.questionAnswerId = this.answer.questionAnswerId;
        this.editClarification.question = this.question.content;
        this.discussionEntry = new DiscussionEntry();
        this.discussionEntry.message = this.editClarification.title;
        this.editClarification.discussionEntryDtoList.push(
          this.discussionEntry
        );

        const result = await RemoteServices.createClarification(
          this.editClarification
        );

        this.$emit('save-clarification', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style scoped></style>
