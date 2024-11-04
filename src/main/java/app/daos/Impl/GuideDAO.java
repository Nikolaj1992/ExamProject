package app.daos.Impl;

import app.daos.IDAO;
import app.dtos.GuideDTO;
import app.entities.Guide;
import app.security.exceptions.ApiException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class GuideDAO implements IDAO<GuideDTO, Integer> {

    private static GuideDAO instance;
    private static EntityManagerFactory emf;

    public static GuideDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new GuideDAO();
        }
        return instance;
    }

    @Override
    public List<GuideDTO> getAll() {
        try (var em = emf.createEntityManager()) {
            TypedQuery<GuideDTO> query = em.createQuery("SELECT new app.dtos.GuideDTO(g) FROM Guide g", GuideDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public GuideDTO getById(Integer id) {
        try (var em = emf.createEntityManager()) {
            Guide guide = em.find(Guide.class, id);
            return new GuideDTO(guide);
        } catch (Exception e) {
            throw new ApiException(404, "Guide with ID " + id + " not found");
        }
    }

    @Override
    public GuideDTO create(GuideDTO guideDTO) {
        try (var em = emf.createEntityManager()) {
            Guide guide = new Guide(guideDTO);
            em.getTransaction().begin();
            em.persist(guide);
            em.getTransaction().commit();
            return new GuideDTO(guide);
        }
    }

    @Override
    public GuideDTO update(Integer id, GuideDTO guideDTO) {
        try (var em = emf.createEntityManager()) {
            Guide existingGuide = em.find(Guide.class, id);
            if (existingGuide == null) throw new ApiException(404, "Guide with ID " + id + " not found");

            em.getTransaction().begin();

            if (guideDTO.getFirstName() != null) {
                existingGuide.setFirstName(guideDTO.getFirstName());
            }
            if (guideDTO.getLastName() != null) {
                existingGuide.setLastName(guideDTO.getLastName());
            }
            if (guideDTO.getEmail() != null) {
                existingGuide.setEmail(guideDTO.getEmail());
            }
            if (guideDTO.getPhone() != null) {
                existingGuide.setPhone(guideDTO.getPhone());
            }
            if (guideDTO.getYearsOfExperience() != 0) {  // assuming experience cannot be null
                existingGuide.setYearsOfExperience(guideDTO.getYearsOfExperience());
            }

            em.merge(existingGuide);
            em.getTransaction().commit();

            return new GuideDTO(existingGuide);
        }
    }

    @Override
    public GuideDTO delete(Integer id) {
        try (var em = emf.createEntityManager()) {
            Guide guide = em.find(Guide.class, id);
            em.getTransaction().begin();
            if (guide == null) {
                em.getTransaction().rollback();
                throw new ApiException(404, "Doctor with ID " + id + " not found");
            }
            guide.getTrips().forEach(appointment -> {   // find better fix, maybe a placeholder doctor?
                appointment.setGuide(null);
                em.merge(appointment);
            });
            em.remove(guide);
            em.getTransaction().commit();
            return new GuideDTO(guide);
        }
    }

}
