import React from 'react';
import {createContext} from 'react';

const SettingContext = createContext();

export function SettingContextProvider({children}) {
  const languages = [
    {label: '독일 독일어(German)', value: 'de-DE'},
    {label: '미국 영어(USA-English)', value: 'en-US'},
    {label: '미국 스페인어(Spanish-English)', value: 'es-US'},
    {label: '브라질 포르투갈어(Portuguesa)', value: 'pt-BR'},
    {label: '영국 영어(UK-English)', value: 'en-GB'},
    {label: '이탈리아 이탈리아어(Italian)', value: 'it-IT'},
    {label: '인도 힌디어(Hindi)', value: 'hi-IN'},
    {label: '일본 일본어(Japanese)', value: 'ja-JP'},
    {label: '중국 중국어(Chinese)', value: 'zh-CN'},
    {label: '캐나다 프랑스어(Canadian-French)', value: 'fr-CA'},
    {label: '태국 태국어(Thai)', value: 'th-TH'},
    {label: '프랑스 프랑스어(French)', value: 'fr-FR'},
    {label: '한국 한국어(Korean)', value: 'ko-KR'},
    {label: '호주 영어(Australia-English)', value: 'en-AU'},
  ];

  const categorys = [
    {label: '기본-default', value: 'default'},
    {label: '항만-Port', value: 'PORT'},
    {label: '회계-Economy', value: 'ECONOMY'},
    {label: '소프트웨어 및 ICT-SW', value: 'SW'},
  ];

  return (
    <SettingContext.Provider value={{languages, categorys}}>
      {children}
    </SettingContext.Provider>
  );
}

export default SettingContext;
