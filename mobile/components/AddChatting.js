import React, {useContext, useRef, useEffect} from 'react';
import {FlatList, StyleSheet} from 'react-native';
import SpeechBubble from './SpeechBubble';
import STTContext from '../contexts/STTContext';

const AddChatting = ({stopRecognizing, startRecognizing, isRecording}) => {
  const {messages} = useContext(STTContext);
  const flatListRef = useRef(null); // FlatList에 대한 ref 생성

  useEffect(() => {
    scrollToBottom(); // 컴포넌트가 마운트될 때마다 스크롤을 맨 아래로 이동
  }, [messages]); // messages 배열이 변경될 때마다 useEffect 실행

  const scrollToBottom = () => {
    flatListRef.current?.scrollToEnd(); // 스크롤을 맨 아래로 이동
  };

  return (
    <FlatList
      ref={flatListRef} // ref를 FlatList에 설정
      style={styles.list}
      data={messages}
      keyExtractor={item => item.id.toString()}
      renderItem={({item}) => (
        <SpeechBubble
          text={item.text}
          direction={item.direction}
          languageCode={item.languageCode}
          translateText={item.translateText}
          stopRecognizing={stopRecognizing}
          startRecognizing={startRecognizing}
          isRecording={isRecording}
        />
      )}
    />
  );
};

const styles = StyleSheet.create({
  list: {
    paddingBottom: 30,
  },
});

export default AddChatting;
