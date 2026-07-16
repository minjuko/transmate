package com.site.transmate.translation;

import com.site.transmate.translation.dto.TranslateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TranslateController {

    private final TranslateService translateService;

    @PostMapping("/translate")
    public String translate(@RequestBody TranslateRequest request) {
        return translateService.translate(request);
    }
}
