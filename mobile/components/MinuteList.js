import React, {useContext} from 'react';
import {View, StatusBar, StyleSheet, FlatList} from 'react-native';
import MinuteItem from './MinuteItem';
import SearchContext from '../contexts/SearchContext';

const MinuteList = ({files, onRemove, getDate, today, onToggle}) => {
  const {keyword} = useContext(SearchContext);

  const filtered =
    keyword === ''
      ? files
      : files.filter(file =>
          [file.title, file.department].some(text => text.includes(keyword)),
        );

  return (
    <View style={styles.container}>
      <FlatList
        ItemSeparatorComponent={() => <View style={styles.separator} />}
        style={styles.minuteList}
        data={filtered}
        renderItem={({item}) => (
          <View>
            <MinuteItem
              title={item.title}
              department={item.department}
              date={item.date}
              onRemove={onRemove}
              content={item.content}
              getDate={getDate}
              today={today}
              onToggle={onToggle}
              file={item}
            />
          </View>
        )}
        keyExtractor={file => file.id}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  separator: {
    backgroundColor: '#e0e0e0',
    height: 1,
  },
});

export default MinuteList;
