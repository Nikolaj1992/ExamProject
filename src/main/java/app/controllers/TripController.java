package app.controllers;

import app.config.HibernateConfig;
import app.config.Populator;
import app.daos.Impl.TripDAO;
import app.dtos.PackingItemDTO;
import app.dtos.TripDTO;
import app.enums.Category;
import app.security.exceptions.ApiException;
import app.services.PackingItemService;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            String category = trip.getCategory().name().toLowerCase();
            PackingItemDTO packingItems = packingItemService.fetchPackingItems(category);

            Map<String, Object> response = new HashMap<>();
            response.put("trip", trip);
            response.put("packingItems", packingItems);

            ctx.json(response).status(200);
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
            String categoryParam = ctx.pathParam("category");
            if (categoryParam == null) {
                throw new ApiException(400, "Category path parameter is missing");
            }

            categoryParam = categoryParam.toLowerCase();
            Category category;
            try {
                category = Category.valueOf(categoryParam);
            } catch (IllegalArgumentException e) {
                throw new ApiException(400, "Invalid category value: " + categoryParam);
            }

            List<TripDTO> filteredTrips = tDao.getTripsByCategory(category);
            ctx.json(filteredTrips).status(200);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(500, "Internal Server Error: " + e.getMessage());
        }
    }

    public void getTotalPriceByGuide(Context ctx) {
        try {
            List<Map<String, Object>> totalPrices = tDao.getTotalPriceByGuide();
            ctx.json(totalPrices).status(200);
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "An unexpected error occurred", "details", e.getMessage()));
        }
    }

    public void getPackingItemsByCategory(Context ctx) {
        try {
            String categoryParam = ctx.pathParam("category").toLowerCase();
            if (categoryParam == null) {
                throw new ApiException(400, "Category path parameter is missing");
            }
            PackingItemDTO packingItems = packingItemService.fetchPackingItems(categoryParam);
            ctx.json(packingItems).status(200);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void getTotalWeightByTripId(Context ctx) {
        try {
            int tripId = Integer.parseInt(ctx.pathParam("id"));
            TripDTO trip = tDao.getById(tripId);
            if (trip == null) {
                throw new ApiException(404, "Trip with ID: " + tripId + " not found");
            }

            String category = trip.getCategory().name().toLowerCase();
            PackingItemDTO packingItems = packingItemService.fetchPackingItems(category);

            int totalWeight = packingItems.getItems().stream()
                    .mapToInt(item -> item.getWeightInGrams() * item.getQuantity())
                    .sum();

            ctx.json(Map.of("tripId", tripId, "totalWeight", totalWeight)).status(200);
        } catch (NumberFormatException e) {
            throw new ApiException(400, "Invalid Trip ID");
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(500, "Internal Server Error. Could not retrieve total weight for trip");
        }
    }

}
