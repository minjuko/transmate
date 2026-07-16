import axios from 'axios';
import auth from '@react-native-firebase/auth';
import Config from 'react-native-config';

if (!Config.BACKEND_API_URL) {
  throw new Error('BACKEND_API_URL is not configured');
}

const backendApi = axios.create({baseURL: Config.BACKEND_API_URL});

export async function attachFirebaseToken(config) {
  const user = auth().currentUser;
  if (user) {
    const idToken = await user.getIdToken();
    config.headers.Authorization = `Bearer ${idToken}`;
  }
  return config;
}

backendApi.interceptors.request.use(attachFirebaseToken);

export async function handleBackendError(error) {
  if (error.response?.status === 401) {
    await auth().signOut();
  }
  return Promise.reject(error);
}

backendApi.interceptors.response.use(response => response, handleBackendError);

export default backendApi;
