import { createNativeStackNavigator } from '@react-navigation/native-stack';
import type { RootStackParamList } from './types';
import Bottom from './Bottom';
import ProfileDetail from '../Screens/Profile/ProfileDetail';
import Settings from '../Screens/Profile/Settings';

const Stack = createNativeStackNavigator<RootStackParamList>();

export default function RootStack() {
  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      <Stack.Screen name="BottomTabs" component={Bottom} />
      <Stack.Screen name="ProfileDetail" component={ProfileDetail} />
      <Stack.Screen name="Settings" component={Settings} />
    </Stack.Navigator>
  );
}
