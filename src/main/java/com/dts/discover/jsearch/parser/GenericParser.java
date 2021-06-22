package com.dts.discover.jsearch.parser;

import com.dts.discover.jsearch.exception.DataLoadException;
import com.dts.discover.jsearch.exception.DataNotFoundException;
import com.dts.discover.jsearch.exception.KeyNotFoundException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeSet;

import static java.util.stream.Collectors.toCollection;

/*
 * This is the heart of this search application. Does the most resource intensive tasks here and the design thinking
 * behind this is to parse the Json data into list of generic key/values rather than binding them to a pre defined
 * object structure and do the searching on the keys. Which makes it simple and extensible to different Json structures.
 *
 * I've chosen to use the Simple-jason library and Java streams API to do the job. Simple-json provides a very basic
 * and fast Jason parsing capabilities and streams API provides very powerful yet less verbose code which will eliminate
 * lots of boilerplate code and more importantly can be parallelized to take advantage of modern multicore processors.
 *
 * However there are some drawbacks such as the version of Simple-jason library is old and not supported by Google
 * anymore. Event hough there is a new version of the lib which is a fork from the original Google's code I've decided
 * not to use it as this version is simpler to use and good fit for my design. Also one other assumption is this will
 * be fed with properly constructed jason data otherwise it will fail to load.
 */
public class GenericParser {

    private final JSONArray searchObjects;
    private final Set<String> keys;
    private String keyString;

    @SuppressWarnings("unchecked")
    public GenericParser(String fileName) throws DataLoadException {
        // Using the lib Simple Json parser to parse data
        JSONParser parser = new JSONParser();
        try {
            var file = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(fileName),
                    StandardCharsets.ISO_8859_1);
            // Parse the data into JSONArray
            searchObjects = (JSONArray) parser.parse(file);
            // Using a TreeSet so the keys are sorted
            keys = new TreeSet<String>();
            // Capture the superset of keys
            searchObjects.parallelStream().forEach(item -> {
                keys.addAll(((JSONObject) item).keySet());
            });
            // Populate the string represents the keys which can be printed out
            keyString = "";
            keys.stream().forEach(key -> keyString += key + '\n');
        } catch (ParseException e) {
            // Couldn't parse the data most likely not a Json file
            throw new DataLoadException("Unable to load the specified data due to invalid file format");
        } catch (Exception e) {
            // Couldn't find the resource, check your paths in AppConfig
            System.err.println(e.getMessage());
            throw new DataLoadException("Unable to load the specified data due to file not found");
        }
    }

    public String getKeyString() {
        // Helper method get all the keys as a string
        return keyString;
    }

    public boolean checkKey(String key) {
        return keys.contains(key);
    }

    @SuppressWarnings("unchecked")
    public JSONArray getMatchingObj(String key, String value) throws KeyNotFoundException, DataNotFoundException {
        if (!keys.contains(key)) {
            // Using an invalid key to search data throw exception
            throw new KeyNotFoundException("Field " + key + " was not found");
        }
        JSONArray retList;
        // Assuming partial keyword searches provides a better user experience over exact value search
        // most of the time. However the tradeoff is sometimes you will get too many matching results back
        // Search using empty value
        if (value.isEmpty()) {
            // Filter based on any objects with non existent keys or keys with empty values
            retList = (JSONArray) searchObjects.parallelStream().
                    filter(searchObject -> !((JSONObject) searchObject).containsKey(key) ||
                            ((JSONObject) searchObject).get(key).toString().isEmpty()).
                    collect(toCollection(JSONArray::new));
        } else {
            // Filer any objects which contain the key and contain the given value ignoring the case
            retList = (JSONArray) searchObjects.parallelStream().
                    filter(searchObject -> ((JSONObject) searchObject).containsKey(key) &&
                            ((JSONObject) searchObject).get(key).toString().toUpperCase().contains(value.toUpperCase())).
                    collect(toCollection(JSONArray::new));
        }
        if (retList.isEmpty()) {
            // No matching data found throw an exception
            throw new DataNotFoundException("No data found for field : \"" + key +
                    "\" with provided value \"" + (value.isEmpty() ? "[]" : value) + "\"");
        }
        return retList;
    }

}