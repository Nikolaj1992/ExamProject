package app.controllers;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.config.Populator;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class TripControllerTest {

    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private static Javalin app;
    private static final int TEST_PORT = 7000;

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
                        .as(new TypeRef<TripDTO>() {});

        assertThat(trip.getName(), is("Beach Adventure"));
        assertThat(trip.getCategory(), is(Category.BEACH));
    }

    @Test
    void create() {
        TripDTO newTrip = new TripDTO(LocalTime.of(10, 0), LocalTime.of(18, 0), "45.0", "50.0", "Cave Adventure", 500.0, Category.FOREST);
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
        assertThat(createdTrip.getCategory(), is(Category.FOREST));

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
        TripDTO updatedTrip = new TripDTO(LocalTime.of(10, 0), LocalTime.of(18, 0), "45.0", "50.0", "Forest Adventure", 500.0, Category.FOREST);
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
    }

    @Test
    void addGuideToTrip() {
    }

    @Test
    void getTripsByGuide() {
    }

    @Test
    void getTripsByCategory() {
    }

    @Test
    void getTotalPriceByGuide() {
    }

    @Test
    void getPackingItemsByCategory() {
    }

    @Test
    void getTotalWeightByTripId() {
    }

}