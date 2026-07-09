import React from 'react';
import {useUserContext} from '../contexts/UserContext';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
//import MainTab from './MainTab';
import SignInScreen from './SignInScreen';
import TranslationSettingsScreen from './TranslationSettingsScreen';
import ChattingScreen from './ChattingScreen';
import MainTabScreen from './MainTabScreen';
import OpenMinute from './OpenMinute';
import SummaryScreen from './SummaryScreen';
import MessageScreen from './MessageScreen';
import ChatScreen from './ChatScreen';

const Stack = createNativeStackNavigator();

const RootStack = () => {
  const {user} = useUserContext();
  return (
    // initialRouteName="Voice"
    <Stack.Navigator
      //initialRouteName="MainTabScreen"
      screenOptions={{
        headerStyle: {
          backgroundColor: '#1976D2',
        },
        headerTintColor: '#fff',
      }}>
      {user ? (
        <>
          <Stack.Screen
            name="MainTab"
            component={MainTabScreen}
            options={{
              headerShown: false,
              headerStyle: {
                backgroundColor: '#1976D2',
              },
              headerTintColor: '#ffff',
            }}
          />
        </>
      ) : (
        <>
          <Stack.Screen
            name="SignIn"
            component={SignInScreen}
            options={{headerShown: false}}
          />
        </>
      )}
      <Stack.Screen
        name="messageScreen"
        component={MessageScreen}
        options={{
          headerShown: false,
          headerStyle: {
            backgroundColor: '#1976D2',
          },
          headerTintColor: '#ffff',
        }}
      />
      <Stack.Screen
        name="Chats"
        component={ChatScreen}
        options={{
          headerShown: false,
          headerStyle: {
            backgroundColor: '#1976D2',
          },
          headerTintColor: '#ffff',
        }}
      />
      <Stack.Screen
        name="MainTabScreen"
        component={MainTabScreen}
        options={{
          headerShown: false,
          headerStyle: {
            backgroundColor: '#1976D2',
          },
          headerTintColor: '#ffff',
        }}
      />
      <Stack.Screen
        name="Summary"
        component={SummaryScreen}
        options={{
          title: '문서',
          headerStyle: {
            backgroundColor: '#1976D2',
          },
          headerTintColor: '#ffff',
        }}
      />
      <Stack.Screen
        name="SettingOption"
        component={TranslationSettingsScreen}
        options={{
          title: '옵션',
          headerTitleAlign: 'center',
          headerStyle: {
            backgroundColor: '#1976D2',
          },
          headerTintColor: '#ffff',
        }}
        style={{flex: 1}}
      />
      <Stack.Screen
        name="Chatting"
        component={ChattingScreen}
        options={{
          title: '대화 번역',
          headerTitleAlign: 'center',
          headerStyle: {
            backgroundColor: '#1976D2',
          },
          headerTintColor: '#ffff',
        }}
      />
      <Stack.Screen
        name="openFile"
        component={OpenMinute}
        options={{
          title: '문서',
          headerStyle: {
            backgroundColor: '#1976D2',
          },
          headerTintColor: '#ffff',
        }}
      />
    </Stack.Navigator>
  );
};

export default RootStack;
