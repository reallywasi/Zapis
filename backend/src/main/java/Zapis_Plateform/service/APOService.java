package Zapis_Plateform.service;

import Zapis_Plateform.dto.APORequest;
import Zapis_Plateform.entity.APO_Dashboard;
import Zapis_Plateform.repository.APORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class APOService {

    @Autowired
    private APORepository apoRepository;

    public APO_Dashboard saveAPOData(APORequest request) {
        APO_Dashboard apoData = APO_Dashboard.builder()
                .username(request.getUsername())
                .data(request.getData())
                .build();
        return apoRepository.save(apoData);
    }

    public Optional<APO_Dashboard> getAPODataByUsername(String username) {
        return apoRepository.findByUsername(username);
    }
}