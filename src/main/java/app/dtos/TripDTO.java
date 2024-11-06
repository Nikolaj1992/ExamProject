package app.dtos;

import app.entities.Trip;
import app.enums.Category;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TripDTO {

    private Integer id;
    private LocalTime startTime;
    private LocalTime endTime;
    private String longitude;
    private String latitude;
    private String name;
    private double price;
    private Category category;
    private GuideDTO guide;

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof TripDTO tripDto)) return false;

        return getId().equals(tripDto.getId());
    }

    @Override
    public int hashCode()
    {
        return getId().hashCode();
    }

}
