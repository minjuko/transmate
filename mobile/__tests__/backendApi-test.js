import auth from '@react-native-firebase/auth';
import {attachFirebaseToken} from '../lib/backendApi';

describe('backendApi authentication', () => {
  afterEach(() => {
    auth().currentUser = null;
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
});
