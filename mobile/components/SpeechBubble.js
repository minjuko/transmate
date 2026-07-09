import React, {useEffect} from 'react';
import {View, Text, StyleSheet, TouchableOpacity} from 'react-native';
import Tts from 'react-native-tts';
import Icon from 'react-native-vector-icons/MaterialIcons';

const SpeechBubble = ({
  text,
  direction,
  languageCode,
  translateText,
  stopRecognizing,
  startRecognizing,
  isRecording,
}) => {
  useEffect(() => {
    // 컴포넌트가 언마운트될 때 음성 출력을 중지합니다.
    return () => {
      Tts.stop();
    };
  }, []);

  const onTTS = async () => {
    try {
      Tts.setDefaultVoice(languageCode + '-default');
      Tts.setDefaultLanguage(languageCode);
    } catch (error) {
      console.error('Error occurred during TTS: ', error);
    }

    if (isRecording) {
      await stopRecognizing();

      if (translateText) {
        Tts.speak(translateText);
      }

      startRecognizing();
    } else {
      if (translateText) {
        Tts.speak(translateText);
      }
    }
  };

  return (
    <View
      style={
        direction === 'left' ? styles.containerLeft : styles.containerRight
      }>
      <Text
        style={[
          styles.speechText,
          direction === 'left' ? styles.textLeft : styles.textRight,
        ]}>
        {text}
        {'\n\n'}
        <Text
          style={[
            direction === 'right'
              ? styles.bottomTextRight
              : styles.bottomTextLeft,
          ]}>
          {translateText}
        </Text>
        <TouchableOpacity style={styles.volume} onPress={onTTS}>
          <View>
            <Icon name="volume-up" size={17} color="#1976D2" />
          </View>
        </TouchableOpacity>
      </Text>
    </View>
  );
};

const styles = StyleSheet.create({
  containerLeft: {
    alignItems: 'flex-start',
    boxShadow: '2px 2px 3px #d1d1d1',
    marginBottom: 10,
    marginRight: 100,
  },
  containerRight: {
    alignItems: 'flex-end',
    boxShadow: '2px 2px 3px #d1d1d1',
    marginBottom: 10,
    marginLeft: 100,
  },
  volume: {
    paddingLeft: 10,
    alignSelf: 'flex-end',
  },
  textRight: {
    fontSize: 18,
    color: '#1976D2',
    backgroundColor: 'white',
    borderColor: '#CCE1FF',
    fontWeight: 600,
    borderWidth: 1,
    borderRadius: 17,
    paddingTop: 5,
    paddingBottom: 5,
    paddingLeft: 12,
    paddingRight: 12,
    marginRight: 10,
  },
  textLeft: {
    fontSize: 18,
    color: '#323232',
    backgroundColor: '#CCE1FF',
    borderRadius: 17,
    paddingTop: 5,
    paddingBottom: 5,
    paddingLeft: 12,
    paddingRight: 12,
    marginLeft: 10,
  },
  bottomTextRight: {
    color: '#5a5a5a',
    fontWeight: 100,
    fontSize: 18,
    flexWrap: 'wrap',
  },
  bottomTextLeft: {
    color: '#1976D2',
    fontSize: 18,
    fontWeight: 600,
  },
});

export default SpeechBubble;
