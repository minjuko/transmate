package com.site.transmate.schedule.dto;

import com.site.transmate.api.OnCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ScheduleRequest(
        String data,
        @NotBlank(groups = OnCreate.class, message = "title은 필수입니다.")
        @Size(max = 20, message = "title은 20자 이하여야 합니다.")
        String title,
        @NotBlank(groups = OnCreate.class, message = "date는 필수입니다.")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "date는 yyyy-MM-dd 형식이어야 합니다.")
        String date,
        @NotBlank(groups = OnCreate.class, message = "time은 필수입니다.")
        @Pattern(regexp = "(?:[01]\\d|2[0-3]):[0-5]\\d", message = "time은 HH:mm 형식이어야 합니다.")
        String time
) {
}
