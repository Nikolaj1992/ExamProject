package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PackingItemDTO {

    @JsonProperty("items")
    private List<Item> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private String name;
        private int weightInGrams;
        private int quantity;
        private String description;
        private String category;
        private String createdAt;
        private String updatedAt;
        private List<BuyingOption> buyingOptions;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class BuyingOption {
            private String shopName;
            private String shopUrl;
            private double price;
        }
    }
}
