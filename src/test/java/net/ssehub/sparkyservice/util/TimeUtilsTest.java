package net.ssehub.sparkyservice.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import net.ssehub.sparkyservice.util.TimeUtils.Term;

public class TimeUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "2020-04-01", // first day of summer term
        "2020-09-30", // last day of summer term
        "2020-05-25",
        "2020-07-01",
        "2020-09-25",
        "2020-04-30",
        "2020-06-15",
        
        "2021-04-01", // first day of summer term
        "2025-09-30", // last day of summer term
        "2022-05-25",
        "2061-07-01",
        "1973-09-25",
        "3429-04-30",
        "2452-06-15",
    })
    public void summerTermDetection(String date) {
        assertEquals(Term.SUMMER, TimeUtils.getTerm(LocalDate.parse(date)));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "2020-10-01", // first day of winter
        "2020-03-31", // last day of winter term
        "2020-11-25",
        "2020-01-01",
        "2020-03-25",
        "2020-10-30",
        "2020-12-15",
        
        "2021-10-01", // first day of winter
        "2025-03-31", // last day of winter term
        "2022-11-25",
        "2061-01-01",
        "1973-03-25",
        "3429-10-30",
        "2452-12-15",
    })
    public void winterTermDetection(String date) {
        assertEquals(Term.WINTER, TimeUtils.getTerm(LocalDate.parse(date)));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
            "2020-04-01", // first day of summer term
            "2020-09-30", // last day of summer term
            "2020-05-25",
            "2020-07-01",
            "2020-09-25",
            "2020-04-30",
            "2020-06-15",
    })
    public void lastDayOfSummerTerm2020(String date) {
        assertEquals(LocalDate.of(2020, 9, 30), TimeUtils.lastDayOfTerm(LocalDate.parse(date)));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
            "2021-03-31", // last day of winter term
            "2021-01-01",
            "2021-03-25",
            "2021-02-15",
    })
    public void lastDayOfWinterTerm2020SameYear(String date) {
        assertEquals(LocalDate.of(2021, 3, 31), TimeUtils.lastDayOfTerm(LocalDate.parse(date)));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
            "2020-10-01", // first day of winter
            "2020-11-25",
            "2020-10-30",
            "2020-12-15",
    })
    public void lastDayOfWinterTerm2020PreviousYear(String date) {
        assertEquals(LocalDate.of(2021, 3, 31), TimeUtils.lastDayOfTerm(LocalDate.parse(date)));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
            "2023-04-01", // first day of summer term
            "2023-09-30", // last day of summer term
            "2023-05-25",
            "2023-07-01",
            "2023-09-25",
            "2023-04-30",
            "2023-06-15",
    })
    public void lastDayOfSummerTerm2023(String date) {
        assertEquals(LocalDate.of(2023, 9, 30), TimeUtils.lastDayOfTerm(LocalDate.parse(date)));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
            "2024-03-31", // last day of winter term
            "2024-01-01",
            "2024-03-25",
            "2024-02-15",
    })
    public void lastDayOfWinterTerm2023SameYear(String date) {
        assertEquals(LocalDate.of(2024, 3, 31), TimeUtils.lastDayOfTerm(LocalDate.parse(date)));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
            "2023-10-01", // first day of winter
            "2023-11-25",
            "2023-10-30",
            "2023-12-15",
    })
    public void lastDayOfWinterTerm2023PreviousYear(String date) {
        assertEquals(LocalDate.of(2024, 3, 31), TimeUtils.lastDayOfTerm(LocalDate.parse(date)));
    }
    
}
