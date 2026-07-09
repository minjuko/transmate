import React from 'react';
import {createContext, useState} from 'react';

const STTContext = createContext();

export const STTContextProvider = ({children}) => {
  const [channer, setChanner] = useState('');
  const [message, setMessage] = useState([]);

  const AddMessage = text => {
    //console.log('AddMessage 추가한 text: ', text);
    text === undefined
      ? console.log('입력된 text 없음')
      : setMessage(prevMessages => [...prevMessages, text]);
    console.log('message: ', message);
  };

  const RemoveMessages = () => {
    setMessage([]);
  };

  return (
    <STTContext.Provider
      value={{
        message,
        channer,
        setMessage,
        setChanner,
        AddMessage,
        RemoveMessages,
      }}>
      {children}
    </STTContext.Provider>
  );
};

export default STTContext;
