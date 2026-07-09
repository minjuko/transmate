import React from 'react';
import {FlatList, StyleSheet, View} from 'react-native';
import CalenderItem from './CalendarItem';

const CalendarList = ({schedules}) => {
  return (
    <FlatList
      data={schedules}
      style={styles.block}
      renderItem={({item}) => <CalenderItem schedule={item} />}
      keyExtractor={schedule => schedule.id}
      ItemSeparatorComponent={() => <View style={styles.separator} />}
    />
  );
};

const styles = StyleSheet.create({
  block: {
    flex: 1,
  },
  separator: {},
});

export default CalendarList;
