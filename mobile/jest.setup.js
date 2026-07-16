/* eslint-env jest */

const mockDocumentReference = {
  get: jest.fn(async () => ({data: () => undefined})),
  set: jest.fn(async () => undefined),
};

const mockCollectionReference = {
  doc: jest.fn(() => mockDocumentReference),
};

const mockAuthInstance = {
  currentUser: null,
  createUserWithEmailAndPassword: jest.fn(),
  onAuthStateChanged: jest.fn(() => jest.fn()),
  signInWithEmailAndPassword: jest.fn(),
  signOut: jest.fn(),
};

jest.mock('@react-native-firebase/auth', () => () => mockAuthInstance);

jest.mock('@react-native-firebase/firestore', () => () => ({
  collection: jest.fn(() => mockCollectionReference),
}));

jest.mock('uuid', () => ({
  v4: jest.fn(() => 'test-uuid'),
}));

jest.mock('react-native-google-cloud-speech-to-text', () => ({
  __esModule: true,
  default: {
    onSpeechError: jest.fn(),
    onSpeechRecognized: jest.fn(),
    onSpeechRecognizing: jest.fn(),
    onVoice: jest.fn(),
    onVoiceEnd: jest.fn(),
    onVoiceStart: jest.fn(),
    removeListeners: jest.fn(),
    setApiKey: jest.fn(),
    start: jest.fn(),
    stop: jest.fn(),
  },
}));

jest.mock('react-native/Libraries/Utilities/BackHandler', () => ({
  addEventListener: jest.fn(() => ({remove: jest.fn()})),
  exitApp: jest.fn(),
  removeEventListener: jest.fn(),
}));
