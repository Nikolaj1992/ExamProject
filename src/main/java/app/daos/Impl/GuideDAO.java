package app.daos.Impl;

import app.daos.IDAO;
import app.dtos.GuideDTO;
import app.entities.Guide;
import app.security.exceptions.ApiException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.stream.Collectors;

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
            TypedQuery<Guide> query = em.createQuery("SELECT g FROM Guide g", Guide.class);
            List<Guide> guides = query.getResultList();
            return guides.stream().map(Guide::toDTO).collect(Collectors.toList());
        }
    }

    @Override
    public GuideDTO getById(Integer id) {
        try (var em = emf.createEntityManager()) {
            Guide guide = em.find(Guide.class, id);
            if (guide == null) {
                throw new ApiException(404, "Guide with ID " + id + " not found");
            }
            return guide != null ? guide.toDTO() : null;
        } catch (Exception e) {
            throw new ApiException(500, "Error retrieving guide with ID " + id + ": " + e.getMessage());
        }
    }

    @Override
    public GuideDTO create(GuideDTO guideDTO) {
        try (var em = emf.createEntityManager()) {
            Guide guide = Guide.fromDTO(guideDTO);
            em.getTransaction().begin();
            em.persist(guide);
            em.getTransaction().commit();
            return guide.toDTO();
        }
    }

    @Override
    public GuideDTO update(Integer id, GuideDTO guideDTO) {
        try (var em = emf.createEntityManager()) {
            Guide guide = em.find(Guide.class, id);
            if (guide == null) {
                throw new ApiException(404, "Trip with ID " + id + " not found");
            }
            em.getTransaction().begin();
            guide.setFirstName(guideDTO.getFirstName());
            guide.setLastName(guideDTO.getLastName());
            guide.setEmail(guideDTO.getEmail());
            guide.setPhone(guideDTO.getPhone());
            guide.setYearsOfExperience(guideDTO.getYearsOfExperience());
            em.merge(guide);
            em.getTransaction().commit();
            return guide.toDTO();
        }

    }

    @Override
    public GuideDTO delete(Integer id) {
        try (var em = emf.createEntityManager()) {
            Guide guide = em.find(Guide.class, id);
            em.getTransaction().begin();
            if (guide == null) {
                em.getTransaction().rollback();
                throw new ApiException(404, "Trip with ID " + id + " not found");
            }
            em.remove(guide);
            em.getTransaction().commit();
            return guide.toDTO();
        }
    }

}
