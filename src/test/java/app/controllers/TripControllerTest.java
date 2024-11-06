package app.controllers;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.config.Populator;
import app.dtos.GuideDTO;
import app.dtos.TripDTO;
import app.enums.Category;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class TripControllerTest {

    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private static Javalin app;
    private static final int TEST_PORT = 7000;

    private static GuideDTO guide1;
    private static GuideDTO guide2;

    @BeforeAll
    static void setUpAll() {
        HibernateConfig.setTest(true);

        app = ApplicationConfig.startServer(TEST_PORT);

        RestAssured.baseURI = "http://localhost:" + TEST_PORT + "/api";
    }

    @AfterAll
    static void tearDownAll() {
        ApplicationConfig.stopServer(app);
        emf.close();
    }

    @BeforeEach
    void setUp() {
        if (!emf.isOpen()) {
            emf = HibernateConfig.getEntityManagerFactoryForTest();
        }
        Populator.populate(emf);

        // GuideDTOs for tests from Populator
        guide1 = Populator.guide1;
        guide2 = Populator.guide2;
    }

    @AfterEach
    void tearDown() {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Trip").executeUpdate();
            em.createQuery("DELETE FROM Guide").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE trip_trip_id_seq RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE guide_guide_id_seq RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @Test
    void getAll() {
        List<TripDTO> trips =
                given()
                        .when()
                        .get("/trips")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(new TypeRef<List<TripDTO>>() {});

        assertThat(trips.size(), is(4));
        assertThat(trips.get(0).getName(), is("Beach Adventure"));
        assertThat(trips.get(1).getName(), is("City Explorer"));
    }

    @Test
    void getById() {
        int tripId = 1;
        TripDTO trip =
                given()
                        .when()
                        .get("/trips/{id}", tripId)
                        .then()
                        .statusCode(200)
                        .extract()
                        .jsonPath()
                        .getObject("trip", TripDTO.class);

        assertThat(trip.getName(), is("Beach Adventure"));
        assertThat(trip.getCategory(), is(Category.beach));
    }

    @Test
    void create() {
        TripDTO newTrip = new TripDTO(null, LocalTime.of(10, 0), LocalTime.of(18, 0), "45.0", "50.0", "Cave Adventure", 500.0, Category.forest, guide1);
        TripDTO createdTrip =
                given()
                        .contentType("application/json")
                        .body(newTrip)
                        .when()
                        .post("/trips")
                        .then()
                        .statusCode(201)
                        .extract()
                        .as(TripDTO.class);

        assertThat(createdTrip.getId(), is(notNullValue()));
        assertThat(createdTrip.getName(), is("Cave Adventure"));
        assertThat(createdTrip.getCategory(), is(Category.forest));

        List<TripDTO> trips =
                given()
                        .when()
                        .get("/trips")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(new TypeRef<List<TripDTO>>() {});

        assertThat(trips.size(), is(5));
    }

    @Test
    void update() {
        int tripId = 1;
        TripDTO updatedTrip = new TripDTO(tripId ,LocalTime.of(10, 0), LocalTime.of(18, 0), "45.0", "50.0", "Forest Adventure", 500.0, Category.forest, guide1);
        TripDTO trip =
                given()
                        .contentType("application/json")
                        .body(updatedTrip)
                        .when()
                        .put("/trips/{id}", tripId)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(TripDTO.class);

        assertThat(trip.getName(), is("Forest Adventure"));
    }

    @Test
    void delete() {
        int tripId = 1;
        int tripsAlreadyInTestDB = 4;

        given()
                .when()
                .delete("/trips/{id}", tripId)
                .then()
                .statusCode(204);

        // Check that the trip was deleted
        given()
                .when()
                .get("/trips/{id}", tripId)
                .then()
                .statusCode(404);

        List<TripDTO> allTripsAfterDeletion =
                given()
                        .when()
                        .get("/trips")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(new TypeRef<List<TripDTO>>() {});

        assertThat(allTripsAfterDeletion.size(), is(tripsAlreadyInTestDB - 1));
    }

    @Test
    void populate() {
        int firstTripId = 1;
        List<TripDTO> trips =
                given()
                        .when()
                        .get("/trips")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(new TypeRef<List<TripDTO>>() {});

        assertThat(trips.size(), is(4));

        // Check that a trip has a guide
        TripDTO firstTrip =
                given()
                        .when()
                        .get("/trips/{id}", firstTripId)
                        .then()
                        .statusCode(200)
                        .extract()
                        .jsonPath()
                        .getObject("trip", TripDTO.class);

        assertThat(firstTrip.getGuide().getEmail(), is("alice@example.com"));
    }

    @Test
    void addGuideToTrip() {
        int tripId = 1;
        given()
                .contentType("application/json")
                .body(guide2)
                .when()
                .put("/trips/{tripId}/guides/{guideId}", tripId, guide2.getId())
                .then()
                .statusCode(204);

        TripDTO updatedTrip =
                given()
                        .when()
                        .get("/trips/{id}", tripId)
                        .then()
                        .statusCode(200)
                        .extract()
                        .jsonPath()
                        .getObject("trip", TripDTO.class);

        assertThat(updatedTrip.getGuide().getEmail(), is(guide2.getEmail()));
    }

    @Test
    void getTripsByGuide() {
        int guideId = guide1.getId();

        List<TripDTO> tripsByGuide =
                given()
                        .when()
                        .get("/trips/guides/{guideId}", guideId)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(new TypeRef<List<TripDTO>>() {});

        assertThat(tripsByGuide.size(), is(2));
        assertThat(tripsByGuide.get(0).getName(), is("Beach Adventure"));
        assertThat(tripsByGuide.get(1).getName(), is("City Explorer"));
    }

    @Test
    void getTripsByCategory() {
        String category = Category.beach.name();

        List<TripDTO> beachTrips =
                given()
                        .when()
                        .get("/trips/category/{category}", category)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(new TypeRef<List<TripDTO>>() {});

        assertThat(beachTrips.size(), is(1));
        assertThat(beachTrips.get(0).getName(), is("Beach Adventure"));
    }

    @Test
    void getTotalPriceByGuide() {
        List<Map<String, Object>> response =
                given()
                        .when()
                        .get("/trips/guide/totalPrice")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(new TypeRef<List<Map<String, Object>>>() {});

        Map<String, Object> guide1Response = response.get(0);
        Map<String, Object> guide2Response = response.get(1);

        // Assert the total price for the guides, going by what was returned from manual dev.http testing
        assertThat(guide1Response.get("guideId"), is(1));
        assertThat(guide1Response.get("totalPrice"), is(800.0));

        assertThat(guide2Response.get("guideId"), is(2));
        assertThat(guide2Response.get("totalPrice"), is(750.0));
    }

    @Test
    void getPackingItemsByCategory() {
        String category = Category.beach.name();

        Map<String, Object> response =
                given()
                        .when()
                        .get("/trips/category/{category}/packingItems", category)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Map.class);

        // Extract items from response
        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");

        // Extract name from each item
        List<String> packingItems = items.stream()
                .map(item -> (String) item.get("name"))
                .collect(Collectors.toList());

        assertThat(packingItems, containsInAnyOrder(
                "Beach Umbrella", "Beach Water Bottle", "Beach Cooler",
                "Beach Towel", "Beach Ball", "Sunscreen SPF 50", "Beach Chair"));
    }

    @Test
    void getTotalWeightByTripId() {
        int tripId = 1;

        Map<String, Object> response =
                given()
                        .when()
                        .get("/trips/{id}/totalWeight", tripId)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Map.class);

        Integer totalWeight = (Integer) response.get("totalWeight");

        assertThat(totalWeight, is(7300));
    }

}