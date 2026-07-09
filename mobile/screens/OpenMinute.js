import React, {useEffect, useState, useContext} from 'react';
import {View, StyleSheet, TouchableOpacity, TextInput} from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';
import FileContext from '../contexts/FileContext';

const OpenMinute = ({navigation, route}) => {
  const {id, title, department, content, date} = route.params.file;

  const [filecontent, setFileContent] = useState(content);
  const {onModify} = useContext(FileContext);

  // 서버와 연결해서 내용 수정, 저장하는 기능 추가(아래 코드 수정)

  const getDate = today => {
    const year = today.getFullYear();
    const month = today.getMonth() + 1;
    const day = today.getDate();

    const date = `${year}.${month}.${day}`;

    return date;
  };

  const onContentSave = () => {
    onModify({
      id: id,
      title: title,
      department: department,
      content: filecontent,
      date: getDate(new Date()),
    });
  };

  useEffect(() => {
    navigation.setOptions({title: title});
    navigation.setOptions({
      headerRight: () => (
        <TouchableOpacity onPress={onContentSave}>
          <Icon name="check" size={20} color="white" />
        </TouchableOpacity>
      ),
    });
  });

  return (
    <View style={styles.container}>
      <TextInput
        style={styles.content}
        multiline={true}
        onChangeText={setFileContent}
        value={filecontent}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
  },
  content: {
    margin: 16,
    fontSize: 16,
    flexShrink: 1,
    lineHeight: 30,
  },
});

export default OpenMinute;
