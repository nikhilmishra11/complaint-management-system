package com.example.cms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Date {

    private LocalDate value;

    public Date() {
        this.value = LocalDate.now();
    }

    public Date(LocalDate value) {
        this.value = value;
    }

    public LocalDate getValue() {
        return value;
    }

    public void setValue(LocalDate value) {
        this.value = value;
    }

    public int getDay() {
        return value.getDayOfMonth();
    }

    public int getMonth() {
        return value.getMonthValue();
    }

    public int getYear() {
        return value.getYear();
    }

    public String getFormattedDate() {
        return value.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static LocalDate today() {
        return LocalDate.now();
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
