package com.site.transmate.schedule.dto;

import com.site.transmate.schedule.Schedule;

public record ScheduleResponse(
        Integer id,
        String title,
        String data,
        String date,
        String time
) {

    public static ScheduleResponse from(Schedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getData(),
                schedule.getDate(),
                schedule.getTime()
        );
    }
}
