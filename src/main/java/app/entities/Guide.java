package app.entities;

import app.dtos.GuideDTO;
import app.dtos.TripDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "guide", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Trip> trips = new ArrayList<>();

    public GuideDTO toDTO() {
        return new GuideDTO(this.id, this.firstName, this.lastName, this.email, this.phone, this.yearsOfExperience);
    }

    public static Guide fromDTO(GuideDTO guideDTO) {
        Guide guide = new Guide();
        guide.setId(guideDTO.getId());
        guide.setFirstName(guideDTO.getFirstName());
        guide.setLastName(guideDTO.getLastName());
        guide.setEmail(guideDTO.getEmail());
        guide.setPhone(guideDTO.getPhone());
        guide.setYearsOfExperience(guideDTO.getYearsOfExperience());
        return guide;
    }

}
