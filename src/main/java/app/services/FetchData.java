package app.services;

import app.dtos.PackingItemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// Test to read from API
public class FetchData {

    public static PackingItemDTO fetchPackingInfo(String category) {
        // fetch weather info from an external API
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Create an HttpClient instance
            HttpClient client = HttpClient.newHttpClient();

            // Create a request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://packingapi.cphbusinessapps.dk/packinglist/" + category))
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check the status code and print the response
            if (response.statusCode() == 200) {
                PackingItemDTO packingItemDTO = objectMapper.readValue(response.body(), PackingItemDTO.class);
                return packingItemDTO;
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new PackingItemDTO();
    }

}
