package app.entities;

import app.dtos.GuideDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "guide")
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "experience", nullable = false)
    private int yearsOfExperience;

    @ToString.Exclude
    @JsonManagedReference
    @OneToMany(mappedBy = "guide", cascade = CascadeType.PERSIST)
    private List<Trip> trips = new ArrayList<>();

    // Conversion constructor from DTO to entity
    public Guide(GuideDTO dto) {
        if (dto.getId() != null) {
            this.id = dto.getId();
        }
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.email = dto.getEmail();
        this.phone = dto.getPhone();
        this.yearsOfExperience = dto.getYearsOfExperience();
    }

    public Guide(String firstName, String lastName, String email, String phone, int yearsOfExperience) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.yearsOfExperience = yearsOfExperience;
    }

    //    // Helper method to add a trip
//    public void addTrip(Trip trip) {
//        trips.add(trip);
//        trip.setGuide(this);
//    }
//
//    // Helper method to remove a trip
//    public void removeTrip(Trip trip) {
//        trips.remove(trip);
//        trip.setGuide(null);
//    }

}
