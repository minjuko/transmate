import {mapMeetings, mapSchedules} from '../lib/backendMappers';

describe('backend response mappers', () => {
  it('maps and reverses meeting responses for the mobile file model', () => {
    const result = mapMeetings([
      {
        meetingid: 1,
        title: 'first',
        category: 'development',
        data: 'first content',
        date: '2026.07.15',
      },
      {
        meetingid: 2,
        title: 'second',
        category: 'sales',
        data: 'second content',
        date: '2026.07.16',
      },
    ]);

    expect(result).toEqual([
      {
        id: 2,
        title: 'second',
        department: 'sales',
        content: 'second content',
        date: '2026.07.16',
      },
      {
        id: 1,
        title: 'first',
        department: 'development',
        content: 'first content',
        date: '2026.07.15',
      },
    ]);
  });

  it('preserves schedule time while mapping and reversing responses', () => {
    const result = mapSchedules([
      {id: 1, title: 'first', date: '2026-07-15', time: '09:00'},
      {id: 2, title: 'second', date: '2026-07-16', time: '14:30'},
    ]);

    expect(result).toEqual([
      {id: 2, title: 'second', date: '2026-07-16', time: '14:30'},
      {id: 1, title: 'first', date: '2026-07-15', time: '09:00'},
    ]);
  });
});
