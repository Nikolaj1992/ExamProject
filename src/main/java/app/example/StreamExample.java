package app.example;

import app.config.HibernateConfig;
import app.daos.Impl.TripDAO;
import app.dtos.TripDTO;
import app.enums.Category;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamExample {

    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private static final TripDAO tripDAO = TripDAO.getInstance(emf);

    public static void main(String[] args) {
        List<TripDTO> allTrips = tripDAO.getAll();

        // Group trips by category using streams
        Map<Category, List<TripDTO>> tripsByCategory = allTrips.stream()
                .collect(Collectors.groupingBy(TripDTO::getCategory));

        // Print the grouped trips using lambda expression
        tripsByCategory.forEach((category, tripList) -> {
            System.out.println("Category: " + category);
            tripList.forEach(trip -> System.out.println("  " + trip.getName()));
        });


        // Filter trips that are under a certain price
        List<TripDTO> affordableTrips = allTrips.stream()
                .filter(trip -> trip.getPrice() < 500)
                .collect(Collectors.toList());

        // Map filtered trips to their names
        List<String> tripNames = affordableTrips.stream()
                .map(TripDTO::getName)
                .collect(Collectors.toList());

        // Print the names of affordable trips
        System.out.println("Affordable trips priced under 500: ");
        tripNames.forEach(System.out::println);

        // Reduce to calculate the total price of affordable trips
        double totalAffordableTripPrice = affordableTrips.stream()
                .map(TripDTO::getPrice)
                .reduce(0.0, Double::sum);

        // Print of the total price of all affordable trips
        System.out.println("Total price of affordable trips: " + totalAffordableTripPrice);

    }

}

