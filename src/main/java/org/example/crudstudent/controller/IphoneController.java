package org.example.crudstudent.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.crudstudent.dto.ApiResponse;
import org.example.crudstudent.dto.IphoneRequest;
import org.example.crudstudent.dto.IphoneResponse;
import org.example.crudstudent.service.IphoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/iphone")
@AllArgsConstructor
public class IphoneController extends BaseController{
    private final IphoneService iphoneService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<IphoneResponse>>> getPhones(){
        return ok("Iphone fetched successfully", iphoneService.getPhones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IphoneResponse>> getPhone(@PathVariable Long id){
        return ok("Iphone fetched successfully", iphoneService.getPhone(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<IphoneResponse>> createPhone(
            @Valid @RequestBody IphoneRequest request,
            UriComponentsBuilder builder
    ){
        var iphone = iphoneService.createPhone(request);
        var uri = builder.path("api/iphone/")
                .buildAndExpand(iphone.getId()).toUri();
        return created("Iphone created successfully", iphone , uri);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IphoneResponse>> updatePhone(
            @PathVariable Long id,
            @Valid @RequestBody IphoneRequest request
    ){
        return ok("Iphone updated successfully", iphoneService.updatePhone(request,id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePhone(
            @PathVariable Long id
    ){
        iphoneService.deletePhone(id);
        return ok("Iphone deleted successfully", null);
    }
}
