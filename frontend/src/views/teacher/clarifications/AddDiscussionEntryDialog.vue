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
          New entry to the discussion:
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="discussionEntry">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field v-model="discussionEntry.message" label="Message" />
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" @click="$emit('dialog', false)"
          >Cancel
        </v-btn>
        <v-btn
          color="blue darken-1"
          @click="saveClarification"
          data-cy="send"
          >Send
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import DiscussionEntry from '@/models/management/DiscussionEntry';

@Component
export default class EditClarificationRequestDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: DiscussionEntry, required: true })
  readonly discussionEntry!: DiscussionEntry;
  @Prop({ type: Number, required: true })
  readonly clarificationId!: number;

  async saveClarification() {
    if (this.discussionEntry && !this.discussionEntry.message) {
      await this.$store.dispatch('error', 'Message must not be empty');
      return;
    }
    if (this.discussionEntry) {
      try {
        const result = await RemoteServices.addDiscussionEntryToClarification(
          this.clarificationId,
          this.discussionEntry
        );
        this.$emit('close-add-discussion-dialog', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style scoped></style>
