package app.entities;

import app.dtos.GuideDTO;
import app.dtos.TripDTO;
import app.enums.Category;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "trip")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "longitude", nullable = false)
    private String longitude;

    @Column(name = "latitude", nullable = false)
    private String latitude;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Setter
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "guide_id", nullable = true)
    private Guide guide;

    public TripDTO toDTO() {
        GuideDTO guideDTO = this.guide != null ? this.guide.toDTO() : null;
        return new TripDTO(this.id, this.startTime, this.endTime, this.longitude, this.latitude, this.name, this.price, this.category, guideDTO);
    }

    public static Trip fromDTO(TripDTO tripDTO) {
        Trip trip = new Trip();
        trip.setId(tripDTO.getId());
        trip.setStartTime(tripDTO.getStartTime());
        trip.setEndTime(tripDTO.getEndTime());
        trip.setLongitude(tripDTO.getLongitude());
        trip.setLatitude(tripDTO.getLatitude());
        trip.setName(tripDTO.getName());
        trip.setPrice(tripDTO.getPrice());
        trip.setCategory(tripDTO.getCategory());
        trip.setGuide(Guide.fromDTO(tripDTO.getGuide()));
        return trip;
    }

}
