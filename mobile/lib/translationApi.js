import backendApi from './backendApi';

export const TRANSLATION_TIMEOUT_MS = 10000;

export async function requestTranslation({
  text,
  terminologyName,
  sourceLanguage,
  targetLanguage,
}) {
  const response = await backendApi.post(
    '/translate',
    {
      Text: text,
      TerminologyNames: terminologyName,
      SourceLanguageCode: sourceLanguage,
      TargetLanguageCode: targetLanguage,
    },
    {timeout: TRANSLATION_TIMEOUT_MS},
  );

  if (typeof response.data !== 'string' || !response.data.trim()) {
    const error = new Error('Translation response is empty');
    error.code = 'EMPTY_TRANSLATION';
    throw error;
  }

  return response.data;
}

export function translationErrorMessage(error) {
  if (error?.code === 'EMPTY_TRANSLATION') {
    return '번역 결과가 비어 있습니다. 다시 시도해주세요.';
  }
  if (error?.code === 'ECONNABORTED' || error?.code === 'ETIMEDOUT') {
    return '번역 요청 시간이 초과되었습니다. 네트워크를 확인하고 다시 시도해주세요.';
  }
  return '번역을 완료하지 못했습니다. 네트워크와 서버 상태를 확인한 뒤 다시 시도해주세요.';
}
