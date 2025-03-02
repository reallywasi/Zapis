package Zapis_Plateform.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final LocalDate EXCEL_EPOCH = LocalDate.of(1899, 12, 30); // Adjusted epoch to account for Excel's quirks

    public static LocalDate excelSerialToLocalDate(String serialDate) {
        try {
            int days = Integer.parseInt(serialDate);
            return EXCEL_EPOCH.plusDays(days); // No additional offset needed now
        } catch (NumberFormatException e) {
            // Handle invalid serial date (e.g., already in YYYY-MM-DD format)
            return LocalDate.parse(serialDate, DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }

    public static String formatLocalDate(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE); // e.g., "2025-02-23"
    }
}