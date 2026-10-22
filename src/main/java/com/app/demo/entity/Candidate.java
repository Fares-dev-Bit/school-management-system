package com.app.demo.entity;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;


@Data
@NoArgsConstructor
@Entity
@Table(name = "candidates" , uniqueConstraints =
    @UniqueConstraint(columnNames = {"firstName","lastName","phone"})
)
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;


    @NotBlank
    @Pattern(regexp = "[\\p{L} .'-]+", message = "First name must contain only letters, spaces, dots, hyphens and apostrophes")
    @Column( nullable = false)
    private String firstName;

    @Column( nullable = false)
    @NotBlank
    @Pattern(regexp = "[\\p{L} .'-]+", message = "Last name must contain only letters, spaces, dots, hyphens and apostrophes")
    private String lastName ;


    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^0[5-7][0-9]{8}$", message = "Phone must contain exactly 10 digits")
    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private Stage stage; // e.g. 1 = Code, 2 = Creneau, 3 = Conduite 4= COMPLETED

    @Column(nullable = false , name = "category")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Category category ;





    @Valid
    @OneToOne(cascade = CascadeType.ALL , orphanRemoval = true)
    @JoinColumn(name="info_model_id" , nullable = false)
    private InfoModel infoModel;

    @PrePersist
    @PreUpdate
    public void sanitizeInputs() {
        if (this.lastName != null) {
            this.lastName = this.lastName.trim().toLowerCase();
        }
        if (this.firstName != null) {
            this.firstName = this.firstName.trim().toLowerCase();
        }
    }
}