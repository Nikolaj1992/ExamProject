package app.config;

import app.dtos.GuideDTO;
import app.dtos.TripDTO;
import app.entities.Guide;
import app.entities.Trip;
import app.enums.Category;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Populator {
    public static void main(String[] args) {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        populate(emf);
        emf.close();

    }

    public static void populate(EntityManagerFactory emf) {
        // Initialize GuideDTOs
        GuideDTO guide1 = new GuideDTO("Alice", "Smith", "alice@example.com", "1234567890", 5);
        GuideDTO guide2 = new GuideDTO("Bob", "Brown", "bob@example.com", "0987654321", 10);

        // Initialize TripDTOs with LocalDateTime
        TripDTO trip1 = new TripDTO(LocalTime.of(10, 0), LocalTime.of(18, 0), "45.0", "90.0", "Beach Adventure", 500.0, Category.BEACH);
        TripDTO trip2 = new TripDTO(LocalTime.of(8, 30), LocalTime.of(16, 30), "46.0", "91.0", "City Explorer", 300.0, Category.CITY);
        TripDTO trip3 = new TripDTO(LocalTime.of(9, 0), LocalTime.of(19, 0), "47.0", "92.0", "Forest Expedition", 400.0, Category.FOREST);
        TripDTO trip4 = new TripDTO(LocalTime.of(11, 15), LocalTime.of(17, 45), "48.0", "93.0", "Lake Retreat", 350.0, Category.LAKE);

        // Convert DTOs to Entities
        Guide guideEntity1 = new Guide(guide1);
        Guide guideEntity2 = new Guide(guide2);

        Trip tripEntity1 = new Trip(trip1);
        Trip tripEntity2 = new Trip(trip2);
        Trip tripEntity3 = new Trip(trip3);
        Trip tripEntity4 = new Trip(trip4);

        // Set guide associations on trips
        tripEntity1.setGuide(guideEntity1);
        tripEntity2.setGuide(guideEntity1);
        tripEntity3.setGuide(guideEntity2);
        tripEntity4.setGuide(guideEntity2);

        // Add trips to guide entities
        guideEntity1.setTrips(List.of(tripEntity1, tripEntity2));
        guideEntity2.setTrips(List.of(tripEntity3, tripEntity4));

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Persist only the guides; their trips should be persisted automatically due to cascading
            em.persist(guideEntity1);
            em.persist(guideEntity2);

            em.getTransaction().commit();
        }
    }

}
