import React from 'react';
import {Calendar} from 'react-native-calendars';
import {StyleSheet} from 'react-native';

const CalendarView = ({markedDates, selectedDate, onSelectDate}) => {
  const markedSelectedDate = {
    ...markedDates,
    [selectedDate]: {
      selected: true,
      marked: markedDates[selectedDate]?.marked,
    },
  };

  return (
    <Calendar
      style={styles.calendar}
      onDayPress={day => {
        onSelectDate(day.dateString);
      }}
      markedDates={markedSelectedDate}
      theme={{
        selectedDayBackgroundColor: '#1976D2',
        arrowColor: '#1976D2',
        dotColor: '#1976D2',
        todayTextColor: '#1976D2',
      }}
      firstDay={7}></Calendar>
  );
};

const styles = StyleSheet.create({
  calendar: {
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
});

export default CalendarView;
