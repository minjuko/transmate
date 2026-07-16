import React, {useState, useContext} from 'react';
import {
  Alert,
  View,
  StyleSheet,
  Text,
  StatusBar,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import {SafeAreaView} from 'react-native-safe-area-context';
import SignButtons from '../components/SignButtons';
import SignInForm from '../components/SignForm';
import {signIn, signUp} from '../lib/auth';
import {useUserContext} from '../contexts/UserContext';
import backendApi from '../lib/backendApi';
import FileContext from '../contexts/FileContext';
import ScheduleContext from '../contexts/ScheduleContext';
import {mapMeetings, mapSchedules} from '../lib/backendMappers';

const SignInScreen = ({navigation, route}) => {
  const {isSignUp} = route.params ?? {};
  const [form, setForm] = useState({
    email: '',
    password: '',
    confirmPassword: '',
  });

  const [loading, setLoading] = useState();
  const {setUser} = useUserContext();
  const {setFiles} = useContext(FileContext);
  const {setSchedules} = useContext(ScheduleContext);

  const createChangeTextHandler = name => value => {
    setForm({...form, [name]: value});
  };

  const onSubmit = async () => {
    //Keyboard.dismiss();
    const {email, password, confirmPassword} = form;

    if (isSignUp && password !== confirmPassword) {
      Alert.alert('실패', '비밀번호가 일치하지 않습니다.');
      return;
    }

    if (password.length === 0) {
      Alert.alert('실패', '비밀번호를 입력해주세요.');
      return;
    }

    const info = {email, password};
    setLoading(true);

    try {
      const {user} = isSignUp ? await signUp(info) : await signIn(info);
      if (isSignUp) {
        try {
          await backendApi.post('/account/create', {
            accountid: user.uid,
          });
        } catch (error) {
          console.error('Error SignUp:', error);
        }
      } else {
        try {
          const response = await backendApi.get(`/meetings/${user.uid}`);
          setFiles(mapMeetings(response.data));
        } catch (error) {
          console.error('Error get File:', error);
        }

        try {
          const response = await backendApi.get(`/schedules/${user.uid}`);
          console.log('schedule data : ', response.data);
          setSchedules(mapSchedules(response.data));
        } catch (error) {
          console.error('Error get Schedule:', error);
        }
      }

      setUser(user);
      console.log(user);

      navigation.navigate('MainTabScreen');
      //navigation.navigate('messageScreen');
    } catch (e) {
      const message = {
        'auth/email-already-in-use': '이미 가입된 이메일입니다.',
        'auth/wrong-password': '잘못된 비밀번호입니다.',
        'auth/user-not-found': '존재하지 않는 계정입니다.',
        'auth/invalid-email': '유효하지 않은 이메일 주소입니다.',
      };
      const msg = message[e.code] || `${isSignUp ? '가입' : '로그인'} 실패`;
      Alert.alert('실패', msg);
      console.log(e);
    } finally {
      setLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView
      style={styles.keyboardAvoidingView}
      behavior={Platform.select({ios: 'padding'})}>
      <SafeAreaView style={styles.fullscreen}>
        <StatusBar backgroundColor="white" barStyle="dark-content" />
        <Text style={styles.text}>L o g i n</Text>
        <View style={styles.form}>
          <SignInForm
            isSignUp={isSignUp}
            onSubmit={onSubmit}
            form={form}
            createChangeTextHandler={createChangeTextHandler}
          />
          <SignButtons
            isSignUp={isSignUp}
            onSubmit={onSubmit}
            loading={loading}
          />
        </View>
      </SafeAreaView>
    </KeyboardAvoidingView>
  );
};

const styles = StyleSheet.create({
  fullscreen: {
    backgroundColor: 'white',
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  text: {
    fontSize: 25,
    fontWeight: 'bold',
  },
  form: {
    marginTop: 50,
    width: '100%',
    paddingHorizontal: 16,
  },
  keyboardAvoidingView: {
    flex: 1,
  },
});

export default SignInScreen;
