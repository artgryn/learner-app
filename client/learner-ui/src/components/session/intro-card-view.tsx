import { StyleSheet, View } from 'react-native';

import { PromptCard } from '@/components/session/prompt-card';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { ThemedText } from '@/components/ui/themed-text';
import { Fonts, Space } from '@/constants/theme';
import type { IntroduceCard } from '@/api';

type IntroCardViewProps = {
  card: IntroduceCard;
  onNext: () => void;
};

// form_type is open text (inventories differ per language) — label what we recognize,
// fall back to the raw code for anything else rather than failing to render it.
const FORM_LABELS: Record<string, string> = {
  indef_sg: 'indef. sg.',
  def_sg: 'def. sg.',
  indef_pl: 'indef. pl.',
  def_pl: 'def. pl.',
  infinitive: 'infinitive',
  present: 'present',
  preteritum: 'preteritum',
  supine: 'supine',
  imperative: 'imperative',
  present_participle: 'present participle',
  past_participle: 'past participle',
  subject: 'subject',
  object: 'object',
  possessive_c: 'possessive (common)',
  possessive_n: 'possessive (neuter)',
  possessive_pl: 'possessive (plural)',
  sg: 'singular',
  pl: 'plural',
};

function formLabel(formType: string): string {
  return FORM_LABELS[formType] ?? formType;
}

/** itemType: introduce — first exposure to a word, teach before testing. Writes nothing. */
export function IntroCardView({ card, onNext }: IntroCardViewProps) {
  const eyebrow = card.gender ? `${card.pos} · ${card.gender} — new word` : `${card.pos} — new word`;

  return (
    <View style={styles.container}>
      <PromptCard eyebrow={eyebrow}>
        <ThemedText type="word" style={styles.word}>
          {card.word}
        </ThemedText>
        <ThemedText type="subhead" color="textSecondary">
          {card.translation}
        </ThemedText>
      </PromptCard>

      {card.forms.length > 0 && (
        <Card>
          <View style={styles.formsGrid}>
            {card.forms.map((form) => (
              <View key={form.formType} style={styles.formCell}>
                <ThemedText type="headline" style={styles.formValue}>
                  {form.form}
                </ThemedText>
                <ThemedText type="caption1" color="textSecondary">
                  {formLabel(form.formType)}
                </ThemedText>
              </View>
            ))}
          </View>
        </Card>
      )}

      <Button variant="primary" style={styles.nextButton} onPress={onNext}>
        Next
      </Button>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    gap: Space[5],
  },
  word: {
    marginTop: Space[1],
    textAlign: 'center',
  },
  formsGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: Space[4],
  },
  formCell: {
    flexBasis: '46%',
    flexGrow: 1,
    alignItems: 'center',
    gap: 2,
  },
  formValue: {
    fontFamily: Fonts.word,
  },
  nextButton: {
    marginTop: 'auto',
  },
});
