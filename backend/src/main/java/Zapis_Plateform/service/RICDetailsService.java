package Zapis_Plateform.service;

import Zapis_Plateform.entity.RICDetails;
import Zapis_Plateform.repository.RICDetailsRepository;
import Zapis_Plateform.entity.APO_Dashboard;
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
public class RICDetailsService {

    @Autowired
    private RICDetailsRepository ricDetailsRepository;

    @Autowired
    private APORepository apoRepository; // Add dependency for APO_Dashboard

    public List<String> saveRICDetailsFromExcel(MultipartFile file, String username) throws IOException {
        List<RICDetails> ricDetailsList = new ArrayList<>();
        List<String> duplicateEntries = new ArrayList<>();

        // Fetch the APO_Dashboard for the logged-in user
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
            RICDetails ricDetails = RICDetails.builder()
                    .roomNo(getCellValueAsString(row.getCell(0))) // Room No.
                    .ipAddress(getCellValueAsString(row.getCell(1))) // IP Address
                    .classCode(getCellValueAsString(row.getCell(2))) // Class Code
                    .username(username) // Set username from the logged-in APO
                    .apoUser(apoUser) // Automatically set the APO_Dashboard
                    .build();

            boolean existsByRoomNo = ricDetailsRepository.existsByRoomNoAndUsername(ricDetails.getRoomNo(), username);
            boolean existsByIpAddress = ricDetailsRepository.existsByIpAddressAndUsername(ricDetails.getIpAddress(), username);
            boolean existsByClassCode = ricDetailsRepository.existsByClassCodeAndUsername(ricDetails.getClassCode(), username);

            if (existsByRoomNo || existsByIpAddress || existsByClassCode) {
                String message = "Duplicate entry found: ";
                if (existsByRoomNo) message += "Room No (" + ricDetails.getRoomNo() + ") ";
                if (existsByIpAddress) message += "IP Address (" + ricDetails.getIpAddress() + ") ";
                if (existsByClassCode) message += "Class Code (" + ricDetails.getClassCode() + ") ";
                duplicateEntries.add(message + "already exists.");
            } else {
                ricDetailsList.add(ricDetails);
            }
        }

        workbook.close();
        ricDetailsRepository.saveAll(ricDetailsList);
        return duplicateEntries;
    }

    public List<RICDetails> getRICDetailsByUsername(String username) {
        return ricDetailsRepository.findByUsername(username);
    }

    public RICDetails updateRICDetails(Long id, RICDetails updatedDetails, String username) {
        RICDetails existingDetails = ricDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RIC detail not found!"));

        if (!existingDetails.getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to update this RIC detail.");
        }

        if (updatedDetails.getRoomNo() != null) existingDetails.setRoomNo(updatedDetails.getRoomNo());
        if (updatedDetails.getIpAddress() != null) existingDetails.setIpAddress(updatedDetails.getIpAddress());
        if (updatedDetails.getClassCode() != null) existingDetails.setClassCode(updatedDetails.getClassCode());

        return ricDetailsRepository.save(existingDetails);
    }

    public void deleteRICDetails(Long id, String username) {
        RICDetails existingDetails = ricDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RIC detail not found!"));

        if (!existingDetails.getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to delete this RIC detail.");
        }

        ricDetailsRepository.delete(existingDetails);
    }

    // New method to fetch classCode by IP address and APO username
    public Optional<String> getClassCodeByIpAddressAndUsername(String ipAddress, String username) {
        return ricDetailsRepository.findByUsername(username)
                .stream()
                .filter(ric -> ric.getIpAddress().equals(ipAddress))
                .map(RICDetails::getClassCode)
                .findFirst();
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