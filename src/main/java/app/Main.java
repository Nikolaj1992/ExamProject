package app;

import app.config.ApplicationConfig;
import app.services.FetchData;

public class Main {
    public static void main(String[] args) {

        ApplicationConfig.startServer(7075);

//        System.out.println(FetchData.fetchPackingInfo("beach"));

    }
}
