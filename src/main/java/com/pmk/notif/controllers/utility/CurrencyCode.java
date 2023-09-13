package com.pmk.notif.controllers.utility;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Enum of supported Currency Code, may be extended.
 * @author s53kr_lm8a6r
 */
public enum CurrencyCode {

    /**
     * Indonesian Rupiah
     */
    IDR(new Locale("id", "ID")),

    /**
     * United States Dollar
     */
    USD(Locale.US),

    /**
     * Great Britain Pound Sterling
     */
    GBP(Locale.UK),

    /**
     * European Euro (Used Germany Locale)
     */
    EUR(Locale.GERMANY),

    ;

    private final Locale locale;
    private final NumberFormat numberFormat;
    private final Currency currency;

    CurrencyCode(Locale locale) {
        this.locale = locale;
        this.numberFormat = NumberFormat.getCurrencyInstance(locale);
        this.currency = Currency.getInstance(locale);
    }

    /**
     * @return {@code Locale} of this currency code
     */
    public Locale getLocale() {
        return this.locale;
    }

    /**
     * @return {@code NumberFormat} of this currency code
     */
    public NumberFormat getNumberFormat() {
        return this.numberFormat;
    }

    /**
     * @return {@code Currency} of this currency code
     */
    public Currency getCurrency() {
        return this.currency;
    }

}