export function mapMeetings(meetings) {
  return meetings
    .map(item => ({
      id: item.meetingid,
      title: item.title,
      department: item.category,
      content: item.data,
      date: item.date,
    }))
    .reverse();
}

export function mapSchedules(schedules) {
  return schedules
    .map(item => ({
      id: item.id,
      title: item.title,
      date: item.date,
      time: item.time,
    }))
    .reverse();
}
