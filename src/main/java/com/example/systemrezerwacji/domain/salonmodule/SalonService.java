package com.example.systemrezerwacji.domain.salonmodule;

import com.example.systemrezerwacji.domain.salonmodule.dto.CreateNewSalonDto;
import com.example.systemrezerwacji.domain.salonmodule.dto.ImageDto;
import com.example.systemrezerwacji.domain.salonmodule.dto.SalonWithIdDto;
import com.example.systemrezerwacji.domain.salonmodule.exception.SalonNotFoundException;
import com.example.systemrezerwacji.domain.usermodule.User;
import com.example.systemrezerwacji.domain.usermodule.exception.InvalidOwnerException;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
class SalonService {
    private static final Logger log = LogManager.getLogger(SalonService.class);
    private final SalonRepository salonRepository;
    private final MaperSalonToSalonWithIdDto mapper;
    private final ImageRepository imageRepository;

    SalonService(SalonRepository salonRepository, MaperSalonToSalonWithIdDto mapper, ImageRepository imageRepository) {
        this.salonRepository = salonRepository;
        this.mapper = mapper;
        this.imageRepository = imageRepository;
        log.info("SalonService initialized");
    }

    Long createNewSalon(CreateNewSalonDto salonDto, Optional<User> user) {
        log.info("Creating new salon: {} in city: {}", salonDto.salonName(), salonDto.city());
        Salon salon = new Salon.SalonBuilder()
                .addName(salonDto.salonName())
                .addCity(salonDto.city())
                .addCategory(salonDto.category())
                .addZipCode(salonDto.zipCode())
                .addStreet(salonDto.street())
                .addNumber(salonDto.number())
                .addUser(user)
                .build();
        salonRepository.save(salon);
        log.info("Salon created with id: {}", salon.getId());
        return salon.getId();
    }

    List<SalonWithIdDto> getAllSalons() {
        log.debug("Fetching all salons");
        Iterable<Salon> allSalon = salonRepository.findAll();
        return StreamSupport.stream(allSalon.spliterator(), false)
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    Optional<SalonWithIdDto> getSalonById(Long id) {
        log.debug("Fetching salon by id: {}", id);
        Optional<Salon> optionalSalon = salonRepository.findById(id);
        if (optionalSalon.isPresent()) {
            log.info("Salon found with id: {}", id);
        } else {
            log.warn("Salon not found with id: {}", id);
        }
        return optionalSalon.map(mapper::map);
    }

    Salon getSalon(Long id) {
        log.debug("Getting salon entity by id: {}", id);
        return salonRepository.findById(id)
                .orElseThrow(() -> {
            log.error("Salon with id: {} not found", id);
            return new SalonNotFoundException("Salon with id: " + id + " not found");
        });
    }



    public Salon addImageToSalon(Long salonId, Image image) {
        log.info("Adding image to salon with id: {}", salonId);
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> {
                    log.error("Salon not found with id: {}", salonId);
                    return new RuntimeException("Salon not found");
                });
        image.setSalon(salon);
        salon.getImages().add(image);
        return salonRepository.save(salon);
    }

    public List<ImageDto> findImagesBySalonId(Long salonId) {
        log.debug("Finding images for salon id: {}", salonId);
        List<Image> images = imageRepository.findBySalonId(salonId);
        log.info("Found {} images for salon id: {}", images.size(), salonId);
        return images.stream()
                .map(image -> new ImageDto(image.getId(), image.getName(), image.getImageUrl(), image.getImageId(), image.getSalon().getId()))
                .collect(Collectors.toList());
    }


    public List<SalonWithIdDto> getAllSalons(User user) {
        log.debug("Fetching all salons for user id: {}", user.getId());
        List<Salon> salonsByUser = salonRepository.getSalonsByUser(user);
        log.info("Found {} salons for user id: {}", salonsByUser.size(), user.getId());
        return salonsByUser.stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    public SalonWithIdDto getSalonByIdAndCheckOwner(Long salonId, User user) {
        log.info("Checking ownership for salon id: {} and user id: {}", salonId, user.getId());
        Optional<Salon> optionalSalon = salonRepository.findById(salonId);

        if (optionalSalon.isPresent()) {
            Salon salon = optionalSalon.get();
            User owner = salon.getUser();
            if (owner.getId().equals(user.getId())) {
                return mapper.map(salon);
            } else {
                throw new InvalidOwnerException("This user is not the owner of this salon!");
            }
        } else {
            throw new SalonNotFoundException("Salon with this Id not exist");
        }
    }
}
