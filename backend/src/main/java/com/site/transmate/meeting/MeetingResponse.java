package com.site.transmate.meeting;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MeetingResponse(
        Integer meetingid,
        String title,
        String data,
        @JsonProperty("summary_data") String summaryData,
        String category,
        String date
) {

    public static MeetingResponse from(Meeting meeting) {
        String responseDate = meeting.getDate();

        if (responseDate == null && meeting.getCreateDate() != null) {
            responseDate = meeting.getCreateDate().toLocalDate().toString();
        }

        return new MeetingResponse(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getData(),
                meeting.getSummary_data(),
                meeting.getCategory(),
                responseDate
        );
    }
}
