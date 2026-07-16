package com.site.transmate.meeting.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.site.transmate.api.OnCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MeetingRequest(
        String data,
        @JsonProperty("summary_data") String summaryData,
        @NotBlank(groups = OnCreate.class, message = "title은 필수입니다.") String title,
        String category,
        @NotBlank(groups = OnCreate.class, message = "date는 필수입니다.")
        @Pattern(regexp = "\\d{4}[-.]\\d{2}[-.]\\d{2}", message = "date는 yyyy-MM-dd 또는 yyyy.MM.dd 형식이어야 합니다.")
        String date
) {
}
