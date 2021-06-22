package com.dts.discover.jsearch.display;

import com.dts.discover.jsearch.config.Colour;
import com.dts.discover.jsearch.exception.DataLoadException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DisplayFormatter {

    private JSONObject pageData = null;
    private List<String> displayOrder = null;
    private StringBuffer output = new StringBuffer("\n");

    @SuppressWarnings("unchecked")
    public DisplayFormatter(JSONObject pageData, List<String> displayOrder) throws DataLoadException {
        // Do some validations to see if there is correct data to render a page
        if (pageData == null || displayOrder == null) {
            throw new DataLoadException("Missing required data object");
        }
        if (!pageData.keySet().containsAll(displayOrder)) {
            throw new DataLoadException("Search data and display order doesn't match");
        }
        this.pageData = pageData;
        this.displayOrder = displayOrder;
    }

    public void addDataRow(String key, String value) {
        // Row of data is displayed as double colons separated (::) key value
        output.append(Colour.GREEN);
        output.append(key);
        output.append(Colour.RESET);
        output.append(" :: ");
        output.append(value);
        output.append('\n');
    }

    public void addFooter() {
        output.append('\n');
    }

    public void addHeading(String heading) {
        output.append(Colour.MAGENTA_UNDERLINED);
        output.append(heading.toUpperCase());
        output.append(Colour.RESET);
        output.append('\n');
    }

    public void addJsonObjectToPage(JSONObject obj) {
        // This will sort the keys in an object alphabetical order and add to a page
        Object[] childKeys = obj.keySet().toArray();
        Arrays.sort(childKeys);
        for (Object childKey : childKeys) {
            addDataRow(childKey.toString().trim(), obj.get(childKey).toString().trim());
        }
    }

    @SuppressWarnings("unchecked")
    public String formatPage() {
        // Render the page using the display order of objects
        for (String display : displayOrder) {
            // Add a heading for the parent object
            addHeading(display + " details");
            // Single object
            if (pageData.get(display) instanceof JSONObject) {
                addJsonObjectToPage((JSONObject) pageData.get(display));
            }
            // series of reoccurring objects
            else if (pageData.get(display) instanceof JSONArray) {
                JSONArray objList = (JSONArray) pageData.get(display);
                Iterator<JSONObject> objIterator = objList.iterator();
                int sequence = 1;
                while (objIterator.hasNext()) {
                    // Add a heading to indicate the sequence number of reoccurring objects
                    addHeading(display + " " + sequence++);
                    addJsonObjectToPage(objIterator.next());
                    addFooter();
                }
            }
            // Simple String key value pair
            else {
                addDataRow(display.toString().trim(), pageData.get(display).toString().trim());
            }
            addFooter();
        }
        return output.toString();
    }
}
