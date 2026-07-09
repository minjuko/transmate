import React, {useEffect, useState} from 'react';
import {View, StyleSheet, TouchableOpacity, Alert, Text} from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';
import RNHTMLtoPDF from 'react-native-html-to-pdf';

const SummaryScreen = ({navigation, route}) => {
  const [filePath, setFilePath] = useState('');
  const summary = route.params.summary;
  let title = route.params.title;
  const department = route.params.department;

  useEffect(() => {
    navigation.setOptions({title: title + '_요약'});
    navigation.setOptions({
      headerRight: () => (
        <TouchableOpacity onPress={createPDF}>
          <Icon name="arrow-downward" size={20} color="white" />
        </TouchableOpacity>
      ),
    });
  });

  const createPDF = async () => {
    const fileTitle = department !== '' ? `${title}_${department}` : `${title}`;
    //if (await isPermitted()) {
    let options = {
      //Content to print
      html: `<h1 style="text-align: center;"><strong>${title}</strong></h1><p style="text-align: center;"><strong>${department}</strong></p><p style="margin: 16;">${summary}</p>`,
      //File Name
      fileName: `${fileTitle}`,
      //File directory
      directory: 'docs',
    };
    let file = await RNHTMLtoPDF.convert(options);

    setFilePath(file.filePath);
    const path = JSON.stringify(file.filePath);
    Alert.alert('다운 받은 파일 경로', `${path}`);
  };

  return (
    <View style={styles.container}>
      <Text style={styles.summary}>{summary}</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
  },
  summary: {
    margin: 15,
    marginTop: 20,
    fontSize: 15,
    color: 'black',
    lineHeight: 30,
  },
});

export default SummaryScreen;
