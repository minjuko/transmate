import React from 'react';
import {Text, View, StyleSheet, Image} from 'react-native';

const Empty = () => {
  return (
    <View style={styles.container}>
      <Image
        style={styles.image}
        source={require('../images/linguistic.png')}
        resizeMode="contain"
      />
      <Text style={styles.description}>저장된 문서가 없습니다.</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  image: {
    width: 250,
    height: 150,
    marginBottom: 25,
  },
  description: {
    fontSize: 20,
    color: '#9e9e9e',
  },
});

export default Empty;
