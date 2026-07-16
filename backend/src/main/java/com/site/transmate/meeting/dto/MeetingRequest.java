package com.site.transmate.meeting.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MeetingRequest(
        String data,
        @JsonProperty("summary_data") String summaryData,
        String title,
        String category,
        String date
) {
}
