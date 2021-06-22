package com.dts.discover.jsearch.config;

/*
 * Enumeration of colours to configure the screen output
 */
public enum Colour {
    RESET("\033[0m"),
    RED("\033[0;31m"),
    GREEN("\033[0;32m"),
    MAGENTA_UNDERLINED("\033[4;35m");

    private final String code;

    Colour(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}
