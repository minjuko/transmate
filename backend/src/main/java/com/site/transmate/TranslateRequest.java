package com.site.transmate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TranslateRequest {

    private String text;
    private String terminologyNames;
    private String sourceLanguageCode;
    private String targetLanguageCode;

}
