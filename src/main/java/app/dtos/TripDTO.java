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
//    private PackingItemDTO packingItems;

    public TripDTO(LocalTime startTime, LocalTime endTime, String longitude, String latitude, String name, double price, Category category) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    // insert guide
    public TripDTO(LocalTime startTime, LocalTime endTime, String longitude, String latitude, String name, double price, Category category, GuideDTO guide) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.price = price;
        this.category = category;
        this.guide = guide;
    }

    // Conversion constructor from entity to DTO
    public TripDTO(Trip trip) {
        this.id = trip.getId();
        this.startTime = trip.getStartTime();
        this.endTime = trip.getEndTime();
        this.longitude = trip.getLongitude();
        this.latitude = trip.getLatitude();
        this.name = trip.getName();
        this.price = trip.getPrice();
        this.category = trip.getCategory();
//        this.guide = (trip.getGuide() != null) ? new GuideDTO(trip.getGuide()) : null;
    }

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
