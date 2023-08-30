package com.choi.doit.global.util;

import com.choi.doit.global.error.GlobalErrorCode;
import com.choi.doit.global.error.exception.RestApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
@Slf4j
public class DatetimeUtil {
    public LocalDate parseDate(String str) throws RestApiException {
        String[] date = str.split("-");

        try {
            int year = date[0].length() == 4 ? Integer.parseInt(date[0]) : Integer.parseInt(20 + date[0]);
            int month = Integer.parseInt(date[1]);
            int day = Integer.parseInt(date[2]);

            return LocalDate.of(year, month, day);
        } catch (NumberFormatException | DateTimeException e) {
            log.error(e.getMessage());

            throw new RestApiException(GlobalErrorCode.INVALID_DATE_FORMAT);
        }
    }

    public LocalTime parseTime(String str) throws RestApiException {
        String[] time = str.split(":");

        try {
            int hour = Integer.parseInt(time[0]);
            int minute = Integer.parseInt(time[1]);

            return LocalTime.of(hour, minute);
        } catch (NumberFormatException | DateTimeException e) {
            log.error(e.getMessage());

            throw new RestApiException(GlobalErrorCode.INVALID_TIME_FORMAT);
        }
    }
}
