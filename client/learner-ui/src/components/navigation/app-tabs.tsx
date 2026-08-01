import { Icon, Label, NativeTabs } from 'expo-router/unstable-native-tabs';
import { useColorScheme } from 'react-native';

import { Colors } from '@/constants/theme';

// SDK 54 (Expo Go compatible): Icon/Label are separate exports, not NativeTabs.Trigger.* statics.
// Icon Android support needs a native `drawable` resource, which Expo Go can't provide — icons are
// iOS-only for now (labels still show on Android); iOS is the primary target per the product brief.
export default function AppTabs() {
  const scheme = useColorScheme();
  const colors = Colors[scheme ?? 'light'];

  return (
    <NativeTabs backgroundColor={colors.surface} tintColor={colors.accentPrimary}>
      <NativeTabs.Trigger name="index">
        <Label>Home</Label>
        <Icon sf={{ default: 'house', selected: 'house.fill' }} />
      </NativeTabs.Trigger>

      <NativeTabs.Trigger name="lists">
        <Label>Lists</Label>
        <Icon sf="list.bullet" />
      </NativeTabs.Trigger>

      <NativeTabs.Trigger name="settings">
        <Label>Account</Label>
        <Icon sf={{ default: 'person.crop.circle', selected: 'person.crop.circle.fill' }} />
      </NativeTabs.Trigger>
    </NativeTabs>
  );
}
