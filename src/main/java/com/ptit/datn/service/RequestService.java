package com.ptit.datn.service;

import com.ptit.datn.constants.RequestStatus;
import com.ptit.datn.domain.Office;
import com.ptit.datn.domain.Request;
import com.ptit.datn.repository.OfficeRepository;
import com.ptit.datn.repository.RequestRepository;
import com.ptit.datn.service.dto.RequestDTO;
import jakarta.persistence.RollbackException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class RequestService {

    private final RequestRepository requestRepository;
    private final OfficeRepository officeRepository;

    public RequestService(RequestRepository requestRepository,
                          OfficeRepository officeRepository) {
        this.requestRepository = requestRepository;
        this.officeRepository = officeRepository;
    }

    /**
     *
     * @param requestDTO RequestDTO object that contains information of the request
     * @return RequestDTO
     */
    public RequestDTO createRequest(RequestDTO requestDTO) {
        Request request = new Request();
        request.setUserId(requestDTO.getUserId());
        request.setDate(requestDTO.getDate());
        request.setTime(requestDTO.getTime());
        request.setNote(requestDTO.getNote());
        request.setStatus(RequestStatus.PENDING); // When user create a request, the status is PENDING

        // Get all offices by officeIds
        List<Office> officeList = officeRepository.findAllByIds(requestDTO.getOfficeIds());
        if (officeList.size() != requestDTO.getOfficeIds().size()) {
            throw new RollbackException("Some offices are not found");
        }
        officeList.forEach(office -> {
            if (office.getStatus() != 0)
                throw new RollbackException("Office is not available");

            request.getOffices().add(office);
        });
        RequestDTO requestDTO_result = new RequestDTO(requestRepository.save(request));
        requestDTO_result.setOfficeIds(requestDTO.getOfficeIds());

        return requestDTO_result;
    }

    public RequestDTO updateRequest(RequestDTO requestDTO) {
        Request request = requestRepository.findById(requestDTO.getId()).orElseThrow();
        request.setUserId(requestDTO.getUserId());
        // request.setOfficeIds(requestDTO.getOfficeIds());
        request.setDate(requestDTO.getDate());
        request.setTime(requestDTO.getTime());
        request.setNote(requestDTO.getNote());
        request.setStatus(requestDTO.getStatus());
        return new RequestDTO(requestRepository.save(request));
    }
}
