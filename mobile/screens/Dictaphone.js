import React, {useState} from 'react';
import {View, Text, TouchableOpacity} from 'react-native';
import Voice from '@react-native-voice/voice';

const Dictaphone = () => {
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [isRecording, setIsRecording] = useState(false);

  Voice.onSpeechStart = () => setIsRecording(true);
  Voice.onSpeechEnd = () => setIsRecording(false);
  Voice.onSpeechError = err => setError(err.error);
  Voice.onSpeechResults = result => {
    setMessage(result.value[0]);
  };

  const startRecording = async () => {
    try {
      await Voice.start('en-US');
    } catch (err) {
      setError(err);
    }
  };

  const stopRecording = async () => {
    try {
      await Voice.stop();
    } catch (error) {
      setError(error);
    }
  };

  return (
    <View style={{alignItems: 'center', margin: 20}}>
      <Text style={{fontSize: 20, color: 'green', fontWeight: '500'}}>
        Voice input
      </Text>
      <Text style={{fontSize: 30}}>{message}</Text>
      <Text>{error.message}</Text>
      <TouchableOpacity
        style={{marginTop: 30}}
        onPress={isRecording ? stopRecording : startRecording}>
        <Text style={{color: 'red'}}>
          {isRecording ? 'Stop Recording' : 'Start Recording'}
        </Text>
      </TouchableOpacity>
    </View>
  );
};

export default Dictaphone;
