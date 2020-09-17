package com.webscraper.controller;

import com.webscraper.model.TmdbData;
import com.webscraper.model.TmdbInput;
import com.webscraper.service.TMDBService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class TMDBController {

    private TMDBService tmdbService;

    public TMDBController(TMDBService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping("/tmdb")
    @Async
    public String getTMDBPage(Model model) {
        List<TmdbData> tmdbDataList = tmdbService.showGoogleSheetsValue();

        model.addAttribute("tmdbInput", new TmdbInput());
        model.addAttribute("tmdbData", new TmdbData());
        model.addAttribute("tmdbList", tmdbDataList);

        return "tmdb";
    }

    @PostMapping("/tmdb")
    @Async
    public String getTMDBData(@ModelAttribute TmdbInput tmdbInput, Model model) {
        TmdbData tmdbData = tmdbService.searchMovieOnTmdb(tmdbInput.getMovieTitle());

        model.addAttribute("tmdbInput", tmdbInput);
        model.addAttribute("tmdbData", tmdbData);

        return "tmdb";
    }
}
