import React, {useState, useEffect, useContext} from 'react';
import {
  View,
  StatusBar,
  StyleSheet,
  Alert,
  Keyboard,
  TouchableOpacity,
} from 'react-native';
import Empty from '../components/Empty';
import MinuteList from '../components/MinuteList';
import Icon from 'react-native-vector-icons/MaterialIcons';
import Dialog from 'react-native-dialog';
import FileContext from '../contexts/FileContext';
import SearchBar from '../components/SearchBar';

const getDate = today => {
  const year = today.getFullYear();
  const month = today.getMonth() + 1;
  const day = today.getDate();

  const date = `${year}.${month}.${day}`;

  return date;
};

const MinutesScreen = ({navigation}) => {
  const [visible, setVisible] = useState(false);
  const [title, setTitle] = useState('');
  const [department, setDepartment] = useState('');

  const {onCreate} = useContext(FileContext);
  const {files} = useContext(FileContext);

  const onSave = () => {
    onCreate({
      title: title,
      department: department,
      content: '',
      date: getDate(new Date()),
    });
  };

  const showDialog = () => {
    setVisible(true);
  };

  const handleCancel = () => {
    setVisible(false);
  };

  const handleOk = () => {
    if (title === '') {
      Alert.alert('문서 추가', '제목은 공백으로 둘 수 없습니다.');

      setVisible(false);
    } else {
      //onInsert(title, department);
      onSave();
      // 서버에 새로운 문서 추가 코드 추가
      setVisible(false);
    }
    setTitle('');
    setDepartment('');
    Keyboard.dismiss();
  };

  useEffect(() => {
    navigation.setOptions({
      headerStyle: {
        backgroundColor: '#1976D2',
      },
      headerTintColor: '#ffff',
      headerRight: () => (
        <View>
          <TouchableOpacity onPress={showDialog} style={styles.addButton}>
            <Icon name="add" size={28} color="white" />
          </TouchableOpacity>
          <View>
            <Dialog.Container visible={visible}>
              <Dialog.Title>문서 추가</Dialog.Title>
              <Dialog.Description>
                문서 제목과 소속을 입력해주세요.
              </Dialog.Description>
              <Dialog.Input placeholder="문서 제목" onChangeText={setTitle} />
              <Dialog.Input placeholder="소속" onChangeText={setDepartment} />
              <Dialog.Button label="취소" onPress={handleCancel} />
              <Dialog.Button label="확인" onPress={handleOk} />
            </Dialog.Container>
          </View>
        </View>
      ),
    });
  });

  const today = new Date();

  return (
    <View style={styles.container}>
      {/**backgroundColor="#FFFFFF" barStyle="dark-content"  */}
      <StatusBar backgroundColor="#1976D2" barStyle="light-content" />
      <SearchBar />
      {files.length === 0 ? (
        <Empty />
      ) : (
        <MinuteList files={files} getDate={getDate} today={today} />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
  },
  item: {
    flex: 1,
    justifyContent: 'center',
    margin: 5,
  },
  box: {
    alignItems: 'center',
  },
  itemText: {
    flex: 1,
  },
  itemButton: {
    flex: 1,
  },
  addButton: {
    marginRight: 16,
  },
});

export default MinutesScreen;
