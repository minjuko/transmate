import React from 'react';
import {createContext, useState} from 'react';

const STTContext = createContext();

export const STTContextProvider = ({children}) => {
  const [channer, setChanner] = useState('');
  const [message, setMessage] = useState([]);

  const AddMessage = text => {
    if (text !== undefined) {
      setMessage(prevMessages => [...prevMessages, text]);
    }
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
