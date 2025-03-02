package Zapis_Plateform.service;

import Zapis_Plateform.entity.FacultyTimeTable;
import Zapis_Plateform.entity.APO_Dashboard;
import Zapis_Plateform.entity.FacultyDetails;
import Zapis_Plateform.repository.FacultyTimeTableRepository;
import Zapis_Plateform.repository.APORepository;
import Zapis_Plateform.repository.FacultyDetailsRepository;
import Zapis_Plateform.utils.DateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class FacultyTimeTableService {

    @Autowired
    private FacultyTimeTableRepository facultyTimeTableRepository;

    @Autowired
    private APORepository apoRepository;

    @Autowired
    private FacultyDetailsRepository facultyDetailsRepository; // Add dependency for FacultyDetails

    @Autowired
    private StudentTimeTableService studentTimeTableService;

    @Transactional
    public List<String> saveFacultyTimeTableFromExcel(MultipartFile file, String username) throws IOException {
        List<FacultyTimeTable> facultyTimeTableList = new ArrayList<>();
        List<String> duplicateEntries = new ArrayList<>();

        APO_Dashboard apoUser = apoRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("APO user not found!"));

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        if (rowIterator.hasNext()) {
            rowIterator.next(); // Skip header row
        }

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            String registrationId = getCellValueAsString(row.getCell(0)); // Registration ID

            // Fetch faculty name from FacultyDetails using registrationId and username
            Optional<FacultyDetails> facultyDetailsOpt = facultyDetailsRepository.findByRegistrationIdAndUsername(registrationId, username);
            String facultyName = facultyDetailsOpt.map(FacultyDetails::getName)
                    .orElseThrow(() -> new RuntimeException("Faculty not found for registrationId: " + registrationId));

            // Get and parse the date from the Excel cell
            Cell dateCell = row.getCell(7); // Date is in column 7 (index 7, 0-based)
            String dateStr = getCellValueAsString(dateCell);
            String formattedDate = formatDate(dateStr); // Format the date using DateUtil

            FacultyTimeTable facultyTimeTable = FacultyTimeTable.builder()
                    .registrationId(registrationId) // Registration ID
                    .course(getCellValueAsString(row.getCell(1))) // Course
                    .specialisation(getCellValueAsString(row.getCell(2))) // Specialisation (optional)
                    .batch(getCellValueAsString(row.getCell(3))) // Batch
                    .semester(getCellValueAsString(row.getCell(4))) // Semester
                    .subject(getCellValueAsString(row.getCell(5))) // Subject
                    .roomNo(getCellValueAsString(row.getCell(6))) // Room No.
                    .date(formattedDate) // Use formatted date
                    .startTimeEndTime(getCellValueAsString(row.getCell(8))) // Start Time - End Time
                    .username(username)
                    .apoUser(apoUser)
                    .facultyName(facultyName) // Automatically set faculty name
                    .build();

            facultyTimeTableList.add(facultyTimeTable);
        }

        workbook.close();
        facultyTimeTableRepository.saveAll(facultyTimeTableList);

        // Generate student timetable after saving faculty timetable
        studentTimeTableService.generateStudentTimeTable(username);

        return duplicateEntries;
    }

    // New method to format the date using DateUtil
    private String formatDate(String dateStr) {
        try {
            return DateUtil.formatLocalDate(DateUtil.excelSerialToLocalDate(dateStr));
        } catch (Exception e) {
            // If the date cannot be parsed as a serial number, assume it's already in a string format (e.g., "YYYY-MM-DD")
            return dateStr; // Return as-is or handle differently if needed
        }
    }

    public List<FacultyTimeTable> getFacultyTimeTableByUsername(String username) {
        return facultyTimeTableRepository.findByUsername(username);
    }

    @Transactional
    public FacultyTimeTable updateFacultyTimeTable(Long id, FacultyTimeTable updatedDetails, String username) {
        FacultyTimeTable existingDetails = facultyTimeTableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty Time Table not found!"));

        if (!existingDetails.getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to update this faculty time table.");
        }

        if (updatedDetails.getCourse() != null) existingDetails.setCourse(updatedDetails.getCourse());
        if (updatedDetails.getSpecialisation() != null) existingDetails.setSpecialisation(updatedDetails.getSpecialisation());
        if (updatedDetails.getBatch() != null) existingDetails.setBatch(updatedDetails.getBatch());
        if (updatedDetails.getSemester() != null) existingDetails.setSemester(updatedDetails.getSemester());
        if (updatedDetails.getSubject() != null) existingDetails.setSubject(updatedDetails.getSubject());
        if (updatedDetails.getRoomNo() != null) existingDetails.setRoomNo(updatedDetails.getRoomNo());
        if (updatedDetails.getDate() != null) existingDetails.setDate(formatDate(updatedDetails.getDate())); // Format the date
        if (updatedDetails.getStartTimeEndTime() != null) existingDetails.setStartTimeEndTime(updatedDetails.getStartTimeEndTime());

        // Re-fetch faculty name if registrationId changes or ensure it remains correct
        if (updatedDetails.getRegistrationId() != null) {
            Optional<FacultyDetails> facultyDetailsOpt = facultyDetailsRepository.findByRegistrationIdAndUsername(updatedDetails.getRegistrationId(), username);
            String facultyName = facultyDetailsOpt.map(FacultyDetails::getName)
                    .orElseThrow(() -> new RuntimeException("Faculty not found for registrationId: " + updatedDetails.getRegistrationId()));
            existingDetails.setFacultyName(facultyName);
        }

        FacultyTimeTable updatedTimeTable = facultyTimeTableRepository.save(existingDetails);

        // Regenerate student timetable after updating faculty timetable
        studentTimeTableService.generateStudentTimeTable(username);

        return updatedTimeTable;
    }

    @Transactional
    public void deleteFacultyTimeTable(Long id, String username) {
        FacultyTimeTable existingDetails = facultyTimeTableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty Time Table not found!"));

        if (!existingDetails.getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to delete this faculty time table.");
        }

        facultyTimeTableRepository.delete(existingDetails);

        // Regenerate student timetable after deleting faculty timetable
        studentTimeTableService.generateStudentTimeTable(username);
    }

    @Transactional
    public void deleteFacultyTimeTableByRegistrationId(String registrationId, String username) {
        facultyTimeTableRepository.deleteByRegistrationIdAndUsername(registrationId, username);

        // Regenerate student timetable after deleting faculty timetable by registration ID
        studentTimeTableService.generateStudentTimeTable(username);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case STRING: return cell.getStringCellValue();
                    case NUMERIC: return String.valueOf((int) cell.getNumericCellValue());
                    case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
                    default: return "";
                }
            default: return "";
        }
    }
}