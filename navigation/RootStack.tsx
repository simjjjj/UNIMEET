import { createNativeStackNavigator } from '@react-navigation/native-stack';
import Bottom from './Bottom';
import ProfileDetail from '../Screens/Profile/ProfileDetail';
import Settings from '../Screens/Profile/Settings';
import RoomDetail from '../Screens/Home/RoomDetail';
import Lounge from '../Screens/Lounge/Lounge';
import PostDetail from '../Screens/Lounge/PostDetail';
import type { RootStackParamList } from './types';

const Stack = createNativeStackNavigator<RootStackParamList>();

export default function RootStack() {
  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      <Stack.Screen name="BottomTabs" component={Bottom} />
      <Stack.Screen name="RoomDetail" component={RoomDetail} />
      <Stack.Screen name="ProfileDetail" component={ProfileDetail} />
      <Stack.Screen name="Settings" component={Settings} />
      <Stack.Screen name="Lounge" component={Lounge} />
      <Stack.Screen name="PostDetail" component={PostDetail} />
    </Stack.Navigator>
  );
}
