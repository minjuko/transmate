import backendApi from '../lib/backendApi';
import {
  requestTranslation,
  TRANSLATION_TIMEOUT_MS,
  translationErrorMessage,
} from '../lib/translationApi';

jest.mock('../lib/backendApi', () => ({post: jest.fn()}));

const request = {
  text: 'port',
  terminologyName: 'trade',
  sourceLanguage: 'en',
  targetLanguage: 'ko',
};

beforeEach(() => backendApi.post.mockReset());

it('sends the terminology and returns a nonempty translation with a timeout', async () => {
  backendApi.post.mockResolvedValue({data: '항만'});

  await expect(requestTranslation(request)).resolves.toBe('항만');
  expect(backendApi.post).toHaveBeenCalledWith(
    '/translate',
    {
      Text: 'port',
      TerminologyNames: 'trade',
      SourceLanguageCode: 'en',
      TargetLanguageCode: 'ko',
    },
    {timeout: TRANSLATION_TIMEOUT_MS},
  );
});

it.each(['', '  ', null, undefined])(
  'rejects an empty translation response: %s',
  async data => {
    backendApi.post.mockResolvedValue({data});

    await expect(requestTranslation(request)).rejects.toMatchObject({
      code: 'EMPTY_TRANSLATION',
    });
  },
);

it('propagates a request timeout for the screen to offer a retry', async () => {
  const timeout = {code: 'ECONNABORTED'};
  backendApi.post.mockRejectedValue(timeout);

  await expect(requestTranslation(request)).rejects.toBe(timeout);
  expect(translationErrorMessage(timeout)).toContain('시간이 초과');
  expect(translationErrorMessage({code: 'EMPTY_TRANSLATION'})).toContain(
    '비어 있습니다',
  );
  expect(translationErrorMessage({})).toContain('다시 시도');
});
