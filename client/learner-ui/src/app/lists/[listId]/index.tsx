import { useLocalSearchParams } from 'expo-router';

import { ListDetailsScreen } from '@/screens/list-details-screen';

export default function ListDetailsRoute() {
  const { listId } = useLocalSearchParams<{ listId: string }>();
  return <ListDetailsScreen listId={Number(listId)} />;
}
