package com.app.demo.controllers;

import com.app.demo.entity.Candidate;
import com.app.demo.service.CandidateService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping(path = "/api/v1/candidates")
public class Controllers {
    private final CandidateService services;

    public Controllers(CandidateService services) {
        this.services = services;
    }



    // --- GET METHODS ---

    @GetMapping
    public ResponseEntity<List<Candidate>> getAllCandidates() {
        return ResponseEntity.ok(services.getCandidates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getCandidateById(@PathVariable Long id) {
        return ResponseEntity.ok(services.getCandidate(id));
    }

    // --- POST METHODS ---

    @PostMapping(path="")
    public ResponseEntity<Candidate> addCandidate(@Valid @RequestBody Candidate Candidate) {

        return ResponseEntity.status(HttpStatus.CREATED).body( services.addCandidate(Candidate));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Candidate>> importCandidates(@RequestBody List<@Valid  Candidate> Candidates) {

        return ResponseEntity.status(HttpStatus.CREATED).body(services.addCandidates(Candidates));
    }

    // --- PUT & DELETE METHODS ---

    @PutMapping(path="/{id}")
    public ResponseEntity<Candidate> updateCandidate( @Valid @RequestBody Candidate Candidate , @PathVariable Long id) {
       return ResponseEntity.ok(services.updateCandidate(id ,Candidate));

    }

    @PutMapping(path="/batch")
    public ResponseEntity<List<Candidate>> updateCandidates(@RequestBody List<Candidate> Candidates){

         return  ResponseEntity.ok(services.updateCandidates(Candidates));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCandidate(@PathVariable Long id) {
        services.removeCandidate(id);
        return ResponseEntity.ok("Deleted");
    }
    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteCandidates(@RequestBody List<Long> ids){
        services.removeCandidates(ids);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/search")
    public ResponseEntity<List<Candidate>> searchCandidates(@RequestParam(required = false) String q,
                                                            @RequestParam(required = false )  String dataField,
                                                            @RequestParam(required = false )  String category,
                                                            @RequestParam(required = false )  String stage,
                                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate start,
                                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ){

        return new ResponseEntity<>(services.searchCandidates(q,dataField,category,stage,start,end) , HttpStatus.OK );
        }




    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

@ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,Object>> duplicate(DataIntegrityViolationException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "success" , false ,
                "error" , "candidate already exists"
        ));
}

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(EntityNotFoundException ex){
        HashMap<String , Object> body = new HashMap<>();
        body.put("success" , false );
        body.put("error", ex.getMessage() != null ? ex.getMessage() : "404 Candidate ID not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validationError(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        Map<String, String> details = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                details.put(err.getField(), err.getDefaultMessage()));
        body.put("details", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

}

