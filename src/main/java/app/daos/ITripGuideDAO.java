package app.daos;

import java.util.Set;

public interface ITripGuideDAO<T> {

    void addGuideToTrip(int tripId, int guideId);
    Set<T> getTripsByGuide(int guideId);

}
