package app.dtos;

import app.entities.Guide;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GuideDTO {

    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private int yearsOfExperience;
    private List<TripDTO> trips = new ArrayList<>();

    public GuideDTO(String firstName, String lastName, String email, String phone, int yearsOfExperience) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.yearsOfExperience = yearsOfExperience;
    }

    public GuideDTO(Integer id, String firstName, String lastName, String email, String phone, int yearsOfExperience) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.yearsOfExperience = yearsOfExperience;
    }

    public GuideDTO(Guide guide) {
        this.id = guide.getId();
        this.firstName = guide.getFirstName();
        this.lastName = guide.getLastName();
        this.email = guide.getEmail();
        this.phone = guide.getPhone();
        this.yearsOfExperience = guide.getYearsOfExperience();
        this.trips = guide.getTrips().stream()
                .map(TripDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof GuideDTO guideDto)) return false;

        return getId().equals(guideDto.getId());
    }

    @Override
    public int hashCode()
    {
        return getId().hashCode();
    }

}
