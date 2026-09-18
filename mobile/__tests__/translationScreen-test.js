import React from 'react';
import renderer, {act} from 'react-test-renderer';
import {Alert, PermissionsAndroid} from 'react-native';
import {GiftedChat} from 'react-native-gifted-chat';
import firestore from '@react-native-firebase/firestore';
import ChattingScreen from '../screens/ChattingScreen';
import FileContext from '../contexts/FileContext';
import STTContext from '../contexts/STTContext';
import {requestTranslation} from '../lib/translationApi';

jest.mock('../lib/translationApi', () => ({
  requestTranslation: jest.fn(),
  translationErrorMessage: jest.fn(() => '다시 시도해주세요.'),
}));
jest.mock('../contexts/UserContext', () => ({
  useUserContext: () => ({user: {uid: 'sender'}}),
}));
jest.mock('react-native-gifted-chat', () => ({
  GiftedChat: Object.assign(
    jest.fn(() => null),
    {
      append: jest.fn((previous, message) => [message, ...previous]),
    },
  ),
}));
jest.mock('react-native-element-dropdown', () => ({Dropdown: () => null}));
jest.mock('react-native-dialog', () => ({
  __esModule: true,
  default: {
    Container: () => null,
    Title: () => null,
    Input: () => null,
    Button: () => null,
  },
}));
jest.mock('react-native-vector-icons/MaterialIcons', () => () => null);
jest.mock('@react-native-firebase/firestore', () => {
  const messages = {
    add: jest.fn(),
    orderBy: jest.fn(() => ({onSnapshot: jest.fn(() => jest.fn())})),
  };
  const firestoreMock = () => ({
    collection: () => ({doc: () => ({collection: () => messages})}),
  });
  firestoreMock.FieldValue = {serverTimestamp: jest.fn()};
  return firestoreMock;
});

it('offers a retry and stores a typed message only after translation succeeds', async () => {
  const alert = jest.spyOn(Alert, 'alert').mockImplementation(() => {});
  const requestPermission = jest
    .spyOn(PermissionsAndroid, 'request')
    .mockResolvedValue('granted');
  requestTranslation
    .mockRejectedValueOnce(new Error('network'))
    .mockResolvedValueOnce('항만');
  const navigation = {
    addListener: jest.fn(() => jest.fn()),
    setOptions: jest.fn(),
  };
  const route = {
    params: {
      languageName: 'English',
      languageCode: 'en',
      categoryName: 'Trade',
      categoryCode: 'trade',
      uid: 'recipient',
    },
  };
  const addMessage = jest.fn();
  let screen;

  await act(async () => {
    screen = renderer.create(
      <FileContext.Provider value={{onCreate: jest.fn()}}>
        <STTContext.Provider
          value={{
            setMessage: jest.fn(),
            channer: '',
            AddMessage: addMessage,
            message: [],
          }}>
          <ChattingScreen route={route} navigation={navigation} />
        </STTContext.Provider>
      </FileContext.Provider>,
    );
  });

  const onSend = GiftedChat.mock.calls.at(-1)[0].onSend;
  await act(async () => {
    await onSend([{text: 'port', user: {_id: 'sender'}}]);
  });

  expect(addMessage).not.toHaveBeenCalled();
  expect(
    firestore().collection().doc().collection().add,
  ).not.toHaveBeenCalled();
  expect(alert).toHaveBeenCalledWith(
    '번역 실패',
    '다시 시도해주세요.',
    expect.any(Array),
  );

  await act(async () => {
    await alert.mock.calls[0][2][1].onPress();
  });

  expect(requestTranslation).toHaveBeenCalledTimes(2);
  expect(addMessage).toHaveBeenCalledTimes(1);
  expect(firestore().collection().doc().collection().add).toHaveBeenCalledTimes(
    1,
  );
  act(() => screen.unmount());
  alert.mockRestore();
  requestPermission.mockRestore();
});
