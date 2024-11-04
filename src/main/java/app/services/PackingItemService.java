package app.services;

import app.dtos.PackingItemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PackingItemService {

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public PackingItemService() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public PackingItemDTO fetchPackingItems(String category) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://packingapi.cphbusinessapps.dk/packinglist/" + category))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), PackingItemDTO.class);
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PackingItemDTO();
    }

}
