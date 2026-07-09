import React, {useState, useContext, useEffect} from 'react';
import {
  StatusBar,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
  Alert,
  TextInput,
} from 'react-native';
import {Dropdown} from 'react-native-element-dropdown';
import SettingContext from '../contexts/SettingContext';
import STTContext from '../contexts/STTContext';
import firestore from '@react-native-firebase/firestore';
import {useUserContext} from '../contexts/UserContext';

const TranslationSettingsScreen = ({navigation}) => {
  const [languageCode, setLanguageCode] = useState(null);
  const [languageName, setLanguageName] = useState('');
  const [categoryCode, setCategoryCode] = useState(null);
  const [categoryName, setCategoryName] = useState('');
  const [isFocus, setIsFocus] = useState(false);
  const [channerText, setChannerText] = useState('');

  const {languages} = useContext(SettingContext);
  const {categorys} = useContext(SettingContext);

  let uid;
  let email;

  const {setChanner, RemoveMessages} = useContext(STTContext);
  const {user} = useUserContext();

  useEffect(() => {
    navigation.setOptions({
      headerStyle: {
        backgroundColor: '#1976D2',
      },
      headerTintColor: '#ffff',
    });
  });

  const handleButtonPress = () => {
    if (channerText.length > 0) {
      firestore().collection(channerText).add({
        name: channerText,
      });
    }
  };

  const getUsers = async () => {
    const querySanp = await firestore()
      .collection('users')
      .where('email', '==', channerText)
      .get();
    // .where('email', '==', channerText)
    // .get();
    const allUsers = querySanp.docs.map(docSnap => docSnap.data());
    uid = allUsers.map(item => item.uid)[0];
    email = allUsers.map(item => item.email)[0];
  };

  return (
    <View style={styles.container}>
      <StatusBar backgroundColor="#1976D2" barStyle="light-content" />
      <View style={styles.logo}>
        <Text style={styles.logoText}>옵션을 선택해주세요.</Text>
      </View>
      <View style={styles.chattingCode}>
        <TextInput
          placeholder="상대방 메일을 입력해주세요."
          value={channerText}
          onChangeText={setChannerText}
        />
      </View>
      <View style={{backgroundColor: '#fff', padding: 20, borderRadius: 15}}>
        <Dropdown
          style={styles.dropdown}
          placeholderStyle={styles.placeholderStyle}
          selectedTextStyle={styles.selectedTextStyle}
          inputSearchStyle={styles.inputSearchStyle}
          iconStyle={styles.iconStyle}
          data={languages}
          search
          maxHeight={300}
          labelField="label"
          valueField="value"
          placeholder={!isFocus ? '언어 선택' : '...'}
          searchPlaceholder="검색"
          value={languageCode}
          onFocus={() => setIsFocus(true)}
          onBlur={() => setIsFocus(false)}
          onChange={item => {
            setLanguageCode(item.value);
            setLanguageName(item.label);
            setIsFocus(false);
          }}
        />
        <Dropdown
          style={styles.dropdown}
          placeholderStyle={styles.placeholderStyle}
          selectedTextStyle={styles.selectedTextStyle}
          inputSearchStyle={styles.inputSearchStyle}
          iconStyle={styles.iconStyle}
          data={categorys}
          search
          maxHeight={300}
          labelField="label"
          valueField="value"
          placeholder={!isFocus ? '분야 선택' : '...'}
          searchPlaceholder="검색"
          value={categoryCode}
          onFocus={() => setIsFocus(true)}
          onBlur={() => setIsFocus(false)}
          onChange={item => {
            setCategoryCode(item.value);
            setCategoryName(item.label);
            setIsFocus(false);
          }}
        />
        <TouchableOpacity
          style={{
            backgroundColor: '#0F3460',
            padding: 20,
            borderRadius: 15,
            alignItems: 'center',
          }}
          onPress={() =>
            languageCode && categoryCode
              ? Alert.alert(
                  '선택이 맞는지 확인해주세요.',
                  `언어: ${languageName}\n분야: ${categoryName}`,
                  [
                    {
                      text: '확인',
                      onPress: async () => {
                        await getUsers();
                        handleButtonPress();
                        console.log('uid: ', uid);
                        console.log('email: ', email);
                        navigation.navigate('Chatting', {
                          languageName: languageName,
                          categoryName: categoryName,
                          languageCode: languageCode,
                          categoryCode:
                            categoryCode === 'default' ? '' : categoryCode,
                          Language: languages,
                          Category: categorys,
                          uid: uid,
                          name: email,
                        });

                        RemoveMessages();

                        setLanguageCode(null);
                        setLanguageName('');
                        setCategoryCode(null);
                        setCategoryName('');
                        setChanner(channerText);
                        setChannerText('');
                      },
                      style: 'default',
                    },
                    {text: '취소', onPress: () => {}, stule: 'cancel'},
                  ],
                  {cancelable: true, onDismiss: () => {}},
                )
              : Alert.alert('입력', '언어와 분야를 모두 선택해주세요!', [
                  {text: '확인', onPress: () => {}, style: 'default'},
                ])
          }>
          <Text
            style={{
              color: '#fff',
              textTransform: 'uppercase',
              fontWeight: '600',
            }}>
            Submit
          </Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

export default TranslationSettingsScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white', //#1976D2
    padding: 16,
    justifyContent: 'center',
    alignContent: 'center',
  },
  logo: {
    alignItems: 'center',
    marginBottom: 20,
  },
  logoText: {
    fontSize: 20,
    color: '#1976D2', //white
  },
  chattingCode: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  dropdown: {
    height: 50,
    borderColor: 'gray',
    borderWidth: 0.7, //0.5
    borderRadius: 8,
    paddingHorizontal: 8,
    marginBottom: 10,
  },
  placeholderStyle: {
    fontSize: 16,
    fontWeight: '500',
  },
  selectedTextStyle: {
    fontSize: 16,
    fontWeight: '500',
  },
  iconStyle: {
    width: 20,
    height: 20,
  },
  inputSearchStyle: {
    height: 40,
    fontSize: 16,
    fontWeight: '500',
  },
});
