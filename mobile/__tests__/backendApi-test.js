import auth from '@react-native-firebase/auth';
import backendApi, {
  attachFirebaseToken,
  handleBackendError,
} from '../lib/backendApi';

describe('backendApi authentication', () => {
  afterEach(() => {
    auth().currentUser = null;
    jest.clearAllMocks();
  });

  it('uses the configured backend API URL', () => {
    expect(backendApi.defaults.baseURL).toBe('http://localhost:8080');
  });

  it('adds the current Firebase ID token to backend requests', async () => {
    auth().currentUser = {
      getIdToken: jest.fn(async () => 'firebase-id-token'),
    };
    const config = {headers: {}};

    const result = await attachFirebaseToken(config);

    expect(auth().currentUser.getIdToken).toHaveBeenCalledTimes(1);
    expect(result.headers.Authorization).toBe('Bearer firebase-id-token');
  });

  it('leaves requests unauthenticated when no user is signed in', async () => {
    const config = {headers: {}};

    const result = await attachFirebaseToken(config);

    expect(result.headers.Authorization).toBeUndefined();
  });

  it('signs out when the backend rejects the Firebase session', async () => {
    const error = {response: {status: 401}};

    await expect(handleBackendError(error)).rejects.toBe(error);

    expect(auth().signOut).toHaveBeenCalledTimes(1);
  });

  it('does not sign out for non-authentication errors', async () => {
    const error = {response: {status: 500}};

    await expect(handleBackendError(error)).rejects.toBe(error);

    expect(auth().signOut).not.toHaveBeenCalled();
  });
});
