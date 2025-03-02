package Zapis_Plateform.dto;

import lombok.Data;

import java.util.List;

@Data
public class AttendanceSummary {
    private int totalClasses;
    private int presentDays;
    private double attendancePercentage;
    private List<AttendanceRecord> records;

    @Data
    public static class AttendanceRecord {
        private String date;
        private String subject;
        private String startTimeEndTime;
        private boolean present;
    }
}