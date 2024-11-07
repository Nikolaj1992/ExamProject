package app.daos.Impl;

import app.daos.IDAO;
import app.daos.ITripGuideDAO;
import app.dtos.TripDTO;
import app.entities.Guide;
import app.entities.Trip;
import app.enums.Category;
import app.security.exceptions.ApiException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.*;
import java.util.stream.Collectors;

public class TripDAO implements IDAO<TripDTO, Integer>, ITripGuideDAO<TripDTO> {

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
            TypedQuery<Trip> query = em.createQuery("SELECT t FROM Trip t", Trip.class);
            List<Trip> trips = query.getResultList();
            return trips.stream()
                    .map(Trip::toDTO)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public TripDTO getById(Integer id) {
        try (var em = emf.createEntityManager()) {
            Trip trip = em.createQuery("SELECT t FROM Trip t JOIN FETCH t.guide WHERE t.id = :id", Trip.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return trip.toDTO();
        } catch (NoResultException e) {
            throw new ApiException(404, "Trip with ID " + id + " not found");
        } catch (Exception e) {
            throw new ApiException(500, "Error retrieving trip with ID " + id + ": " + e.getMessage());
        }
    }

    @Override
    public TripDTO create(TripDTO tripDTO) {
        try (var em = emf.createEntityManager()) {
            Trip trip = Trip.fromDTO(tripDTO);
            em.getTransaction().begin();
            em.persist(trip);
            em.getTransaction().commit();
            return trip.toDTO();
        }
    }

    @Override
    public TripDTO update(Integer id, TripDTO tripDTO) {
        try (var em = emf.createEntityManager()) {
            Trip trip = em.find(Trip.class, id);
            if (trip == null) {
                throw new ApiException(404, "Trip with ID " + id + " not found");
            }
            em.getTransaction().begin();
            trip.setStartTime(tripDTO.getStartTime());
            trip.setEndTime(tripDTO.getEndTime());
            trip.setLongitude(tripDTO.getLongitude());
            trip.setLatitude(tripDTO.getLatitude());
            trip.setName(tripDTO.getName());
            trip.setPrice(tripDTO.getPrice());
            trip.setCategory(tripDTO.getCategory());
            trip.setGuide(Guide.fromDTO(tripDTO.getGuide()));
            em.merge(trip);
            em.getTransaction().commit();
            return trip.toDTO();
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
            return trip.toDTO();
        }
    }

    @Override
    public void addGuideToTrip(int tripId, int guideId) {
        try (var em = emf.createEntityManager()) {
            Trip trip = em.find(Trip.class, tripId);
            Guide guide = em.find(Guide.class, guideId);
            em.getTransaction().begin();
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
    public Set<TripDTO> getTripsByGuide(int guideId) {
        try (var em = emf.createEntityManager()) {
            Guide guide = em.find(Guide.class, guideId);
            return guide != null ? guide.getTrips().stream()
                    .map(Trip::toDTO)
                    .collect(Collectors.toSet()) : Set.of();
        }
    }

    public List<TripDTO> getTripsByCategory(Category category) {
        try (var em = emf.createEntityManager()) {
            TypedQuery<Trip> query = em.createQuery("SELECT t FROM Trip t WHERE t.category = :category", Trip.class);
            query.setParameter("category", category);
            List<Trip> trips = query.getResultList();
            return trips.stream()
                    .map(Trip::toDTO)
                    .collect(Collectors.toList());
        }
    }

    public List<Map<String, Object>> getTotalPriceByGuide() {
        try (var em = emf.createEntityManager()) {
            List<Object[]> results = em.createQuery(
                            "SELECT t.guide.id, SUM(t.price) FROM Trip t GROUP BY t.guide.id", Object[].class)
                    .getResultList();

            return results.stream()
                    .map(result -> Map.of("guideId", result[0], "totalPrice", result[1]))
                    .collect(Collectors.toList());
        }
    }

    // No longer used, but potentially useful later on
    public List<Integer> getAllTripIds() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("SELECT t.id FROM Trip t", Integer.class).getResultList();
        }
    }

}
