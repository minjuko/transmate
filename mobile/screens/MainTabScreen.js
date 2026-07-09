import React from 'react';
import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import MinutesScreen from './MinutesScreen';
import Icon from 'react-native-vector-icons/MaterialIcons';
import TranslationSettingsScreen from './TranslationSettingsScreen';
import CalendarScreen from './CalendarScreen';

const Tab = createBottomTabNavigator();

const MainTabScreen = () => {
  return (
    <Tab.Navigator>
      <Tab.Screen
        name="문서"
        component={MinutesScreen}
        tabBarOptions={{
          showLabel: false,
          activeTintColor: '#009688',
        }}
        options={{
          tabBarIcon: ({color, size}) => (
            <Icon name="folder-open" size={size} color={color} />
          ),
          headerTitleAlign: 'center',
        }}
      />
      <Tab.Screen
        name="캘린더"
        component={CalendarScreen}
        tabBarOptions={{
          showLabel: false,
          activeTintColor: '#009688',
        }}
        options={{
          tabBarIcon: ({color, size}) => (
            <Icon name="event" size={size} color={color} />
          ),
          headerTitleAlign: 'center',
        }}
      />
      <Tab.Screen
        name="대화 번역"
        component={TranslationSettingsScreen}
        tabBarOptions={{
          showLabel: false,
          activeTintColor: '#009688',
        }}
        options={{
          tabBarIcon: ({color, size}) => (
            <Icon name="forum" size={size} color={color} />
          ),
          headerTitleAlign: 'center',
        }}
      />
    </Tab.Navigator>
  );
};

export default MainTabScreen;
