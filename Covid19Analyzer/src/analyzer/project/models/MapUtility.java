// Yong Jian Ming

package analyzer.project.models;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MapUtility {
    private static final String VIEW_BY_CONFIRMED = "view_by_confirmed";
    private static final String VIEW_BY_DEATHS = "view_by_deaths";
    private static final String VIEW_BY_RECOVERED = "view_by_recovered";
    private static final String VIEW_BY_ACTIVE = "view_by_active";

    private static final Color CONFIRMED_COLOR = new Color(180, 0, 0, 150);
    private static final Color DEATHS_COLOR = new Color(0, 0, 180, 150);
    private static final Color RECOVERED_COLOR = new Color(0, 180, 0, 150);
    private static final Color ACTIVE_COLOR = new Color(180, 135, 0, 150);

    private static final SpatialReference DEFAULT_SPATIAL_REFERENCE = SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR_AUXILIARY_SPHERE);

    private static final SimpleMarkerSymbol CONFIRMED_CIRCLE = new SimpleMarkerSymbol(CONFIRMED_COLOR, 1, SimpleMarkerSymbol.Style.CIRCLE);
    private static final SimpleMarkerSymbol DEATHS_CIRCLE = new SimpleMarkerSymbol(DEATHS_COLOR, 1, SimpleMarkerSymbol.Style.CIRCLE);
    private static final SimpleMarkerSymbol RECOVERED_CIRCLE = new SimpleMarkerSymbol(RECOVERED_COLOR, 1, SimpleMarkerSymbol.Style.CIRCLE);
    private static final SimpleMarkerSymbol ACTIVE_CIRCLE = new SimpleMarkerSymbol(ACTIVE_COLOR, 1, SimpleMarkerSymbol.Style.CIRCLE);

    public static Graphic createGraphic(Covid19Case covid19Case, int numberOfDays, String viewBy) {
        final Geometry geometry = GeometryEngine.project(covid19Case.getLongitude(), covid19Case.getLatitude(), DEFAULT_SPATIAL_REFERENCE);
        final Map<String, Object> attributes = new LinkedHashMap<>();

        if (covid19Case.getState() == null || covid19Case.getState().isEmpty()) {
            attributes.put("location", covid19Case.getCountry());
        } else {
            attributes.put("location", covid19Case.getState() + ", " + covid19Case.getCountry());
        }

        final long confirmed = covid19Case.getConfirmed(numberOfDays);
        final long deaths = covid19Case.getDeaths(numberOfDays);
        final long recovered = covid19Case.getRecovered(numberOfDays);
        final long active = covid19Case.getActive(numberOfDays);

        attributes.put("confirmed", confirmed);
        attributes.put("deaths", deaths);
        attributes.put("recovered", recovered);
        attributes.put("active", active);

        final SimpleMarkerSymbol defaultCircle;

        switch (viewBy) {
            case VIEW_BY_CONFIRMED:
                defaultCircle = CONFIRMED_CIRCLE;
                break;

            case VIEW_BY_DEATHS:
                defaultCircle = DEATHS_CIRCLE;
                break;

            case VIEW_BY_RECOVERED:
                defaultCircle = RECOVERED_CIRCLE;
                break;

            case VIEW_BY_ACTIVE:
                defaultCircle = ACTIVE_CIRCLE;
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + viewBy);
        }

        if (confirmed <= 50) {
            defaultCircle.setSize(5);
        } else if (confirmed <= 200) {
            defaultCircle.setSize(10);
        } else if (confirmed <= 400) {
            defaultCircle.setSize(15);
        } else if (confirmed <= 800) {
            defaultCircle.setSize(20);
        } else if (confirmed <= 1600) {
            defaultCircle.setSize(25);
        } else if (confirmed <= 3000) {
            defaultCircle.setSize(30);
        } else if (confirmed <= 17000) {
            defaultCircle.setSize(35);
        } else if (confirmed <= 50000) {
            defaultCircle.setSize(40);
        } else if (confirmed <= 100000) {
            defaultCircle.setSize(45);
        }

        return new Graphic(geometry, defaultCircle, attributes);
    }
}
