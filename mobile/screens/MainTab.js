import React from 'react';
import {useUserContext} from '../contexts/UserContext';
import {StyleSheet, Text, View, Button} from 'react-native';

const MainTab = ({navigation}) => {
  const {user} = useUserContext();
  return (
    <View style={styles.block}>
      <Text style={styles.text}>Hello, {user.displayName}</Text>
      <Button title="회의록" onPress={navigation.navigate('Minutes')} />
    </View>
  );
};

const styles = StyleSheet.create({
  block: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  text: {
    fontSize: 24,
  },
});

export default MainTab;
