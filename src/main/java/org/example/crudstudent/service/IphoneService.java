package org.example.crudstudent.service;

import lombok.AllArgsConstructor;
import org.example.crudstudent.dto.IphoneRequest;
import org.example.crudstudent.dto.IphoneResponse;
import org.example.crudstudent.exception.ResourceNotFoundException;
import org.example.crudstudent.mapper.IphoneMapper;
import org.example.crudstudent.repository.IphoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class IphoneService {
    private final IphoneRepository iphoneRepository;
    private final IphoneMapper iphoneMapper;

    public List<IphoneResponse> getPhones(){
        return iphoneRepository.findAll()
                .stream()
                .map(iphoneMapper::toDto)
                .toList();
    }

    public IphoneResponse getPhone(Long id){
        var iphone = iphoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IPhone not found with id: " + id));
        return iphoneMapper.toDto(iphone);
    }

    public IphoneResponse createPhone(IphoneRequest request){
        return iphoneMapper.toDto(iphoneRepository.save(iphoneMapper.toEntity(request)));
    }

    public IphoneResponse updatePhone(IphoneRequest request, Long id){
        var iphone = iphoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IPhone not found with id: " + id));

        iphoneMapper.update(request, iphone);
        return iphoneMapper.toDto(iphoneRepository.save(iphone));
    }

    public void deletePhone(Long id){
        var iphone = iphoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IPhone not found with id: " + id));
        iphoneRepository.delete(iphone);
    }

}
