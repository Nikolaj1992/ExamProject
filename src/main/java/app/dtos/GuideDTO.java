package app.dtos;

import app.entities.Guide;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
//    private List<TripDTO> trips = new ArrayList<>();      // maybe add again later if needed, for now leave out

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
