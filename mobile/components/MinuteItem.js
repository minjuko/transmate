import React, {useContext, useState} from 'react';
import {View, Text, StyleSheet, TouchableOpacity, Alert} from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';
import {useNavigation} from '@react-navigation/native';
import Dialog from 'react-native-dialog';
import RNHTMLtoPDF from 'react-native-html-to-pdf';
import FileContext from '../contexts/FileContext';
import KakaoContext from '../contexts/KakaoContext';

const MinuteItem = ({getDate, file}) => {
  const navigation = useNavigation();

  const [visible, setVisible] = useState(false);
  const [filePath, setFilePath] = useState('');
  const {id, title, department, content, date} = file;

  const [itemTitle, setitemTitle] = useState(title);
  const [itemDepartment, setitemDepartment] = useState(department);

  const {onModify, onRemove} = useContext(FileContext);
  const {onCreate} = useContext(KakaoContext);

  const handleSummarize = async () => {
    try {
      const createdSummary = await onCreate({
        prompt: content + '\n한 줄 요약:',
      });

      console.log(createdSummary);

      navigation.navigate('Summary', {
        title: title,
        department: department,
        summary: createdSummary,
      });
    } catch (error) {
      console.error('Error: ', error);
    }
  };

  const showDialog = () => {
    setVisible(true);
  };

  const handleCancel = () => {
    setVisible(false);
  };

  const handleOk = () => {
    const today = new Date();

    if (itemTitle === '') {
      Alert.alert('제목은 공백으로 둘 수 없습니다.');

      setitemTitle(title);
      setitemDepartment(department);
    } else {
      onModify({
        id: id,
        title: itemTitle,
        department: itemDepartment,
        date: getDate(today),
      });
    }
    setVisible(false);
  };

  const onAskRemove = () => {
    Alert.alert('삭제', '정말로 삭제하시겠어요?', [
      {text: '취소', style: 'cancel'},
      {
        text: '삭제',
        onPress: () => {
          onRemove(file?.id);
        },
      },
    ]);
  };

  // const isPermitted = async () => {
  //   if (Platform.OS === 'android') {
  //     console.log('android');
  //     try {
  //       const granted = await PermissionsAndroid.request(
  //         PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE,
  //         {
  //           title: 'External Storage Write Permission',
  //           message: 'App needs access to Storage data',
  //         },
  //       );
  //       console.log(PermissionsAndroid.RESULTS.GRANTED);
  //       return granted === PermissionsAndroid.RESULTS.GRANTED;
  //     } catch (err) {
  //       alert('Write permission err', err);
  //       return false;
  //     }
  //   } else {
  //     return true;
  //   }
  // };

  const createPDF = async () => {
    const fileTitle = department !== '' ? `${title}_${department}` : `${title}`;
    //if (await isPermitted()) {
    let options = {
      //Content to print
      html: `<h1 style="text-align: center;"><strong>${title}</strong></h1><p style="text-align: center;"><strong>${department}</strong></p><p style="margin: 16;">${content}</p>`,
      //File Name
      fileName: `${fileTitle}`,
      //File directory
      directory: 'docs',
    };
    let file = await RNHTMLtoPDF.convert(options);

    setFilePath(file.filePath);
    const path = JSON.stringify(file.filePath);
    Alert.alert('다운 받은 파일 경로', `${path}`);
  };

  return (
    <View style={styles.item}>
      <TouchableOpacity style={styles.Icon} onPress={createPDF}>
        <Icon name="arrow-circle-down" size={30} color="#1976D2" />
      </TouchableOpacity>
      <View style={styles.textContainer}>
        <TouchableOpacity
          onPress={() =>
            navigation.navigate('openFile', {
              file: file,
            })
          }>
          <Text style={styles.title}>{title}</Text>
          <View style={styles.bottomData}>
            <Text style={styles.department}>{department}</Text>
            <Text style={styles.date}>{date}</Text>
          </View>
        </TouchableOpacity>
      </View>
      <View style={styles.rightIcon}>
        <TouchableOpacity
          style={[styles.summary, {marginRight: 5}]}
          onPress={showDialog}>
          <Icon name="edit" size={30} style={styles.modifyIcon} />
          <Text style={styles.summaryText}>수정</Text>
        </TouchableOpacity>
        <View>
          <Dialog.Container visible={visible}>
            <Dialog.Title>정보 수정</Dialog.Title>
            <Dialog.Description>변경 사항을 입력해주세요.</Dialog.Description>
            <Dialog.Input value={itemTitle} onChangeText={setitemTitle} />
            <Dialog.Input
              value={itemDepartment}
              onChangeText={setitemDepartment}
            />
            <Dialog.Button label="취소" onPress={handleCancel} />
            <Dialog.Button label="확인" onPress={handleOk} />
          </Dialog.Container>
        </View>
        <TouchableOpacity
          style={[styles.summary, {marginRight: 5}]}
          onPress={handleSummarize}>
          <Icon name="wysiwyg" size={30} color="#C0C0C0" />
          <Text style={styles.summaryText}>요약</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.summary} onPress={onAskRemove}>
          <Icon name="delete" size={30} color="#cd5c5c" />
          <Text style={styles.summaryText}>삭제</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  item: {
    flexDirection: 'row',
    padding: 13,
  },
  Icon: {
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 16,
    borderRadius: 50,
  },
  rightIcon: {
    flexDirection: 'row',
    alignItems: 'flex-end',
    justifyContent: 'center',
    marginLeft: 10,
  },
  textContainer: {
    flex: 1,
  },
  title: {
    fontSize: 18,
    color: '#212121',
  },
  bottomData: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-end',
  },
  department: {
    marginRight: 3,
    flexShrink: 1,
  },
  date: {
    marginLeft: 3,
  },
  summary: {
    alignItems: 'center',
  },
  summaryText: {
    fontSize: 12,
  },
});

export default MinuteItem;
