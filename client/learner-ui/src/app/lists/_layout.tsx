import { Stack } from 'expo-router';

// Own stack so the catalog can push into list details/session while staying inside the Lists
// tab. Both are presented as page sheets rather than plain pushes: NativeTabs renders an actual
// native tab bar controller (not a JS overlay), which has no "hide on push" option in this SDK —
// a modal presentation is what actually covers it. `fullScreenModal` would too, but it has no
// interactive dismiss gesture and starts flush at y=0 (colliding with the Dynamic Island on
// notched iPhones); `pageSheet` gets native swipe-to-dismiss and its own top inset for free.
export default function ListsLayout() {
  return (
    <Stack screenOptions={{ headerShown: false }}>
      <Stack.Screen name="index" />
      <Stack.Screen name="[listId]/index" options={{ presentation: 'pageSheet' }} />
      <Stack.Screen name="[listId]/session" options={{ presentation: 'pageSheet' }} />
    </Stack>
  );
}
