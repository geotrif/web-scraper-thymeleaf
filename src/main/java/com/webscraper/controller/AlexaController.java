package com.webscraper.controller;

import com.webscraper.model.AlexaInput;
import com.webscraper.model.AlexaSiteInfoData;
import com.webscraper.service.AlexaSiteInfoService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AlexaController {

    private AlexaSiteInfoService alexaSiteInfoService;

    public AlexaController(AlexaSiteInfoService alexaSiteInfoService) {
        this.alexaSiteInfoService = alexaSiteInfoService;
    }

    @GetMapping("/alexa")
    @Async
    public String getAlexaController(Model model) {
        List<AlexaSiteInfoData> alexaDataList = alexaSiteInfoService.showGoogleSheetData();

        model.addAttribute("alexaInput", new AlexaInput());
        model.addAttribute("alexaData", new AlexaSiteInfoData());
        model.addAttribute("alexaList", alexaDataList);

        return "alexa";
    }

    @PostMapping("/alexa")
    @Async
    public String getAlexaData(@ModelAttribute AlexaInput alexaInput, Model model) {
        AlexaSiteInfoData alexaSiteInfoData = alexaSiteInfoService.resolveAlexaSiteInfoInformation(alexaInput.getSiteName());

        model.addAttribute("alexaInput", alexaInput);
        model.addAttribute("alexaData", alexaSiteInfoData);

        return "alexa";
    }
}
