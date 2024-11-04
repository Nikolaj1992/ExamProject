package app.routes;

import app.controllers.TripController;
import app.daos.Impl.TripDAO;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;


public class TripRoutes {

    private final TripController tripController = new TripController(new TripDAO());

    protected EndpointGroup getRoutes() {

        return () -> {
            get("/", tripController::getAll, Role.ANYONE);
            get("/{id}", tripController::getById, Role.ANYONE);
            post("/", tripController::create); // set role user or admin
            put("/{id}", tripController::update); // set role user or admin
            delete("/{id}", tripController::delete); // set role user or admin
            put("/{tripId}/guides/{guideId}", tripController::addGuideToTrip); // set role user or admin
            get("/guides/{guideId}", tripController::getTripsByGuide, Role.ANYONE);
            post("/populate", tripController::populate, Role.ANYONE);   // only anyone for test reasons

            get("/category/{category}", tripController::getTripsByCategory, Role.ANYONE);
            get("/guides/totalprice", tripController::getTotalPriceByGuide, Role.ANYONE);

            get("/{id}/packing-items", tripController::getPackingItemsByCategory, Role.ANYONE);
            get("/{id}/total-weight", tripController::getTotalWeightByTripId, Role.ANYONE);
        };
    }
}
