package com.scitequest.martin.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import org.junit.Test;

public class GuiUtilsTest {

    @Test
    public void testLocalDate() {
        ZonedDateTime datetime = GuiUtils.parseDateTime("2022-11-06");
        assertEquals(ZonedDateTime.of(2022, 11, 6, 0, 0, 0, 0, ZoneId.systemDefault()), datetime);
    }

    @Test
    public void testDateWithOffsets() {
        assertThrows(DateTimeParseException.class, () -> {
            GuiUtils.parseDateTime("2022-11-06-10:00");
        });
        assertThrows(DateTimeParseException.class, () -> {
            GuiUtils.parseDateTime("2022-11-06-10:00[Pacific/Honolulu]");
        });
    }

    @Test
    public void testLocalTimeSeconds() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime datetime = GuiUtils.parseDateTime("16:30:15");
        assertEquals(16, datetime.getHour());
        assertEquals(30, datetime.getMinute());
        assertEquals(15, datetime.getSecond());
        assertEquals(now.toLocalDate(), datetime.toLocalDate());
        assertEquals(now.getZone(), datetime.getZone());
    }

    @Test
    public void testLocalTimeMinutes() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime datetime = GuiUtils.parseDateTime("15:30");

        assertEquals(15, datetime.getHour());
        assertEquals(30, datetime.getMinute());
        assertEquals(0, datetime.getSecond());
        assertEquals(now.toLocalDate(), datetime.toLocalDate());
        assertEquals(now.getZone(), datetime.getZone());
    }

    @Test
    public void testLocalTimeWithOffsets() {
        assertThrows(DateTimeParseException.class, () -> {
            GuiUtils.parseDateTime("15:30-10:00");
        });
        assertThrows(DateTimeParseException.class, () -> {
            GuiUtils.parseDateTime("15:30-10:00[Pacific/Honolulu]");
        });
    }

    @Test
    public void testLocalDateTime() {
        ZonedDateTime datetime;

        datetime = GuiUtils.parseDateTime("2022-11-06T15:30");
        assertEquals(
                ZonedDateTime.of(2022, 11, 6, 15, 30, 0, 0, ZoneId.systemDefault()),
                datetime);

        datetime = GuiUtils.parseDateTime("2022-11-06T15:30:15");
        assertEquals(
                ZonedDateTime.of(2022, 11, 6, 15, 30, 15, 0, ZoneId.systemDefault()),
                datetime);
    }

    @Test
    public void testInstant() {
        ZonedDateTime datetime;
        ZonedDateTime expected;

        datetime = GuiUtils.parseDateTime("2022-11-06T15:30Z");
        expected = OffsetDateTime.of(LocalDateTime.of(2022, 11, 6, 15, 30), ZoneOffset.UTC)
                .atZoneSameInstant(ZoneId.systemDefault());
        assertEquals(expected, datetime);

        datetime = GuiUtils.parseDateTime("2022-11-06T15:30:15Z");
        expected = OffsetDateTime.of(LocalDateTime.of(2022, 11, 6, 15, 30, 15), ZoneOffset.UTC)
                .atZoneSameInstant(ZoneId.systemDefault());
        assertEquals(expected, datetime);
    }

    @Test
    public void testDateTimeWithOffsets() {
        ZonedDateTime datetime;
        ZonedDateTime expected;

        datetime = GuiUtils.parseDateTime("2022-11-06T15:30-10:00");
        expected = OffsetDateTime.of(LocalDateTime.of(2022, 11, 6, 15, 30), ZoneOffset.ofHours(-10))
                .atZoneSameInstant(ZoneId.systemDefault());
        assertEquals(expected, datetime);

        datetime = GuiUtils.parseDateTime("2022-11-06T15:30+01:00[Europe/Paris]");
        expected = OffsetDateTime.of(LocalDateTime.of(2022, 11, 6, 15, 30), ZoneOffset.ofHours(1))
                .atZoneSameInstant(ZoneId.of("Europe/Paris"));
        assertEquals(expected, datetime);

        datetime = GuiUtils.parseDateTime("2022-11-06T15:30+11:00[Asia/Tokyo]");
        expected = OffsetDateTime.of(LocalDateTime.of(2022, 11, 6, 13, 30), ZoneOffset.ofHours(9))
                .atZoneSameInstant(ZoneId.of("Asia/Tokyo"));
        assertEquals(expected, datetime);
    }
}
