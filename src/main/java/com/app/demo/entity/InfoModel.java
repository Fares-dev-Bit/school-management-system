package com.app.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Past;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.Period;

@Data
@Entity
public class InfoModel { 
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;
     @CreationTimestamp
     @Column(updatable = false, name = "register_date")
     private LocalDate registerDate;
     @Past(message = "Birth date must be in the past")
     private LocalDate birthDate;
     private LocalDate  codeDate;
     private LocalDate   creneauDate;
     private LocalDate   circulationDate;

     @JsonIgnore
     @AssertTrue(message = "Candidate must be at least 18 years old")
     public boolean isAdult() {
         if (birthDate == null) return true;
         return Period.between(birthDate, LocalDate.now()).getYears() >= 18;
     }
}

