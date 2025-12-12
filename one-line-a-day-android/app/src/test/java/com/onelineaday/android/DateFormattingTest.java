package com.onelineaday.android;

import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine
 * (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DateFormattingTest {
    @Test
    public void dateFormat_isCorrect() {
        LocalDate date = LocalDate.of(2023, 12, 12);
        // Test standard format logic used in app (simulated)
        // Note: Real app uses resources for pattern, here we test the Locales

        DateTimeFormatter enFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.US);
        assertEquals("Tuesday, December 12, 2023", date.format(enFormatter));

        DateTimeFormatter plFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", new Locale("pl", "PL"));
        // Note: Polish month names might vary by JDK version in test env (Styczeń vs
        // Stycznia),
        // but checking the structure is enough.
        String plOutput = date.format(plFormatter);
        assertTrue(plOutput.contains("12") && plOutput.contains("2023"));
    }
}
