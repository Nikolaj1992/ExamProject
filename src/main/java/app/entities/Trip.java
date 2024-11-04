package app.entities;

import app.dtos.TripDTO;
import app.enums.Category;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Column(name = "start_time", nullable = false, unique = true)
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
    @JoinColumn(name = "guide_id", nullable = false)
    private Guide guide;

    public Trip(TripDTO dto) {
        if (dto.getId() != null) {
            this.id = dto.getId();
        }
        this.startTime = dto.getStartTime();
        this.endTime = dto.getEndTime();
        this.longitude = dto.getLongitude();
        this.latitude = dto.getLatitude();
        this.name = dto.getName();
        this.price = dto.getPrice();
        this.category = dto.getCategory();
    }

    public Trip(LocalTime startTime, LocalTime endTime, String longitude, String latitude, String name, double price, Category category) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.price = price;
        this.category = category;
    }
}
