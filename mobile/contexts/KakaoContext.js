import React, {useState, createContext} from 'react';
import axios from 'axios';
import Config from 'react-native-config';

const REST_API_KEY = Config.KAKAO_REST_API_KEY ?? '';

const KakaoContext = createContext();

export const KakaoContextProvider = ({children}) => {
  const [summary, setSummary] = useState();

  const onCreate = async ({prompt}) => {
    if (!REST_API_KEY) {
      throw new Error('KAKAO_REST_API_KEY is not configured');
    }

    try {
      const response = await kogptApi(prompt, 160, 0.4);
      setSummary(response);
      return response;
    } catch {
      console.error('Error creating summary');
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
    console.error('Error requesting summary');
    throw error;
  }
};

export default KakaoContext;
