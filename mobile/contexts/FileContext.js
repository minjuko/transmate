import React from 'react';
import {createContext, useState} from 'react';
import {v4 as uuidv4} from 'uuid';
import backendApi from '../lib/backendApi';
import {useUserContext} from '../contexts/UserContext';
import {createLocalMeeting, createMeeting} from '../lib/meetingsApi';

const FileContext = createContext();

export const FileContextProvider = ({children}) => {
  const {user} = useUserContext();

  const [files, setFiles] = useState([]);

  const onCreate = async ({title, department, content, date}) => {
    const meeting = {title, department, content, date};
    const localId = uuidv4();
    let file = createLocalMeeting(meeting, localId);

    try {
      file = await createMeeting(user.uid, meeting, localId);
    } catch {
      console.error('Error createFile');
      return;
    }

    setFiles(currentFiles => [file, ...currentFiles]);
  };

  const onModify = async modified => {
    //id가 일치하면 교체, 그렇지 않으면 유지
    const nextFiles = files.map(file =>
      file.id === modified.id ? modified : file,
    );

    try {
      await backendApi.patch(`/meeting/patch/${modified.id}`, {
        data: modified.content,
        title: modified.title,
        category: modified.department,
        date: modified.date,
      });
      setFiles(nextFiles);
    } catch {
      console.error('Error modifyFile');
    }
  };

  const onRemove = async id => {
    const nextFiles = files.filter(file => file.id !== id);

    try {
      await backendApi.delete(`/meeting/delete/${id}`);
      setFiles(nextFiles);
    } catch {
      console.error('Error removeFile');
    }
  };

  return (
    <FileContext.Provider
      value={{files, setFiles, onCreate, onModify, onRemove}}>
      {children}
    </FileContext.Provider>
  );
};

export default FileContext;
