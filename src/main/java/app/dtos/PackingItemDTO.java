package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PackingItemDTO {

    @JsonProperty("items")
    private List<Item> items;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Item {
        private String name;
        private int weightInGrams;
        private int quantity;
        private String description;
        private String category;
        private String createdAt;
        private String updatedAt;
        private List<BuyingOption> buyingOptions;

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        @ToString
        public static class BuyingOption {
            private String shopName;
            private String shopUrl;
            private double price;
        }
    }

}
