import {v4 as uuidv4} from 'uuid';
import backendApi from './backendApi';

export function createLocalMeeting({title, department, content, date}, id) {
  return {id, title, department, content, date};
}

export async function createMeeting(userId, meeting, localId = uuidv4()) {
  const response = await backendApi.post(`/meeting/create/${userId}`, {
    title: meeting.title,
    category: meeting.department,
    data: meeting.content,
    date: meeting.date,
  });

  return createLocalMeeting(meeting, response.data.meetingid ?? localId);
}
