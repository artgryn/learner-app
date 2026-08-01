import { Feather } from '@expo/vector-icons';
import { SymbolView } from 'expo-symbols';
import { Modal, StyleSheet, View } from 'react-native';

import { Button } from '@/components/ui/button';
import { ThemedText } from '@/components/ui/themed-text';
import { ThemedView } from '@/components/ui/themed-view';
import { Radius, Shadow, Space } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type ConfirmDialogProps = {
  visible: boolean;
  title: string;
  message: string;
  confirmLabel: string;
  cancelLabel?: string;
  onConfirm: () => void;
  onCancel: () => void;
};

/** Destructive-confirmation overlay — shared by the switch-list warning and account deletion. */
export function ConfirmDialog({
  visible,
  title,
  message,
  confirmLabel,
  cancelLabel = 'Cancel',
  onConfirm,
  onCancel,
}: ConfirmDialogProps) {
  const theme = useTheme();

  return (
    <Modal visible={visible} transparent animationType="fade" onRequestClose={onCancel}>
      <View style={styles.backdrop}>
        <ThemedView type="surface" style={[styles.sheet, Shadow.sheet]}>
          <View style={[styles.iconCircle, { backgroundColor: theme.errorTint }]}>
            <SymbolView
              name="exclamationmark.triangle"
              size={22}
              tintColor={theme.error}
              fallback={<Feather name="alert-triangle" size={22} color={theme.error} />}
            />
          </View>
          <ThemedText type="headline" style={styles.title}>
            {title}
          </ThemedText>
          <ThemedText type="footnote" color="textSecondary" style={styles.message}>
            {message}
          </ThemedText>
          <View style={styles.actions}>
            <Button variant="destructive" style={styles.fullWidth} onPress={onConfirm}>
              {confirmLabel}
            </Button>
            <Button variant="ghost" style={styles.fullWidth} onPress={onCancel}>
              {cancelLabel}
            </Button>
          </View>
        </ThemedView>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  backdrop: {
    flex: 1,
    backgroundColor: 'rgba(20,16,10,0.55)',
    alignItems: 'center',
    justifyContent: 'center',
    padding: Space[6],
  },
  sheet: {
    width: '100%',
    borderRadius: Radius.xl,
    padding: Space[6],
    alignItems: 'center',
  },
  iconCircle: {
    width: 48,
    height: 48,
    borderRadius: Radius.pill,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: Space[3],
  },
  title: {
    textAlign: 'center',
  },
  message: {
    textAlign: 'center',
    marginTop: Space[2],
    lineHeight: 18,
  },
  actions: {
    width: '100%',
    gap: Space[2],
    marginTop: Space[5],
  },
  fullWidth: {
    width: '100%',
  },
});
