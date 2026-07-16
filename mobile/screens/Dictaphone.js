import React, {useState} from 'react';
import {View, Text, TouchableOpacity, StyleSheet} from 'react-native';
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
    } catch (stopError) {
      setError(stopError);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Voice input</Text>
      <Text style={styles.message}>{message}</Text>
      <Text>{error.message}</Text>
      <TouchableOpacity
        style={styles.recordButton}
        onPress={isRecording ? stopRecording : startRecording}>
        <Text style={styles.recordButtonText}>
          {isRecording ? 'Stop Recording' : 'Start Recording'}
        </Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    margin: 20,
  },
  title: {
    fontSize: 20,
    color: 'green',
    fontWeight: '500',
  },
  message: {
    fontSize: 30,
  },
  recordButton: {
    marginTop: 30,
  },
  recordButtonText: {
    color: 'red',
  },
});

export default Dictaphone;
