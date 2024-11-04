package app.controllers;

import app.config.HibernateConfig;
import app.config.Populator;
import app.daos.Impl.TripDAO;
import app.dtos.PackingItemDTO;
import app.dtos.TripDTO;
import app.entities.Trip;
import app.enums.Category;
import app.security.exceptions.ApiException;
import app.services.PackingItemService;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TripController {

    private final EntityManagerFactory emf;
    private final TripDAO tDao;
    private final PackingItemService packingItemService;


    public TripController(TripDAO tDao) {
        this.emf = HibernateConfig.getEntityManagerFactory();
        this.tDao = TripDAO.getInstance(emf);
        this.packingItemService = new PackingItemService();
    }

    public void getAll(Context ctx) {
        try {
            List<TripDTO> trips = tDao.getAll();
            ctx.json(trips, TripDTO.class).status(200);
        } catch (Exception e) {
            throw new ApiException(500, "Internal Server Error. Could not retrieve trips");
        }
    }

    public void getById(Context ctx) {
        try {
            Integer id = ctx.pathParamAsClass("id", Integer.class).get();
            TripDTO trip = tDao.getById(id);
            if (trip == null) {
                throw new ApiException(404, "Trip with ID: " + id + " not found. /api/trips/" + id);
            }
//            PackingItemDTO packingItems = packingItemService.fetchPackingItems(trip.getCategory().toString().toLowerCase());
//            trip.setPackingItems(packingItems);
            ctx.json(trip, TripDTO.class).status(200);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(500, "Internal Server Error. Could not retrieve trip");
        }
    }

    public void create(Context ctx) {
        try {
            TripDTO trip = ctx.bodyAsClass(TripDTO.class);
            if (trip.getName() == null || trip.getStartTime() == null || trip.getEndTime() == null || trip.getLongitude() == null || trip.getLatitude() == null|| trip.getCategory() == null) {
                throw new ApiException(400, "Missing required fields");
            }
            TripDTO createdTrip = tDao.create(trip);
            ctx.json(createdTrip, TripDTO.class).status(201);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(500, "Internal Server Error. Could not create trip");
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            TripDTO updatedTrip = ctx.bodyAsClass(TripDTO.class);

            TripDTO existingTrip = tDao.getById(id);
            if (existingTrip == null) {
                throw new ApiException(404, "Trip with ID: " + id + " not found");
            }
            tDao.update(id, updatedTrip);
            ctx.json(updatedTrip, TripDTO.class).status(200);
        } catch (NumberFormatException e) {
            throw new ApiException(400, "Invalid Trip ID");
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(500, "Internal Server Error. An error occurred while updating Trip");
        }
    }

    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            TripDTO trip = tDao.getById(id);
            if (trip == null) {
                throw new ApiException(404, "Trip with ID: " + id + " not found");
            }
            tDao.delete(id);
            ctx.status(204);
        } catch (NumberFormatException e) {
            throw new ApiException(400, "Invalid trip ID");
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(500, "Internal Server Error. An error occurred while deleting trip");
        }
    }

    public void populate(Context ctx) {
        try {
            Populator.populate(emf);
            ctx.status(201).result("Database populated successfully");
        } catch (Exception e) {
            throw new ApiException(500, "Internal Server Error. Could not populate database");
        }
    }

    // these two might be trouble
    public void addGuideToTrip(Context ctx) {
        try {
            int tripId = Integer.parseInt(ctx.pathParam("tripId"));
            int guideId = Integer.parseInt(ctx.pathParam("guideId"));
            tDao.addGuideToTrip(tripId, guideId);
            ctx.status(204); // No Content
        } catch (NumberFormatException e) {
            throw new ApiException(400, "Invalid Trip ID or Guide ID");
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(500, "Internal Server Error. Could not add guide to trip");
        }
    }

    public void getTripsByGuide(Context ctx) {
        try {
            int guideId = Integer.parseInt(ctx.pathParam("guideId"));
            Set<TripDTO> trips = tDao.getTripsByGuide(guideId);
            ctx.json(trips).status(200);
        } catch (NumberFormatException e) {
            throw new ApiException(400, "Invalid Guide ID");
        } catch (Exception e) {
            throw new ApiException(500, "Internal Server Error. Could not retrieve trips for guide");
        }
    }

    public void getTripsByCategory(Context ctx) {
        try {
            String categoryParam = ctx.queryParam("category");
            if (categoryParam == null) {
                throw new ApiException(400, "Missing category query parameter");
            }

            Category category;
            try {
                category = Category.valueOf(categoryParam.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ApiException(400, "Invalid category");
            }
            List<TripDTO> filteredTrips = tDao.findByCategory(category);

            ctx.json(filteredTrips, TripDTO.class).status(200);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getTotalPriceByGuide(Context ctx) {
        List<Map<String, Object>> totalPriceList = tDao.getTotalPriceByGuide();
        ctx.json(totalPriceList);
    }

    public void getPackingItemsByCategory(Context ctx) {
        String category = ctx.queryParam("category");
        if (category != null) {
            PackingItemDTO packingItems = packingItemService.fetchPackingItems(category.toLowerCase());
            ctx.json(packingItems, PackingItemDTO.class).status(200);
        } else {
            throw new ApiException(400, "Missing category query parameter");
        }
    }

    public void getTotalWeightByTripId(Context ctx) {
        Integer id = Integer.valueOf(ctx.pathParam("id"));
        TripDTO trip = tDao.getById(id);
        if (trip != null) {
            PackingItemDTO packingItems = packingItemService.fetchPackingItems(trip.getCategory().toString().toLowerCase());
            double totalWeight = packingItems.getItems().stream()
                    .mapToDouble(item -> item.getWeightInGrams() * item.getQuantity())
                    .sum();
            ctx.json(Map.of("tripId", id, "totalWeightInGrams", totalWeight));
        } else {
            ctx.status(404).json("Trip not found.");
        }
    }


}
