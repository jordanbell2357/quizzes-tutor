<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-list-item
        v-for="item in clarification.discussionEntryDtoList"
        :key="item.id"
      >
        <v-list-item-content class="text-left">
          <v-card>
            <v-list-item-title class="mx-5"
              >{{ item.message }}
            </v-list-item-title>

            <v-list-item-subtitle class="mx-5"
              >{{ item.dateTime }}
            </v-list-item-subtitle>

            <v-list-item-action class="mx-5">
              <v-list-item-action-text>
                {{ item.userName }}</v-list-item-action-text
              >
            </v-list-item-action>
          </v-card>
        </v-list-item-content>
      </v-list-item>

      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" @click="$emit('dialog', false)"
          >Cancel
        </v-btn>
        <v-btn color="blue darken-1" @click="saveClarification" data-cy="send"
          >Send
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Clarification from '@/models/management/Clarification';

@Component
export default class ShowClarificationDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Clarification, required: true })
  readonly clarification!: Clarification;
  headers: object = [
    { text: 'Time', value: 'dateTime', align: 'left' },
    { text: 'User', value: 'username', align: 'left' },
    { text: 'Message', value: 'message', align: 'left' }
  ];

  async saveClarification() {
    if (false) {
      await this.$store.dispatch('error', 'Message must not be empty');
      return;
    }
    if (this.clarification) {
      this.$emit('close-add-discussion-dialog', Clarification);
    }
  }
}
</script>

<style scoped></style>
