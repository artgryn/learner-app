import { Feather } from '@expo/vector-icons';
import { usePathname } from 'expo-router';
import { Tabs, TabList, TabTrigger, TabSlot, TabTriggerSlotProps, TabListProps } from 'expo-router/ui';
import { StyleSheet, View } from 'react-native';

import { ThemedText } from '@/components/ui/themed-text';
import { ThemedView } from '@/components/ui/themed-view';

import { MaxContentWidth, Space } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

// expo-symbols (SF Symbols) is iOS-only, so the web tab bar uses Feather icons instead.
export default function AppTabs() {
  // On iOS, list details/session are presented as native page sheets (see app/lists/_layout.tsx)
  // that cover NativeTabs' real tab bar controller. expo-router/ui's web Tabs has no equivalent
  // modal concept — TabList is always an absolutely-positioned overlay on top of TabSlot,
  // regardless of what route is showing — so without this it sits on top of those screens' own
  // Check/Next/Enroll buttons. Hide it for the same routes that are pageSheet-presented natively.
  //
  // The TabTrigger elements must stay mounted even while hidden — Tabs' navigator builder reads
  // them to know what screens exist at all, and errors ("Couldn't find any screens for the
  // navigator") if TabList/TabTrigger aren't rendered. So CustomTabList hides its own chrome via
  // `hidden`, it isn't conditionally unmounted from here.
  const pathname = usePathname();
  const hideTabBar = pathname.startsWith('/lists/');

  return (
    <Tabs>
      <TabSlot style={{ height: '100%' }} />
      <TabList asChild>
        <CustomTabList hidden={hideTabBar}>
          <TabTrigger name="index" href="/" asChild>
            <TabButton icon="home">Home</TabButton>
          </TabTrigger>
          <TabTrigger name="lists" href="/lists" asChild>
            <TabButton icon="list">Lists</TabButton>
          </TabTrigger>
          <TabTrigger name="settings" href="/settings" asChild>
            <TabButton icon="user">Account</TabButton>
          </TabTrigger>
        </CustomTabList>
      </TabList>
    </Tabs>
  );
}

type TabButtonProps = TabTriggerSlotProps & { icon: keyof typeof Feather.glyphMap };

export function TabButton({ children, isFocused, icon, ...props }: TabButtonProps) {
  const theme = useTheme();
  const color = isFocused ? theme.accentPrimary : theme.textSecondary;

  return (
    <View {...props} style={styles.tabButton}>
      <Feather name={icon} size={20} color={color} />
      <ThemedText type="caption1" color={isFocused ? 'accentPrimary' : 'textSecondary'}>
        {children}
      </ThemedText>
    </View>
  );
}

type CustomTabListProps = TabListProps & { hidden?: boolean };

export function CustomTabList({ hidden, ...props }: CustomTabListProps) {
  const theme = useTheme();

  if (hidden) {
    // display: 'none' keeps the TabTriggers mounted (so Tabs still sees its screens) while
    // removing the bar from layout/painting/hit-testing entirely.
    return <View style={styles.hidden}>{props.children}</View>;
  }

  return (
    <View {...props} style={styles.tabListContainer}>
      <ThemedView
        type="surface"
        style={[styles.innerContainer, { borderColor: theme.borderHairline }]}>
        {props.children}
      </ThemedView>
    </View>
  );
}

const styles = StyleSheet.create({
  hidden: {
    display: 'none',
  },
  tabListContainer: {
    position: 'absolute',
    bottom: 0,
    width: '100%',
    padding: Space[3],
    alignItems: 'center',
  },
  innerContainer: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    width: '100%',
    maxWidth: MaxContentWidth,
    paddingVertical: Space[2],
    borderTopWidth: StyleSheet.hairlineWidth,
  },
  tabButton: {
    alignItems: 'center',
    gap: 2,
    paddingHorizontal: Space[4],
    paddingVertical: Space[1],
  },
});
