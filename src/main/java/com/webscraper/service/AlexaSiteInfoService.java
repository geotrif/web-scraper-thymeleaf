package com.webscraper.service;

import static com.webscraper.util.Constants.*;

import com.webscraper.model.AlexaSiteInfoData;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class AlexaSiteInfoService {

    private static final Logger LOGGER = Logger.getLogger(AlexaSiteInfoService.class.getName());

    private static final String ALEXA_SITEINFO_URL = "https://www.alexa.com/siteinfo/%s";

    private static final String WEBSITE_NAME_TAG = "strong";
    private static final String OPTIMIZATION_OPPORTUNITIES_TAG = "div.value";
    private static final String SEARCH_TRAFFIC_TAG = "div.ThirdFull.ProgressNumberBar > span";
    private static final String DAILY_TIME_ON_SITE_TAG = "div.rankmini-daily > div.rankmini-rank";
    private static final String GLOBAL_SITE_RANKING_TAG = "div.rankmini-global > div.rankmini-rank";
    private static final String JSOUP_CONNECTION_ERROR_MESSAGE = "Couldn't scrape information for website %s";
    private static final String JSOUP_CONNECTION_INFO_MESSAGE = "Started information scraping for website %s";
    private static final String JSOUP_CONNECTION_SCRAPE_SUCCESS_MESSAGE = "Scraped data [%s %s %s %s %s] for website %s";

    private final GoogleSheetService googleSheetService;

    public AlexaSiteInfoService(GoogleSheetService googleSheetService) {
        this.googleSheetService = googleSheetService;
    }

    public AlexaSiteInfoData resolveAlexaSiteInfoInformation(String website) {
        LOGGER.info(String.format(JSOUP_CONNECTION_INFO_MESSAGE, website));
        Connection connection = Jsoup.connect(String.format(ALEXA_SITEINFO_URL, website)).ignoreHttpErrors(true).timeout(30 * 1000);

        String websiteName = StringUtils.EMPTY;
        String optimizationOpportunities = StringUtils.EMPTY;
        String searchTraffic = StringUtils.EMPTY;
        String globalSiteRanking = StringUtils.EMPTY;
        String dailyTimeOnSite = StringUtils.EMPTY;

        try {
            websiteName = connection.get().select(WEBSITE_NAME_TAG).first().text();
            optimizationOpportunities = connection.get().select(OPTIMIZATION_OPPORTUNITIES_TAG).first().text();
            searchTraffic = connection.get().select(SEARCH_TRAFFIC_TAG).first().text();
            globalSiteRanking = connection.get().select(GLOBAL_SITE_RANKING_TAG).first().text();
            dailyTimeOnSite = connection.get().select(DAILY_TIME_ON_SITE_TAG).first().text();
        } catch (IOException e) {
            LOGGER.severe(String.format(JSOUP_CONNECTION_ERROR_MESSAGE, website));
        }

        LOGGER.info(String.format(JSOUP_CONNECTION_SCRAPE_SUCCESS_MESSAGE, websiteName, optimizationOpportunities, searchTraffic, globalSiteRanking, dailyTimeOnSite, website));
        return new AlexaSiteInfoData(websiteName, optimizationOpportunities, searchTraffic, globalSiteRanking, dailyTimeOnSite);
    }

    public List<AlexaSiteInfoData> showGoogleSheetData() {
        List<List<Object>> rowResults = new ArrayList<>();
        List<AlexaSiteInfoData> alexaDataList = new ArrayList<>();

        try {
            rowResults = googleSheetService.readFromSpreadsheet(ALEXA_SPREADSHEET_ID, ALEXA_SHEET_RANGE);
        } catch (IOException e) {
            LOGGER.severe(String.format(READ_VALUES_FROM_GOOGLE_SHEET_ERROR_MESSAGE, ALEXA_SPREADSHEET_ID, ALEXA_SHEET_RANGE));
        }

        rowResults.forEach(rowValue -> alexaDataTransformer(rowValue, alexaDataList));

        return alexaDataList;
    }

    private void alexaDataTransformer(List<Object> rowValue, List<AlexaSiteInfoData> alexaDataList) {
        AlexaSiteInfoData alexaSiteInfoData = new AlexaSiteInfoData();
        alexaSiteInfoData.setWebsiteName(rowValue.get(0).toString());
        alexaSiteInfoData.setOptimizationOpportunities(rowValue.get(1).toString());
        alexaSiteInfoData.setSearchTraffic(rowValue.get(2).toString());
        alexaSiteInfoData.setGlobalSiteRanking(rowValue.get(3).toString());
        alexaSiteInfoData.setDailyTimeOnSite(rowValue.get(4).toString());

        alexaDataList.add(alexaSiteInfoData);
    }
}
