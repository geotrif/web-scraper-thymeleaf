package com.webscraper.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.webscraper.util.GoogleSheetsUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GoogleSheetService {

    private static final String USER_ENTERED_TEXT = "USER_ENTERED";
    private static final String INSERT_ROWS_TEXT = "INSERT_ROWS";
    private static final String ROWS_TEXT = "ROWS";
    private static final String RAW_TEXT = "RAW";

    private Sheets sheetsService = GoogleSheetsUtil.getSheetsService();

    public GoogleSheetService() throws IOException, GeneralSecurityException {
    }

    public List readFromSpreadsheet(String spreadsheetId, String range) throws IOException {
        ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> values = response.getValues();

        return values;
    }

    public void writeValuesInSpreadsheet(String spreadsheetId, String sheetName, List<Object> values) throws IOException {
        ValueRange appendBody = new ValueRange().setValues(Arrays.asList(values));

        sheetsService.spreadsheets().values().append(spreadsheetId, sheetName, appendBody)
                .setValueInputOption(USER_ENTERED_TEXT)
                .setInsertDataOption(INSERT_ROWS_TEXT)
                .setIncludeValuesInResponse(true)
                .execute();
    }

    public void updateValuesInSpreadsheet(String spreadsheetId, String cellName, Object... values) throws IOException {
        ValueRange body = new ValueRange().setValues(Arrays.asList(Arrays.asList(values)));

        sheetsService.spreadsheets().values().update(spreadsheetId, cellName, body)
                .setValueInputOption(RAW_TEXT)
                .execute();
    }

    public void deleteRowFroSpreadsheet(String spreadsheetId, Integer sheetId, Integer startIndex) throws IOException {
        DeleteDimensionRequest deleteRequest = new DeleteDimensionRequest().setRange(new DimensionRange()
                .setSheetId(sheetId)
                .setDimension(ROWS_TEXT)
                .setStartIndex(startIndex));

        List<Request> requests = new ArrayList<>();
        requests.add(new Request().setDeleteDimension(deleteRequest));

        BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        sheetsService.spreadsheets().batchUpdate(spreadsheetId, body).execute();
    }
}
