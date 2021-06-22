package com.dts.discover.jsearch.parser;

import com.dts.discover.jsearch.config.TestConfig;
import com.dts.discover.jsearch.exception.DataLoadException;
import com.dts.discover.jsearch.exception.DataNotFoundException;
import com.dts.discover.jsearch.exception.KeyNotFoundException;
import org.json.simple.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class GenericParserTest {

    private static GenericParser testDataParser;

    @BeforeAll
    public static void init() throws DataLoadException {
        testDataParser = new GenericParser(TestConfig.TEST_FILE_URL);
    }

    @Test
    void loadDataThrowsExceptionOnInvalidFileName() {
        Exception exception = Assertions.assertThrows(DataLoadException.class, () ->
                new GenericParser("bogusfilename.json"));
        Assertions.assertEquals(exception.getMessage(),"Unable to load the specified data due to file not found");
    }

    @Test
    void loadDataThrowsExceptionOnInvalidFileType() {
        Exception exception = Assertions.assertThrows(DataLoadException.class, () -> {
            new GenericParser(TestConfig.INVALID_FILE_URL);
        });
        Assertions.assertEquals(exception.getMessage(),"Unable to load the specified data due to invalid file format");
    }

    @Test
    void parserThrowsExceptionOnValueInvalidKey() {
        String searchKey = "someKey";
        String searchVal = "someVal";
        Exception exception = Assertions.assertThrows(KeyNotFoundException.class, () ->
                testDataParser.getMatchingObj(searchKey,searchVal));
        Assertions.assertEquals(exception.getMessage(),"Field " + searchKey + " was not found");
    }

    @Test
    void parserThrowsExceptionOnValueNotFound() {
        String searchKey = "_id";
        String searchVal = "someVal";
        Exception exception = Assertions.assertThrows(DataNotFoundException.class, () ->
                testDataParser.getMatchingObj(searchKey,searchVal));
        Assertions.assertEquals(exception.getMessage(),"No data found for field : \"" + searchKey + "\" with provided value \"" + searchVal + "\"");
    }

    @Test
    void parserReturnSingleMatchedObject() {
        String searchKey = "_id";
        String searchVal = "2";
        JSONArray jsonArray = Assertions.assertDoesNotThrow(() ->
                testDataParser.getMatchingObj(searchKey, searchVal)
        );
        Assertions.assertNotNull(jsonArray);
        Assertions.assertTrue(jsonArray.size() == 1);
    }

    @Test
    void parserReturnMultipleMatchedObject() {
        String searchKey = "location";
        String searchVal = "syd";
        JSONArray jsonArray = Assertions.assertDoesNotThrow(() ->
                testDataParser.getMatchingObj(searchKey, searchVal)
        );
        Assertions.assertNotNull(jsonArray);
        Assertions.assertTrue(jsonArray.size() == 2);
    }

    @Test
    void parseSearchesSubListInDifferentPositions() {
        String searchKey = "multi";
        String searchVal = "twenty five";
        JSONArray jsonArray = Assertions.assertDoesNotThrow(() ->
                testDataParser.getMatchingObj(searchKey, searchVal)
        );
        Assertions.assertNotNull(jsonArray);
        Assertions.assertTrue(jsonArray.size() == 4);
    }

    @Test
    void parserReturnsDataOnEmptyValueWhenFieldIsMissing() {
        String searchKey = "location";
        String searchVal = "";
        JSONArray jsonArray = Assertions.assertDoesNotThrow(() ->
                testDataParser.getMatchingObj(searchKey, searchVal)
        );
        Assertions.assertNotNull(jsonArray);
    }

    @Test
    void parserReturnsDataWhenFieldIsEmpty() {
        String searchKey = "name";
        String searchVal = "";
        JSONArray jsonArray = Assertions.assertDoesNotThrow(() ->
                testDataParser.getMatchingObj(searchKey, searchVal)
        );
        Assertions.assertNotNull(jsonArray);
        Assertions.assertTrue(jsonArray.size() == 1);
    }

    @Test
    void parserThrowsExceptionWhenNoDataFoundEmptyValue() {
        String searchKey = "note";
        String searchVal = "";
        Exception exception = Assertions.assertThrows(DataNotFoundException.class, () ->
                testDataParser.getMatchingObj(searchKey,searchVal));
        Assertions.assertEquals(exception.getMessage(),"No data found for field : \"" + searchKey + "\" with provided value \"[]\"");
    }

    @Test
    void parserIgnoresObjectsWithMissingKeys() {
        String searchKey = "location";
        String searchVal = "Mel";
        JSONArray jsonArray = Assertions.assertDoesNotThrow(() ->
                testDataParser.getMatchingObj(searchKey, searchVal)
        );
        Assertions.assertNotNull(jsonArray);
        Assertions.assertTrue(jsonArray.size() == 1);
    }

    @Test
    void parserSearchDataIgnoringCase() {
        String searchKey = "name";
        String searchVal = "jon doe";
        JSONArray jsonArray = Assertions.assertDoesNotThrow(() ->
                testDataParser.getMatchingObj(searchKey, searchVal)
        );
        Assertions.assertNotNull(jsonArray);
        Assertions.assertTrue(jsonArray.size() == 1);
    }

    @Test
    void parserSearchDataWithPartialValues() {
        String searchKey = "name";
        String searchVal = "jon";
        JSONArray jsonArray = Assertions.assertDoesNotThrow(() ->
                testDataParser.getMatchingObj(searchKey, searchVal)
        );
        Assertions.assertNotNull(jsonArray);
        Assertions.assertTrue(jsonArray.size() == 2);
    }

    @Test
    void printKeysReturnsCorrectDataWithMatchingKeys() {
        String expected = "_id" + '\n' +
                "location" + '\n' +
                "multi" + '\n' +
                "name"+ '\n' +
                "note"+ '\n';
        String actual = testDataParser.getKeyString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void checkKeyReturnsFalseOnInvalidKey() {
        Assertions.assertFalse(testDataParser.checkKey("someKey"));
    }

    @Test
    void checkKeyReturnsTrueOnValidKey() {
        Assertions.assertTrue(testDataParser.checkKey("_id"));
    }
}
