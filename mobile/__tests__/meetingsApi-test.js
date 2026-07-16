import backendApi from '../lib/backendApi';
import {createMeeting} from '../lib/meetingsApi';

jest.mock('../lib/backendApi', () => ({
  __esModule: true,
  default: {post: jest.fn()},
}));

describe('meetingsApi', () => {
  it('uses the server meeting ID for subsequent update and delete requests', async () => {
    backendApi.post.mockResolvedValue({data: {meetingid: 42}});
    const meeting = {
      title: 'weekly meeting',
      department: 'development',
      content: 'meeting content',
      date: '2026.07.16',
    };

    const result = await createMeeting('firebase-user-id', meeting, 'local-id');

    expect(backendApi.post).toHaveBeenCalledWith(
      '/meeting/create/firebase-user-id',
      {
        title: 'weekly meeting',
        category: 'development',
        data: 'meeting content',
        date: '2026.07.16',
      },
    );
    expect(result).toEqual({...meeting, id: 42});
  });
});
