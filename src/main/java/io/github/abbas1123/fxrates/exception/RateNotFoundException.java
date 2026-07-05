package io.github.abbas1123.fxrates.exception;

public class RateNotFoundException extends RuntimeException {

    public RateNotFoundException(String code) {
        super("No CBAR rate published for currency: " + code);
    }
}
