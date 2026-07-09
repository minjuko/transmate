import React, {useState, createContext} from 'react';
import axios from 'axios';

const REST_API_KEY = '';

const KakaoContext = createContext();

export const KakaoContextProvider = ({children}) => {
  const [summary, setSummary] = useState();

  const onCreate = async ({prompt}) => {
    try {
      const response = await kogptApi(prompt, 160, 0.4);
      setSummary(response);
      return response;
    } catch (error) {
      console.error('Error:', error);
    }
  };

  return (
    <KakaoContext.Provider value={{summary, onCreate}}>
      {children}
    </KakaoContext.Provider>
  );
};

const kogptApi = async (prompt, maxTokens = 160, topP = 1.0) => {
  try {
    const response = await axios.post(
      'https://api.kakaobrain.com/v1/inference/kogpt/generation',
      {
        prompt: prompt,
        max_tokens: maxTokens,
        top_p: topP,
      },
      {
        headers: {
          Authorization: 'KakaoAK ' + REST_API_KEY,
          'Content-Type': 'application/json',
        },
      },
    );

    const responseText = response.data.generations[0].text;

    return responseText;
  } catch (error) {
    console.error('Error:', error);
    throw error;
  }
};

export default KakaoContext;
