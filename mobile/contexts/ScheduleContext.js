import React from 'react';
import {createContext, useState, useRef} from 'react';
import backendApi from '../lib/backendApi';
import {useUserContext} from './UserContext';

const ScheduleContext = createContext();

export const ScheduleContextProvider = ({children}) => {
  const {user} = useUserContext();
  const nextId = useRef(2);
  const [schedules, setSchedules] = useState([
    {
      id: 1,
      title: 'Test1',
      date: '2023-05-17',
      time: '12:27',
    },
  ]);

  const onCreate = async ({title, date, time}) => {
    const schedule = {
      id: nextId,
      title,
      date,
      time,
    };

    try {
      await backendApi.post(`/schedule/create/${user.uid}`, {
        id: nextId.current,
        title: title,
        date: date,
        time: time,
      });
    } catch (error) {
      console.error('Error create Schedule:', error);
    }
    nextId.current += 1;
    setSchedules([schedule, ...schedules]);
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
    } catch (error) {
      console.error('Error modify Schedule:', error);
    }

    setSchedules(nextSchedules);
  };

  const onRemove = async id => {
    const nextSchedules = schedules.filter(schedule => schedule.id !== id);

    try {
      await backendApi.delete(`/schedule/delete/${id}`);
    } catch (error) {
      console.error('Error delete Schedule:', error);
    }

    setSchedules(nextSchedules);
  };

  return (
    <ScheduleContext.Provider
      value={{schedules, setSchedules, onCreate, onModify, onRemove}}>
      {children}
    </ScheduleContext.Provider>
  );
};

export default ScheduleContext;
