package app.config;

import app.dtos.GuideDTO;
import app.dtos.TripDTO;
import app.entities.Guide;
import app.entities.Trip;
import app.enums.Category;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalTime;
import java.util.List;

public class Populator {

    // Fields to store GuideDTOs for tests
    public static GuideDTO guide1;
    public static GuideDTO guide2;

    public static void main(String[] args) {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        populate(emf);
        emf.close();

    }

    public static void populate(EntityManagerFactory emf) {
        // Initialize GuideDTOs
        guide1 = new GuideDTO(null, "Alice", "Smith", "alice@example.com", "1234567890", 5);
        guide2 = new GuideDTO(null, "Bob", "Brown", "bob@example.com", "0987654321", 10);

        // Initialize TripDTOs
        TripDTO trip1 = new TripDTO(null, LocalTime.of(10, 0), LocalTime.of(18, 0), "45.0", "90.0", "Beach Adventure", 500.0, Category.beach, guide1);
        TripDTO trip2 = new TripDTO(null, LocalTime.of(8, 30), LocalTime.of(16, 30), "46.0", "91.0", "City Explorer", 300.0, Category.city, guide1);
        TripDTO trip3 = new TripDTO(null, LocalTime.of(9, 0), LocalTime.of(19, 0), "47.0", "92.0", "Forest Expedition", 400.0, Category.forest, guide2);
        TripDTO trip4 = new TripDTO(null, LocalTime.of(11, 15), LocalTime.of(17, 45), "48.0", "93.0", "Lake Retreat", 350.0, Category.lake, guide2);

        // Convert DTOs to Entities
        Guide guideEntity1 = Guide.fromDTO(guide1);
        Guide guideEntity2 = Guide.fromDTO(guide2);

        Trip tripEntity1 = Trip.fromDTO(trip1);
        Trip tripEntity2 = Trip.fromDTO(trip2);
        Trip tripEntity3 = Trip.fromDTO(trip3);
        Trip tripEntity4 = Trip.fromDTO(trip4);

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

            // Persist only the guides; trips will be cascaded
            em.persist(guideEntity1);
            em.persist(guideEntity2);

            em.getTransaction().commit();

            guide1.setId(guideEntity1.getId());
            guide2.setId(guideEntity2.getId());
        }
    }

}
