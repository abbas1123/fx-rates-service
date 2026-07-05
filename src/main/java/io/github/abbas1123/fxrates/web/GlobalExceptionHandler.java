package io.github.abbas1123.fxrates.web;

import io.github.abbas1123.fxrates.exception.RateNotFoundException;
import io.github.abbas1123.fxrates.exception.RatesUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateNotFoundException.class)
    public ProblemDetail handleRateNotFound(RateNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Rate not found");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(RatesUnavailableException.class)
    public ProblemDetail handleRatesUnavailable(RatesUnavailableException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        problem.setTitle("Rates temporarily unavailable");
        problem.setDetail(ex.getMessage());
        return problem;
    }
}
