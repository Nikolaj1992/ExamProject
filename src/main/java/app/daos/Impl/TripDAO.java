package app.daos.Impl;

import app.daos.IDAO;
import app.daos.ITripGuideDAO;
import app.dtos.GuideDTO;
import app.dtos.PackingItemDTO;
import app.dtos.TripDTO;
import app.entities.Guide;
import app.entities.Trip;
import app.enums.Category;
import app.security.exceptions.ApiException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.*;
import java.util.stream.Collectors;

public class TripDAO implements IDAO<TripDTO, Integer>, ITripGuideDAO {

    private static TripDAO instance;
    private static EntityManagerFactory emf;

    public static TripDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new TripDAO();
        }
        return instance;
    }

    @Override
    public List<TripDTO> getAll() {
        try (var em = emf.createEntityManager()) {
            TypedQuery<TripDTO> query = em.createQuery("SELECT new app.dtos.TripDTO(t) FROM Trip t", TripDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public TripDTO getById(Integer id) {
        try (var em = emf.createEntityManager()) {
            Trip trip = em.find(Trip.class, id);
            if (trip == null) {
                throw new ApiException(404, "Trip with ID " + id + " not found");
            }
            Guide guide = trip.getGuide(); // Fetch associated guide
            GuideDTO guideDTO = (guide != null) ? new GuideDTO(guide) : null;
            return new TripDTO(trip.getId(), trip.getStartTime(), trip.getEndTime(), trip.getLongitude(), trip.getLatitude(), trip.getName(), trip.getPrice(), trip.getCategory(), guideDTO);
        } catch (Exception e) {
            throw new ApiException(500, "Error retrieving trip with ID " + id + ": " + e.getMessage());
        }
    }

    @Override
    public TripDTO create(TripDTO tripDTO) {
        try (var em = emf.createEntityManager()) {
            Trip trip = new Trip(tripDTO);
            em.getTransaction().begin();
            em.persist(trip);
            em.getTransaction().commit();
            return new TripDTO(trip);
        }
    }

    @Override
    public TripDTO update(Integer id, TripDTO tripDTO) {
        try (var em = emf.createEntityManager()) {
            Trip existingTrip = em.find(Trip.class, id);
            if (existingTrip == null) throw new ApiException(404, "Trip with ID " + id + " not found");

            em.getTransaction().begin();
            if (tripDTO.getStartTime() != null) {
                existingTrip.setStartTime(tripDTO.getStartTime());
            }
            if (tripDTO.getEndTime() != null) {
                existingTrip.setEndTime(tripDTO.getEndTime());
            }
            if (tripDTO.getLongitude() != null) {
                existingTrip.setLongitude(tripDTO.getLongitude());
            }
            if (tripDTO.getLatitude() != null) {
                existingTrip.setLatitude(tripDTO.getLatitude());
            }
            if (tripDTO.getName() != null) {
                existingTrip.setName(tripDTO.getName());
            }
            if (tripDTO.getPrice() != 0.0) {  // assuming price cannot be null, use a suitable default check
                existingTrip.setPrice(tripDTO.getPrice());
            }
            if (tripDTO.getCategory() != null) {
                existingTrip.setCategory(tripDTO.getCategory());
            }
            em.merge(existingTrip);
            em.getTransaction().commit();

            return new TripDTO(existingTrip);
        }
    }

    @Override
    public TripDTO delete(Integer id) {
        try (var em = emf.createEntityManager()) {
            Trip trip = em.find(Trip.class, id);
            em.getTransaction().begin();
            if (trip == null) {
                em.getTransaction().rollback();
                throw new ApiException(404, "Trip with ID " + id + " not found");
            }
            em.remove(trip);
            em.getTransaction().commit();
            return new TripDTO(trip);
        }
    }

    // check these
    @Override
    public void addGuideToTrip(int tripId, int guideId) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Trip trip = em.find(Trip.class, tripId);
            Guide guide = em.find(Guide.class, guideId);

            if (trip == null || guide == null) {
                em.getTransaction().rollback();
                throw new ApiException(404, "Trip or Guide not found");
            }

            trip.setGuide(guide);
            guide.getTrips().add(trip);
            em.merge(trip);
            em.merge(guide);
            em.getTransaction().commit();
        }
    }

    @Override
    public Set getTripsByGuide(int guideId) {
        try (var em = emf.createEntityManager()) {
            TypedQuery<Trip> query = em.createQuery("SELECT t FROM Trip t WHERE t.guide.id = :guideId", Trip.class);
            query.setParameter("guideId", guideId);

            List<Trip> trips = query.getResultList();
            Set<TripDTO> tripDTOSet = new HashSet<>();
            for (Trip trip : trips) {
                tripDTOSet.add(new TripDTO(trip));
            }
            return tripDTOSet;
        }
    }

    public List<TripDTO> findByCategory(Category category) {
        try (var em = emf.createEntityManager()) {
            TypedQuery<Trip> query = em.createQuery("SELECT t FROM Trip t WHERE t.category = :category", Trip.class);
            query.setParameter("category", category);
            List<Trip> trips = query.getResultList();
            return trips.stream()
                    .map(TripDTO::new)
                    .collect(Collectors.toList());
        }
    }

    public List<Map<String, Object>> getTotalPriceByGuide() {
        try (var em = emf.createEntityManager()) {
            TypedQuery<Object[]> query = em.createQuery(
                    "SELECT g.id, SUM(t.price) FROM Trip t JOIN t.guide g GROUP BY g.id", Object[].class);
            List<Object[]> results = query.getResultList();

            List<Map<String, Object>> totalPriceList = new ArrayList<>();

            for (Object[] result : results) {
                Integer guideId = (Integer) result[0];
                Double totalPrice = (Double) result[1];

                Map<String, Object> map = new HashMap<>();
                map.put("guideId", guideId);
                map.put("totalPrice", totalPrice);
                totalPriceList.add(map);
            }

            return totalPriceList;
        }
    }

}
