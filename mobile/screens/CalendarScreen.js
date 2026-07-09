import React, {useEffect, useState, useContext, useMemo} from 'react';
import {
  Text,
  View,
  StyleSheet,
  TouchableOpacity,
  Pressable,
  TextInput,
} from 'react-native';
import CalendarView from '../components/CalendarView';
import Icon from 'react-native-vector-icons/MaterialIcons';
import Dialog from 'react-native-dialog';
import {format} from 'date-fns';
import ScheduleContext from '../contexts/ScheduleContext';
import DateTimePickerModal from 'react-native-modal-datetime-picker';
import CalendarList from '../components/CalendarList';

const CalendarScreen = ({navigation}) => {
  const [visible, setVisible] = useState(false);
  const [pickerVisible, setPickerVisible] = useState(false);
  const [todo, setTodo] = useState('');
  const [mode, setMode] = useState('date');
  const [date, setDate] = useState(new Date());
  const [time, setTime] = useState();
  const [selectedDate, setSelectedDate] = useState(
    format(new Date(), 'yyyy-MM-dd'),
  );

  const today = new Date();

  const formatDate = date => {
    if (!date) return '';

    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();

    return `${year}-${month < 10 ? '0' + month : month}-${
      day < 10 ? '0' + day : day
    }`;
  };

  const formatTime = time => {
    if (!time) return '없음';

    const hours = time.getHours();
    const minutes = time.getMinutes();

    return `${hours < 10 ? '0' + hours : hours}:${
      minutes < 10 ? '0' + minutes : minutes
    }`;
  };

  useEffect(() => {
    navigation.setOptions({
      headerStyle: {
        backgroundColor: '#1976D2',
      },
      headerTintColor: '#ffff',
      // eslint-disable-next-line react/no-unstable-nested-components
      headerRight: () => (
        <View>
          <TouchableOpacity onPress={showDialog} style={styles.addButton}>
            <Icon name="add" size={28} color="white" />
          </TouchableOpacity>
          <View>
            <Dialog.Container visible={visible}>
              <Dialog.Title>일정 추가</Dialog.Title>
              <Dialog.Input placeholder="일정" onChangeText={setTodo} />
              <Pressable onPress={onPressDate} style={styles.modalButton}>
                <Text style={styles.modalText}>날짜</Text>
                <Text style={styles.dateTimeText}>{formatDate(date)}</Text>
              </Pressable>
              <View style={styles.separator} />
              <Pressable onPress={onPressTime} style={styles.modalButton}>
                <Text style={styles.modalText}>시간</Text>
                <Text style={styles.dateTimeText}>{formatTime(time)}</Text>
              </Pressable>
              <DateTimePickerModal
                isVisible={pickerVisible}
                mode={mode}
                date={today}
                onConfirm={onConfirm}
                onCancel={onCancel}
                display="spinner"
                is24Hour={true}
              />
              <Dialog.Button label="취소" onPress={handleCancel} />
              <Dialog.Button label="확인" onPress={handleOk} />
            </Dialog.Container>
          </View>
        </View>
      ),
    });
  });

  const onPressDate = () => {
    setMode('date');
    setPickerVisible(true);
  };

  const onPressTime = () => {
    setMode('time');
    setPickerVisible(true);
  };

  const {schedules} = useContext(ScheduleContext);
  const {onCreate} = useContext(ScheduleContext);

  const markedDates = useMemo(
    () =>
      schedules.reduce((acc, current) => {
        const formattedDate = format(new Date(current.date), 'yyyy-MM-dd');
        acc[formattedDate] = {marked: true};
        return acc;
      }, {}),
    [schedules],
  );

  const showDialog = () => {
    setVisible(true);
  };

  const handleCancel = () => {
    setTime(null);
    setDate(null);
    setVisible(false);
    setTodo('');
  };

  const handleOk = () => {
    onCreate({
      title: todo,
      date: formatDate(date),
      time: formatTime(time),
    });
    setTime(null);
    setDate(new Date());
    setTodo('');
    setVisible(false);
  };

  const onConfirm = selectedDate => {
    setPickerVisible(false);
    if (mode === 'date') {
      setDate(selectedDate);
    } else {
      setTime(selectedDate);
    }
  };

  const onCancel = () => {
    setPickerVisible(false);
  };

  const filteredSchedules = schedules.filter(
    schedule => format(new Date(schedule.date), 'yyyy-MM-dd') === selectedDate,
  );

  return (
    <View style={styles.container}>
      <CalendarView
        markedDates={markedDates}
        selectedDate={selectedDate}
        onSelectDate={setSelectedDate}
      />
      <Text style={styles.bottomText}>{selectedDate}</Text>
      <CalendarList schedules={filteredSchedules} style={styles.list} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
  },
  addButton: {
    marginRight: 16,
  },
  modalButton: {
    marginLeft: 10,
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 15,
  },
  modalText: {
    color: 'black',
    fontSize: 15,
  },
  dateTimeText: {
    marginRight: 15,
    color: 'black',
  },
  list: {
    marginTop: 10,
  },
  bottomText: {
    marginLeft: 15,
    marginTop: 10,
    marginBottom: 10,
    color: 'black',
    fontWeight: 'bold',
  },
});

export default CalendarScreen;
