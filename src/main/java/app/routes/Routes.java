package app.routes;

import app.security.routes.SecurityRoutes;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final TripRoutes tripRoute = new TripRoutes();
    private final SecurityRoutes securityRoutes = new SecurityRoutes();

    public EndpointGroup getRoutes() {
        return () -> {
            path("/trips", tripRoute.getRoutes());
            path("/auth", securityRoutes.getSecurityRoutes());
        };
    }
}