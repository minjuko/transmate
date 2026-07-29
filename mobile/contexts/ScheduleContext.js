import React from 'react';
import {createContext, useState} from 'react';
import backendApi from '../lib/backendApi';
import {useUserContext} from './UserContext';

const ScheduleContext = createContext();

export const ScheduleContextProvider = ({children}) => {
  const {user} = useUserContext();
  const [schedules, setSchedules] = useState([
    {
      id: 1,
      title: 'Test1',
      date: '2023-05-17',
      time: '12:27',
    },
  ]);

  const onCreate = async ({title, date, time}) => {
    try {
      const response = await backendApi.post(`/schedule/create/${user.uid}`, {
        title: title,
        date: date,
        time: time,
      });
      const schedule = {
        id: response.data.id,
        title,
        date,
        time,
      };
      setSchedules(currentSchedules => [schedule, ...currentSchedules]);
    } catch {
      console.error('Error create Schedule');
    }
  };

  const onModify = async modified => {
    //id가 일치하면 교체, 그렇지 않으면 유지
    const nextSchedules = schedules.map(schedule =>
      schedule.id === modified.id ? modified : schedule,
    );

    try {
      await backendApi.patch(`/schedule/patch/${modified.id}`, {
        title: modified.title,
        date: modified.date,
        time: modified.time,
      });
      setSchedules(nextSchedules);
    } catch {
      console.error('Error modify Schedule');
    }
  };

  const onRemove = async id => {
    const nextSchedules = schedules.filter(schedule => schedule.id !== id);

    try {
      await backendApi.delete(`/schedule/delete/${id}`);
      setSchedules(nextSchedules);
    } catch {
      console.error('Error delete Schedule');
    }
  };

  return (
    <ScheduleContext.Provider
      value={{schedules, setSchedules, onCreate, onModify, onRemove}}>
      {children}
    </ScheduleContext.Provider>
  );
};

export default ScheduleContext;
