package com.bjbs.haji.business.apis.controllers.utility;

import static com.bjbs.haji.business.apis.controllers.utility.ProvidedStaticLocale.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatterUtil {

    //#region Date and Time Formatter
    /**
     * Format {@code LocalDate date} to a Universal Date Syntax {@code String} representation.<br/>
     * Usually be used as SQL syntax or converting from one to another.<br/>
     * <i>example: 1970-01-01</i>
     * 
     * @author s53kr_lm8a6r
     * 
     * @param date the date of type {@code LocalDate} for which to be converted
     * @return formatted {@code String} representation of the date, forming (yyyy-MM-dd)
     */
    public static String getSyntaxDateFrom(LocalDate date) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date);
    }

    /**
     * Format {@code LocalDateTime dateTime} to a Universal Date-Only Syntax {@code String} representation.<br/>
     * Usually be used as SQL syntax or converting from one to another.<br/>
     * <i>example: 1970-01-01</i>
     * 
     * @author s53kr_lm8a6r
     * 
     * @param dateTime the date of type {@code LocalDateTime} for which to be converted
     * @return formatted {@code String} representation of the date, forming (yyyy-MM-dd)
     */
    public static String getSyntaxDateFrom(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(dateTime);
    }

    /**
     * Format {@code LocalDate date} to a Generated Default Password {@code String} representation.<br/>
     * This should only be used to generate default password with user's birthday on account creation.<br/>
     * <i>example: 01011970</i>
     * 
     * @author s53kr_lm8a6r
     * 
     * @param date the date of type {@code LocalDate} for which to be converted
     * @return formatted {@code String} representation of the date, forming (ddMMyyyy)
     */
    public static String getPasswordFormedDateFrom(LocalDate date) {
        return DateTimeFormatter.ofPattern("ddMMyyyy").format(date);
    }

    /**
     * Format {@code LocalDateTime dateTime} to a Generated Default Password {@code String} representation.<br/>
     * This should only be used to generate default password with user's birthday on account creation.<br/>
     * <i>example: 01011970</i>
     * 
     * @author s53kr_lm8a6r
     * 
     * @param dateTime the date of type {@code LocalDateTime} for which to be converted
     * @return formatted {@code String} representation of the date, forming (ddMMyyyy)
     */
    public static String getPasswordFormedDateFrom(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern("ddMMyyyy").format(dateTime);
    }

    /**
     * Format {@code LocalDate date} to a Full Readable Date {@code String} representation.<br/>
     * This will use Indonesian Locale to format date as that is the application's default {@code Locale}.<br/>
     * <i>example: Kamis, 1 Januari 1970</i>
     * 
     * @author s53kr_lm8a6r
     * 
     * @param date the date of type {@code LocalDate} for which to be converted
     * @return formatted {@code String} representation of the date, forming (EEEE, d MMMM yyyy)
     */
    public static String getFullReadableDateFrom(LocalDate date) {
        return DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", LOCALE_ID).format(date);
    }

    /**
     * Format {@code LocalDateTime dateTime} to a Full Readable Date {@code String} representation.<br/>
     * This will use Indonesian Locale to format date as that is the application's default {@code Locale}.<br/>
     * <i>example: Kamis, 1 Januari 1970</i>
     * 
     * @author s53kr_lm8a6r
     * 
     * @param dateTime the date of type {@code LocalDateTime} for which to be converted
     * @return formatted {@code String} representation of the date, forming (EEEE, d MMMM yyyy)
     */
    public static String getFullReadableDateFrom(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", LOCALE_ID).format(dateTime);
    }

    /**
     * Format {@code LocalDate date} to a Half Readable Date {@code String} representation.<br/>
     * This will use Indonesian Locale to format date as that is the application's default {@code Locale}.<br/>
     * <i>example: 1 Januari 1970</i>
     * 
     * @author s53kr_lm8a6r
     * 
     * @param date the date of type {@code LocalDate} for which to be converted
     * @return formatted {@code String} representation of the date, forming (d MMMM yyyy)
     */
    public static String getHalfReadableDateFrom(LocalDate date) {
        return DateTimeFormatter.ofPattern("d MMMM yyyy", LOCALE_ID).format(date);
    }

    /**
     * Format {@code LocalDateTime dateTime} to a Half Readable Date {@code String} representation.<br/>
     * This will use Indonesian Locale to format date as that is the application's default {@code Locale}.<br/>
     * <i>example: 1 Januari 1970</i>
     * 
     * @author s53kr_lm8a6r
     * 
     * @param dateTime the date of type {@code LocalDateTime} for which to be converted
     * @return formatted {@code String} representation of the date, forming (d MMMM yyyy)
     */
    public static String getHalfReadableDateFrom(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern("d MMMM yyyy", LOCALE_ID).format(dateTime);
    }

    /**
     * Format {@code LocalDate date} to a Short Readable Date {@code String} representation.<br/>
     * This will use Indonesian Locale to format date as that is the application's default {@code Locale}.<br/>
     * <i>example: 1 Jan 1970</i>
     * 
     * @author s53kr_lm8a6r
     * 
     * @param date the date of type {@code LocalDate} for which to be converted
     * @return formatted {@code String} representation of the date, forming (d MMM yyyy)
     */
    public static String getShortReadableDateFrom(LocalDate date) {
        return DateTimeFormatter.ofPattern("d MMM yyyy", LOCALE_ID).format(date);
    }

    /**
     * Format {@code LocalDateTime dateTime} to a Short Readable Date {@code String} representation.<br/>
     * This will use Indonesian Locale to format date as that is the application's default {@code Locale}.<br/>
     * <i>example: 1 Jan 1970</i>
     * 
     * @author s53kr_lm8a6r
     * 
     * @param dateTime the date of type {@code LocalDateTime} for which to be converted
     * @return formatted {@code String} representation of the date, forming (d MMM yyyy)
     */
    public static String getShortReadableDateFrom(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern("d MMM yyyy", LOCALE_ID).format(dateTime);
    }

    /**
     * Format {@code LocalDate date} to a Numeric Readable Date {@code String} representation.<br/>
     * This will use Indonesian Locale to format date as that is the application's default {@code Locale}.<br/>
     * <i>example: 01/01/1970</i>
     * 
     * @author s53kr_lm8a6r
     * 
     * @param date the date of type {@code LocalDate} for which to be converted
     * @return formatted {@code String} representation of the date, forming (dd/MM/yyyy)
     */
    public static String getNumericReadableDateFrom(LocalDate date) {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(date);
    }

    /**
     * Format {@code LocalDateTime dateTime} to a Numeric Readable Date {@code String} representation.<br/>
     * This will use Indonesian Locale to format date as that is the application's default {@code Locale}.<br/>
     * <i>example: 01/01/1970</i>
     * 
     * @author s53kr_lm8a6r
     * 
     * @param dateTime the date of type {@code LocalDateTime} for which to be converted
     * @return formatted {@code String} representation of the date, forming (dd/MM/yyyy)
     */
    public static String getNumericReadableDateFrom(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(dateTime);
    }

    /**
     * Format {@code LocalDateTime dateTime} to a Long 24-Hour Time Format {@code String} representation.<br/>
     * Usually be used as SQL syntax or converting from one to another.<br/>
     * <i>example: 16:20:13</i>
     * 
     * @author s53kr_lm8a6r
     * 
     * @param dateTime the date of type {@code LocalDateTime} for which to be converted
     * @return formatted {@code String} representation of the date, forming (HH:mm:ss)
     */
    public static String getLongTimeFrom(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern("HH:mm:ss").format(dateTime);
    }

    /**
     * Format {@code LocalDateTime dateTime} to a Long 12-Hour Time Format {@code String} representation.<br/>
     * Usually be used as SQL syntax or converting from one to another.<br/>
     * <i>example: 04:20:13 PM</i>
     * 
     * @author s53kr_lm8a6r
     * 
     * @param dateTime the date of type {@code LocalDateTime} for which to be converted
     * @return formatted {@code String} representation of the date, forming (hh:mm:ss a)
     */
    public static String getLongAmPmTimeFrom(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern("hh:mm:ss a").format(dateTime);
    }

    /**
     * Format {@code LocalDateTime dateTime} to a Short 24-Hour Time Format {@code String} representation.<br/>
     * Usually be used as SQL syntax or converting from one to another.<br/>
     * <i>example: 16:20</i>
     * 
     * @author s53kr_lm8a6r
     * 
     * @param dateTime the date of type {@code LocalDateTime} for which to be converted
     * @return formatted {@code String} representation of the date, forming (HH:mm)
     */
    public static String getShortTimeFrom(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern("HH:mm:ss").format(dateTime);
    }

    /**
     * Format {@code LocalDateTime dateTime} to a Short 12-Hour Time Format {@code String} representation.<br/>
     * Usually be used as SQL syntax or converting from one to another.<br/>
     * <i>example: 04:20 PM</i>
     * 
     * @author s53kr_lm8a6r
     * 
     * @param dateTime the date of type {@code LocalDateTime} for which to be converted
     * @return formatted {@code String} representation of the date, forming (hh:mm a)
     */
    public static String getShortAmPmTimeFrom(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern("hh:mm:ss a").format(dateTime);
    }
    //#endregion
    
}