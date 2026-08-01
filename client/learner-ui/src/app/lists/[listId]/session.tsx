import { useLocalSearchParams } from 'expo-router';

import { SessionScreen } from '@/screens/session-screen';

export default function SessionRoute() {
  const { listId } = useLocalSearchParams<{ listId: string }>();
  return <SessionScreen listId={Number(listId)} />;
}
