package com.toeic.online.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "en";

    public static final char DEFAULT_ESCAPE_CHAR_QUERY = '\\';

    private Constants() {}

    public static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";
    public static final String DATE_FORMAT_YYYY_MM_DD = "'yyyy-MM-dd";
    public static final String DATE_FORMAT_DDMMYYY = "dd/MM/yyyy";
    public static final String TIME_FORMAT_TO_SECOND = "yyyyMMddhhmmss";
    public static final String TIME_FORMAT_TO_MILISECOND = "yyyyMMddHHmmssSSS";
    public static final Integer TIME_TYPE_DATE = 1;
    public static final Integer TIME_TYPE_MONTH = 2;
    public static final Integer TIME_TYPE_QUARTER = 3;
    public static final Integer TIME_TYPE_YEAR = 4;

    public static final int WIDTH = 255;
    public static final String TIME_ZONE_DEFAULT = "GMT+7";

    public static final String SUCCESS_CODE = "0";
    public static final String SUCCESS_MSG = "SUCCESS";
    public static final String success_msg = "success";
    public static final String FAIL_CODE = "1";
    public static final String FAIL_MSG = "FAIL";
    public static final String fail_msg = "fail";
}
