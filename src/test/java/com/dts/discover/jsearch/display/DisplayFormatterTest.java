package com.dts.discover.jsearch.display;

import com.dts.discover.jsearch.exception.DataLoadException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;


public class DisplayFormatterTest {

    private JSONObject page = new JSONObject();
    private List<String> displayOrder = null;

    @Test
    void displayFormatterThrowsExceptionOnNullPageData() {
        Exception exception = Assertions.assertThrows(DataLoadException.class, () -> new DisplayFormatter(null, List.of("")));
        Assertions.assertEquals(exception.getMessage(),"Missing required data object");
    }

    @Test
    void displayFormatterThrowsExceptionOnNullDisplayOrder() {
        Exception exception = Assertions.assertThrows(DataLoadException.class, () -> new DisplayFormatter(page, null));
        Assertions.assertEquals(exception.getMessage(),"Missing required data object");
    }

    @Test
    void displayFormatterThrowsExceptionOnBothNllObjects() {
        Exception exception = Assertions.assertThrows(DataLoadException.class, () -> new DisplayFormatter(null,null));
        Assertions.assertEquals(exception.getMessage(),"Missing required data object");
    }

    @Test
    @SuppressWarnings("unchecked")
    void displayFormatterThrowsExceptionOnIncompatibleObjectsDisplayOrder() {
        page.put("testField", "test");
        page.put("testField1", "test1");
        displayOrder = List.of("notTest","notTest1");
        Exception exception = Assertions.assertThrows(DataLoadException.class, () -> new DisplayFormatter(page,displayOrder));
        Assertions.assertEquals(exception.getMessage(),"Search data and display order doesn't match");
    }

    @Test
    @SuppressWarnings("unchecked")
    void displayFormatterThrowsExceptionOnIncompleteObjectsDisplayOrder() {
        page.put("testField", "test");
        page.put("testField1", "test1");
        displayOrder = List.of("test");
        Exception exception = Assertions.assertThrows(DataLoadException.class, () -> new DisplayFormatter(page,displayOrder));
        Assertions.assertEquals(exception.getMessage(),"Search data and display order doesn't match");
    }

    @Test
    @SuppressWarnings("unchecked")
    void displayFormatterDoesNotThrowsExceptionOnCorrectData() {
        page.put("testField", "test");
        page.put("testField1", "test1");
        displayOrder = List.of("testField","testField1");
        DisplayFormatter display = Assertions.assertDoesNotThrow(() -> new DisplayFormatter(page,displayOrder));
        Assertions.assertNotNull(display);
    }

    @Test
    @SuppressWarnings("unchecked")
    DisplayFormatter displayFormatterCreatesComplexLayout() {
        page.put("testField", "test");
        page.put("testField1", "test1");
        page.put("arrayOfFields", new String[] {"test","test1","test2"});
        JSONObject childObj = new JSONObject();
        childObj.put("testField", "test");
        childObj.put("testField1", "test1");
        childObj.put("arrayOfFields", new String[] {"test","test1","test2"});
        page.put("childObj", childObj);
        displayOrder = List.of("testField","testField1","arrayOfFields","childObj","childArray");
        JSONArray childArray = new JSONArray();
        JSONObject childObj1 = new JSONObject();
        childObj1.put("testField", "test");
        childObj1.put("testField1", "test1");
        childArray.add(childObj);
        childArray.add(childObj1);
        page.put("childArray", childArray);
        DisplayFormatter display = Assertions.assertDoesNotThrow(() -> new DisplayFormatter(page,displayOrder));
        return display;
    }

    @Test
    void displayFormatterRendersComplexLayout() {
        Assertions.assertNotNull(displayFormatterCreatesComplexLayout().formatPage());
    }
}
