package com.sparklab.TAM.contollers;

import com.sparklab.TAM.dto.apartment.ApartmentLinkedApartmentOption;
import com.sparklab.TAM.exceptions.ApiCallError;
import com.sparklab.TAM.services.ApartmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("TAM/{userId}/apartments")
public class ApartmentController {
    private final ApartmentService apartmentService;

    @GetMapping
    public ResponseEntity<?> getAllApartments(@PathVariable String userId) {
        try {
            return new ResponseEntity<>(apartmentService.getAllApartments(userId), HttpStatus.OK);
        } catch (ApiCallError e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping("{id}")
    public ResponseEntity<?> getApartmentById(@PathVariable String userId,@PathVariable int id){
        try {
            return new ResponseEntity<>(apartmentService.getApartmentById(userId,id),HttpStatus.OK );
        }catch (ApiCallError e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("getallApartmentOptions/{apartmentId}")
    public ResponseEntity<?> getAllApartmentsOptions(@PathVariable String apartmentId) {
        return new ResponseEntity<>(apartmentService.getAllApartmentOptions(apartmentId), HttpStatus.OK);
    }

    @PostMapping("apartmentOption/saveOrUpdate")
    public ResponseEntity<?> saveApartmentOptions(@RequestBody ApartmentLinkedApartmentOption apartmentOption, @PathVariable String userId) {
        return new ResponseEntity<>(apartmentService.saveApartmentOption(apartmentOption,userId), HttpStatus.OK);
    }


    @DeleteMapping("/deleteApartmentOption/delete")
    public ResponseEntity<?> deleteApartmentOptions(@RequestBody ApartmentLinkedApartmentOption apartmentLinkedApartmentOption) {
        return new ResponseEntity<>(apartmentService.deleteApartmentOption(apartmentLinkedApartmentOption), HttpStatus.OK);
    }



}
