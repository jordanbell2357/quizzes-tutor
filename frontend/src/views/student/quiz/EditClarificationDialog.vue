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
            clarification && clarification.id === null
              ? 'New Clarification'
              : 'Edit Clarification'
          }}
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="clarification">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field v-model="clarification.title" label="Message" />
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
  discussionEntry!: DiscussionEntry;

  async saveClarification() {
    if (this.clarification && !this.clarification.title) {
      await this.$store.dispatch('error', 'Clarification must have a message');
      return;
    }
    if (this.clarification) {
      try {
        this.clarification.questionAnswerId = this.answer.questionAnswerId;
        this.clarification.question = this.question.content;
        this.discussionEntry = new DiscussionEntry();
        this.discussionEntry.message = this.clarification.title;
        this.clarification.discussionEntryDtoList.push(this.discussionEntry);

        const result = await RemoteServices.createClarification(
          this.clarification
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
