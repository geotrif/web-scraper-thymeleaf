package com.webscraper.service;

import static com.webscraper.util.Constants.*;

import com.webscraper.model.TmdbData;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class TMDBService {

    private static final Logger LOGGER = Logger.getLogger(TMDBService.class.getName());

    private static final String TMDB_SEARCH_URL = "https://www.themoviedb.org/search?query=%s&language=en-US";
    private static final String TMDB_BASE_URL = "https://www.themoviedb.org/";

    private static final String HREF_ATTRIBUTE = "href";
    private static final String PICTURE_TAG = "div.image_content > a";
    private static final String DATA_PERCENTAGE_ATTRIBUTE = "data-percent";
    private static final String JSOUP_CONNECTION_ERROR_MESSAGE = "Couldn't scrape information for movie %s";
    private static final String JSOUP_CONNECTION_INFO_MESSAGE = "Started information scraping for movie %s";
    private static final String JSOUP_CONNECTION_SCRAPE_SUCCESS_MESSAGE = "Scraped data [%s %s %s %s %s] for website %s";
    private static final String URL_TAG = "#card_movie_4bc8ac2e017a3c122d04eeb4 > div > div.details > div.wrapper > div > a";
    private static final String TITLE_TAG = "#original_header > div.header_poster_wrapper.true > section > div.title.ott_true > h2 > a";
    private static final String GENRES_TAG = "#original_header > div.header_poster_wrapper.true > section > div.title.ott_true > div > span.genres";
    private static final String RUNTIME_TAG = "#original_header > div.header_poster_wrapper.true > section > div.title.ott_true > div > span.runtime";
    private static final String RELEASE_DATE_TAG = "#original_header > div.header_poster_wrapper.true > section > div.title.ott_true > div > span.release";
    private static final String USER_SCORE_TAG = "#original_header > div.header_poster_wrapper.true > section > ul > li.chart > div.consensus.details > div > div";

    private final GoogleSheetService googleSheetService;

    public TMDBService(GoogleSheetService googleSheetService) {
        this.googleSheetService = googleSheetService;
    }

    public TmdbData searchMovieOnTmdb(String movieTitle) {
        LOGGER.info(String.format(JSOUP_CONNECTION_INFO_MESSAGE, movieTitle));
        Connection connection = Jsoup.connect(String.format(TMDB_SEARCH_URL, movieTitle)).ignoreHttpErrors(true).timeout(30 * 1000);
        String movieUrl = StringUtils.EMPTY;

        try {
            movieUrl = connection.get().select(URL_TAG).first().attr(HREF_ATTRIBUTE);
            System.out.println(movieUrl);
        } catch (IOException e) {
            LOGGER.severe(String.format(JSOUP_CONNECTION_ERROR_MESSAGE, movieTitle));
        }

        return resolveTMDBInformation(movieUrl);
    }

    private TmdbData resolveTMDBInformation(String movieUrl) {
        Connection connection = Jsoup.connect(TMDB_BASE_URL + movieUrl).ignoreHttpErrors(true).timeout(30 * 1000);
        String title = StringUtils.EMPTY;
        String picture = StringUtils.EMPTY;
        String userScore = StringUtils.EMPTY;
        String releaseDate = StringUtils.EMPTY;
        String runtime = StringUtils.EMPTY;
        String genres = StringUtils.EMPTY;

        try {
            title = connection.get().select(TITLE_TAG).first().text();
            picture = connection.get().select(PICTURE_TAG).attr(HREF_ATTRIBUTE);
            userScore = connection.get().select(USER_SCORE_TAG).attr(DATA_PERCENTAGE_ATTRIBUTE);
            releaseDate = connection.get().select(RELEASE_DATE_TAG).first().text().substring(0, 11);
            runtime = connection.get().select(RUNTIME_TAG).first().text();
            genres = connection.get().select(GENRES_TAG).first().text();
        } catch (IOException e) {
            LOGGER.severe(String.format(JSOUP_CONNECTION_ERROR_MESSAGE, movieUrl));
        }

        LOGGER.info(String.format(JSOUP_CONNECTION_SCRAPE_SUCCESS_MESSAGE, title, userScore, releaseDate, runtime, genres, movieUrl));
        return new TmdbData(title, picture, userScore, releaseDate, runtime, genres);
    }

    public List<TmdbData> showGoogleSheetsValue() {
        List<List<Object>> rowResults = new ArrayList<>();
        List<TmdbData> tmdbDataList = new ArrayList<>();

        try {
            rowResults = googleSheetService.readFromSpreadsheet(TMDB_SPREADSHEET_ID, TMDB_SHEET_RANGE);
        } catch (IOException e) {
            LOGGER.severe(String.format(READ_VALUES_FROM_GOOGLE_SHEET_ERROR_MESSAGE, TMDB_SPREADSHEET_ID, TMDB_SHEET_RANGE));
        }

        rowResults.forEach(rowValue -> tmdbDataTransformer(rowValue, tmdbDataList));

        return tmdbDataList;
    }

    private void tmdbDataTransformer(List<Object> rowValue, List<TmdbData> tmdbDataList) {
        TmdbData tmdbData = new TmdbData();
        tmdbData.setTitle(rowValue.get(0).toString());
        tmdbData.setUserScore(rowValue.get(1).toString());
        tmdbData.setReleaseDate(rowValue.get(2).toString());
        tmdbData.setRuntime(rowValue.get(3).toString());
        tmdbData.setGenres(rowValue.get(4).toString());

        tmdbDataList.add(tmdbData);
    }
}
