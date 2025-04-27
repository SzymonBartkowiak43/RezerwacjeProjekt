package com.example.systemrezerwacji.infrastructure.restcontrollers;


import com.example.systemrezerwacji.domain.employeemodule.response.CreateEmployeeResponseDto;
import com.example.systemrezerwacji.domain.employeemodule.dto.EmployeeDto;
import com.example.systemrezerwacji.domain.salonmodule.SalonFacade;
import com.example.systemrezerwacji.domain.salonmodule.dto.*;
import com.example.systemrezerwacji.domain.openinghoursmodule.dto.OpeningHoursDto;
import com.example.systemrezerwacji.infrastructure.claudinary.CloudinaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.example.systemrezerwacji.domain.salonmodule.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

@CrossOrigin(origins = "http://164.90.190.165")
@RestController
public class SalonController {
    private final SalonFacade salonFacade;
    private final CloudinaryService cloudinaryService;

    public SalonController(SalonFacade salonFacade, CloudinaryService cloudinaryService) {
        this.salonFacade = salonFacade;
        this.cloudinaryService = cloudinaryService;
    }


    @PostMapping("/salon")
    public ResponseEntity<SalonFacadeResponseDto> createSalon(@RequestBody CreateNewSalonDto salon) {
        SalonFacadeResponseDto newSalon = salonFacade.createNewSalon(salon);

        if(newSalon.salonId() == null) {
            return ResponseEntity.badRequest().body(newSalon);
        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newSalon.salonId())
                .toUri();

        return ResponseEntity.created(location).body(newSalon);
    }

    @PatchMapping("/salon/add-opening-hours")
    public ResponseEntity<SalonFacadeResponseDto> addOpeningHours(@RequestBody List<OpeningHoursDto> openingHoursDto) {
        SalonFacadeResponseDto response = salonFacade.addOpeningHoursToSalon(openingHoursDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/salons")
    public ResponseEntity<List<SalonWithIdDto>> getAllSalons() {
        List<SalonWithIdDto> allSalons = salonFacade.getAllSalons();

        return ResponseEntity.ok(allSalons);
    }

    @GetMapping("/salons/{id}")
    public ResponseEntity<SalonWithIdDto> getSalon(@PathVariable Integer id) {
        return salonFacade.getSalonById(id.longValue())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/salon/{id}/employee")
    public ResponseEntity<CreateEmployeeResponseDto> addEmployeeToSalon(@PathVariable Long id, @RequestBody EmployeeDto employeeDto) {
        EmployeeDto updatedEmployeeDto = new EmployeeDto(id, employeeDto.name(), employeeDto.email(), employeeDto.availability());

        CreateEmployeeResponseDto response = salonFacade.addEmployeeToSalon(updatedEmployeeDto);

        if(response.message().equals("success")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/salons/image/{salonId}")
    @ResponseBody
    public ResponseEntity<String> uploadImage(
            @PathVariable Integer salonId,
            @RequestParam MultipartFile multipartFile) throws IOException {

        BufferedImage bi = ImageIO.read(multipartFile.getInputStream());
        if (bi == null) {
            return new ResponseEntity<>("Image not validate!", HttpStatus.BAD_REQUEST);
        }

        Map result = cloudinaryService.upload(multipartFile);
        Image image = new Image((String) result.get("original_filename"),
                (String) result.get("url"),
                (String) result.get("public_id"));

        salonFacade.addImageToSalon(Long.valueOf(salonId),image);
        return new ResponseEntity<>("Uploading successful", HttpStatus.OK);
    }

    @GetMapping("/salons/image/{salonId}")
    public ResponseEntity<List<ImageDto>> getImagesForSalon(@PathVariable Long salonId) {
        List<ImageDto> images = salonFacade.findImagesBySalonId(salonId);
        if (images.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(images, HttpStatus.OK);
    }

}
