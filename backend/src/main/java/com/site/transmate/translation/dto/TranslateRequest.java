package com.site.transmate.translation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TranslateRequest(
        @JsonProperty("Text") @NotBlank(message = "Text는 필수입니다.") String text,
        @JsonProperty("TerminologyNames") String terminologyNames,
        @JsonProperty("SourceLanguageCode")
        @NotBlank(message = "SourceLanguageCode는 필수입니다.")
        @Pattern(regexp = "[a-z]{2,3}", message = "SourceLanguageCode 형식이 올바르지 않습니다.")
        String sourceLanguageCode,
        @JsonProperty("TargetLanguageCode")
        @NotBlank(message = "TargetLanguageCode는 필수입니다.")
        @Pattern(regexp = "[a-z]{2,3}", message = "TargetLanguageCode 형식이 올바르지 않습니다.")
        String targetLanguageCode
) {
}
