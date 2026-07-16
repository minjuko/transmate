import axios from 'axios';
import auth from '@react-native-firebase/auth';

const backendApi = axios.create({baseURL: 'http://3.39.132.36:8080'});

export async function attachFirebaseToken(config) {
  const user = auth().currentUser;
  if (user) {
    const idToken = await user.getIdToken();
    config.headers.Authorization = `Bearer ${idToken}`;
  }
  return config;
}

backendApi.interceptors.request.use(attachFirebaseToken);

export default backendApi;
