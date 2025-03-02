package Zapis_Plateform.service;

import Zapis_Plateform.entity.FacultyDetails;
import Zapis_Plateform.entity.APO_Dashboard;
import Zapis_Plateform.repository.FacultyDetailsRepository;
import Zapis_Plateform.repository.APORepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class FacultyDetailsService {

    @Autowired
    private FacultyDetailsRepository facultyDetailsRepository;

    @Autowired
    private APORepository apoRepository;

    public List<String> saveFacultyDetailsFromExcel(MultipartFile file, String username) throws IOException {
        List<FacultyDetails> facultyDetailsList = new ArrayList<>();
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
            FacultyDetails facultyDetails = new FacultyDetails();

            facultyDetails.setName(getCellValueAsString(row.getCell(0))); // Name
            facultyDetails.setSap(getCellValueAsString(row.getCell(1))); // SAP
            facultyDetails.setRegistrationId(getCellValueAsString(row.getCell(2))); // Registration ID
            facultyDetails.setCollegeEmail(getCellValueAsString(row.getCell(3))); // College Email
            facultyDetails.setDepartment(getCellValueAsString(row.getCell(4))); // Department

            facultyDetails.setUsername(username);
            facultyDetails.setApoUser(apoUser);

            // Check for duplicates
            boolean existsByEmail = facultyDetailsRepository.existsByCollegeEmail(facultyDetails.getCollegeEmail());
            boolean existsBySap = facultyDetailsRepository.existsBySap(facultyDetails.getSap());
            boolean existsByRegistrationId = facultyDetailsRepository.existsByRegistrationIdAndUsername(facultyDetails.getRegistrationId(), username);

            if (existsByEmail || existsBySap || existsByRegistrationId) {
                String message = "Duplicate entry found: ";
                if (existsByEmail) {
                    message += "Email (" + facultyDetails.getCollegeEmail() + ") ";
                }
                if (existsBySap) {
                    message += "SAP (" + facultyDetails.getSap() + ") ";
                }
                if (existsByRegistrationId) {
                    message += "Registration ID (" + facultyDetails.getRegistrationId() + ") ";
                }
                if (existsByRegistrationId) {
                    message += " - This is already uploaded by you.";
                } else {
                    message += " - This already exists in our database.";
                }
                duplicateEntries.add(message);
            } else {
                facultyDetailsList.add(facultyDetails);
            }
        }

        workbook.close();
        facultyDetailsRepository.saveAll(facultyDetailsList);
        return duplicateEntries;
    }

    public List<FacultyDetails> getFacultyDetailsByUsername(String username) {
        return facultyDetailsRepository.findByUsername(username);
    }

    public FacultyDetails updateFacultyDetails(Long id, FacultyDetails updatedDetails, String username) {
        FacultyDetails existingDetails = facultyDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty not found!"));

        if (!existingDetails.getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to update this faculty detail.");
        }

        if (updatedDetails.getName() != null) {
            existingDetails.setName(updatedDetails.getName());
        }
        if (updatedDetails.getSap() != null) {
            if (facultyDetailsRepository.existsBySap(updatedDetails.getSap()) && 
                !existingDetails.getSap().equals(updatedDetails.getSap())) {
                throw new RuntimeException("SAP (" + updatedDetails.getSap() + ") already exists in the database.");
            }
            existingDetails.setSap(updatedDetails.getSap());
        }
        if (updatedDetails.getRegistrationId() != null) {
            if (facultyDetailsRepository.existsByRegistrationIdAndUsername(updatedDetails.getRegistrationId(), username) && 
                !existingDetails.getRegistrationId().equals(updatedDetails.getRegistrationId())) {
                throw new RuntimeException("Registration ID (" + updatedDetails.getRegistrationId() + ") already exists for this user.");
            }
            existingDetails.setRegistrationId(updatedDetails.getRegistrationId());
        }
        if (updatedDetails.getCollegeEmail() != null) {
            if (facultyDetailsRepository.existsByCollegeEmail(updatedDetails.getCollegeEmail()) && 
                !existingDetails.getCollegeEmail().equals(updatedDetails.getCollegeEmail())) {
                throw new RuntimeException("Email (" + updatedDetails.getCollegeEmail() + ") already exists in the database.");
            }
            existingDetails.setCollegeEmail(updatedDetails.getCollegeEmail());
        }
        if (updatedDetails.getDepartment() != null) {
            existingDetails.setDepartment(updatedDetails.getDepartment());
        }

        return facultyDetailsRepository.save(existingDetails);
    }

    public void deleteFacultyDetails(Long id, String username) {
        FacultyDetails existingDetails = facultyDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty not found!"));

        if (!existingDetails.getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to delete this faculty detail.");
        }

        facultyDetailsRepository.delete(existingDetails);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case STRING:
                        return cell.getStringCellValue();
                    case NUMERIC:
                        return String.valueOf((int) cell.getNumericCellValue());
                    case BOOLEAN:
                        return String.valueOf(cell.getBooleanCellValue());
                    default:
                        return "";
                }
            default:
                return "";
        }
    }

    public Optional<FacultyDetails> getFacultyDetailsByCollegeEmail(String collegeEmail) {
        return facultyDetailsRepository.findByCollegeEmail(collegeEmail);
    }
}
