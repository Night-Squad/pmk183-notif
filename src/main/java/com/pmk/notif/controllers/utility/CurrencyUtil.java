package com.pmk.notif.controllers.utility;

import java.math.BigDecimal;

/**
 * A class which define a {@code CurrencyCode} to be used as the utility's operation target.
 * @author s53kr_lm8a6r
 */
public class CurrencyUtil {

    public static CurrencyLogicProvider of(CurrencyCode code) {
        return new CurrencyLogicProvider(code);
    }

    /**
     * A class which provide logic block for currency operation.
     */
    public static final class CurrencyLogicProvider {

        private final CurrencyCode code;

        private CurrencyLogicProvider(CurrencyCode code){
            this.code=code;
        }

        public String getCurrencySymbol() {
            return code.getCurrency().getSymbol();
        }

        public String getCurrencyCode() {
            return code.getCurrency().getCurrencyCode();
        }

        public ValuedCurrencyLogicProvider withValue(Double value) {
            return new ValuedCurrencyLogicProvider(value);
        }

        public ValuedCurrencyLogicProvider withValue(BigDecimal value) {
            return new ValuedCurrencyLogicProvider(value);
        }

        /**
         * A class which provide logic block for currency operation with assigned value.
         */
        public final class ValuedCurrencyLogicProvider {

            private final BigDecimal value;

            private ValuedCurrencyLogicProvider(BigDecimal value) {
                this.value=value;
            }

            private ValuedCurrencyLogicProvider(Double value) {
                this.value = new BigDecimal(value);
            }

            public String asCurrencyString() {
                return code.getNumberFormat().format(value);
            }

        }

    }
    
}