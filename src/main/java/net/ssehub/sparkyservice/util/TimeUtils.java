package net.ssehub.sparkyservice.util;

import java.time.LocalDate;
import java.time.Month;

public class TimeUtils {

    /**
     * No instances.
     */
    private TimeUtils() {
    }
    
    public enum Term {
        SUMMER, WINTER;
    }
    
    public static Term getTerm(LocalDate date) {
        Term term;
        if (date.getMonth().compareTo(Month.APRIL) >= 0 && date.getMonth().compareTo(Month.SEPTEMBER) <= 0) {
            term = Term.SUMMER;
        } else {
            term = Term.WINTER;
        }
        return term;
    }
    
    public static LocalDate lastDayOfTerm(LocalDate date) {
        LocalDate result;
        
        Term term = getTerm(date);
        switch (term) {
        case SUMMER:
            result = LocalDate.of(date.getYear(), Month.SEPTEMBER, Month.SEPTEMBER.length(date.isLeapYear()));
            break;
        
        case WINTER:
            int year = date.getYear();
            // check if end of semester will be next year
            if (date.getMonth().compareTo(Month.OCTOBER) >= 0) {
                year++;
            }
            result = LocalDate.of(year, Month.MARCH, Month.MARCH.length(LocalDate.of(year, 1, 1).isLeapYear()));
            break;
            
        default:
            throw new AssertionError("Invalid term: " + term);
        }
        
        return result;
    }
    
    public static LocalDate convert(org.threeten.bp.LocalDate date) {
        return LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }
    
    public static org.threeten.bp.LocalDate convert(LocalDate date) {
        return org.threeten.bp.LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }
    
}
