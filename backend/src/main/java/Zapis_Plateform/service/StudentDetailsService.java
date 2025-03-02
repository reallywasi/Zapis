package Zapis_Plateform.service;

import Zapis_Plateform.entity.StudentDetails;
import Zapis_Plateform.entity.APO_Dashboard;
import Zapis_Plateform.repository.StudentDetailsRepository;
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
public class StudentDetailsService {

    @Autowired
    private StudentDetailsRepository studentDetailsRepository;

    @Autowired
    private APORepository apoRepository; // Add this to fetch the APO user

    public List<String> saveStudentDetailsFromExcel(MultipartFile file, String username) throws IOException {
        List<StudentDetails> studentDetailsList = new ArrayList<>();
        List<String> duplicateEntries = new ArrayList<>();
    
        APO_Dashboard apoUser = apoRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("APO user not found!"));
    
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
    
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }
    
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            StudentDetails studentDetails = new StudentDetails();
    
            studentDetails.setName(getCellValueAsString(row.getCell(0)));
            studentDetails.setSap(getCellValueAsString(row.getCell(1)));
            studentDetails.setRegistrationId(getCellValueAsString(row.getCell(2)));
            studentDetails.setCollegeEmail(getCellValueAsString(row.getCell(3)));
            studentDetails.setCourse(getCellValueAsString(row.getCell(4)));
            studentDetails.setDepartment(getCellValueAsString(row.getCell(5)));
            studentDetails.setSemester(getCellValueAsString(row.getCell(6)));
            studentDetails.setBatch(getCellValueAsString(row.getCell(7)));
    
            studentDetails.setUsername(username);
            studentDetails.setApoUser(apoUser);
    
            // Check for duplicates
            boolean existsByGmail = studentDetailsRepository.existsByCollegeEmail(studentDetails.getCollegeEmail());
            boolean existsBySap = studentDetailsRepository.existsBySap(studentDetails.getSap());
            boolean existsByRegistrationId = studentDetailsRepository.existsByRegistrationIdAndUsername(studentDetails.getRegistrationId(), username);
    
            if (existsByGmail || existsBySap || existsByRegistrationId) {
                String message = "Duplicate entry found: ";
                if (existsByGmail) {
                    message += "Gmail (" + studentDetails.getCollegeEmail() + ") ";
                }
                if (existsBySap) {
                    message += "SAP (" + studentDetails.getSap() + ") ";
                }
                if (existsByRegistrationId) {
                    message += "Registration ID (" + studentDetails.getRegistrationId() + ") ";
                }
                if ((existsByGmail && studentDetailsRepository.existsByCollegeEmailAndUsername(studentDetails.getCollegeEmail(), username)) ||
                    (existsBySap && studentDetailsRepository.existsBySapAndUsername(studentDetails.getSap(), username)) ||
                    existsByRegistrationId) {
                    message += " - This is already uploaded by you.";
                } else {
                    message += " - This already exists in our database.";
                }
                duplicateEntries.add(message);
            } else {
                studentDetailsList.add(studentDetails);
            }
        }
    
        workbook.close();
        studentDetailsRepository.saveAll(studentDetailsList);
        return duplicateEntries;
    }

    public List<StudentDetails> getStudentDetailsByUsername(String username) {
        return studentDetailsRepository.findByUsername(username);
    }

    public StudentDetails updateStudentDetails(Long id, StudentDetails updatedDetails, String username) {
        StudentDetails existingDetails = studentDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found!"));
    
        if (!existingDetails.getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to update this student detail.");
        }
    
        if (updatedDetails.getName() != null) {
            existingDetails.setName(updatedDetails.getName());
        }
        if (updatedDetails.getSap() != null) {
            if (studentDetailsRepository.existsBySap(updatedDetails.getSap()) && 
                !existingDetails.getSap().equals(updatedDetails.getSap())) {
                throw new RuntimeException("SAP (" + updatedDetails.getSap() + ") already exists in the database.");
            }
            existingDetails.setSap(updatedDetails.getSap());
        }
        if (updatedDetails.getRegistrationId() != null) {
            if (studentDetailsRepository.existsByRegistrationIdAndUsername(updatedDetails.getRegistrationId(), username) && 
                !existingDetails.getRegistrationId().equals(updatedDetails.getRegistrationId())) {
                throw new RuntimeException("Registration ID (" + updatedDetails.getRegistrationId() + ") already exists for this user.");
            }
            existingDetails.setRegistrationId(updatedDetails.getRegistrationId());
        }
        if (updatedDetails.getCollegeEmail() != null) {
            if (studentDetailsRepository.existsByCollegeEmail(updatedDetails.getCollegeEmail()) && 
                !existingDetails.getCollegeEmail().equals(updatedDetails.getCollegeEmail())) {
                throw new RuntimeException("Email (" + updatedDetails.getCollegeEmail() + ") already exists in the database.");
            }
            existingDetails.setCollegeEmail(updatedDetails.getCollegeEmail());
        }
        if (updatedDetails.getCourse() != null) {
            existingDetails.setCourse(updatedDetails.getCourse());
        }
        if (updatedDetails.getDepartment() != null) {
            existingDetails.setDepartment(updatedDetails.getDepartment());
        }
        if (updatedDetails.getSemester() != null) {
            existingDetails.setSemester(updatedDetails.getSemester());
        }
        if (updatedDetails.getBatch() != null) {
            existingDetails.setBatch(updatedDetails.getBatch());
        }
    
        return studentDetailsRepository.save(existingDetails);
    }

    public void deleteStudentDetails(Long id, String username) {
        StudentDetails existingDetails = studentDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found!"));

        // Ensure the user is authorized to delete this student detail
        if (!existingDetails.getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to delete this student detail.");
        }

        studentDetailsRepository.delete(existingDetails);
    }

    // Helper method to handle cell types
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return ""; // Return empty string for null cells
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // Convert numeric value to string
                return String.valueOf((int) cell.getNumericCellValue()); // Use (int) for whole numbers
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // Handle formula cells
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

    public Optional<StudentDetails> getStudentByCollegeEmail(String collegeEmail) {
        return studentDetailsRepository.findByCollegeEmail(collegeEmail);
    }
}