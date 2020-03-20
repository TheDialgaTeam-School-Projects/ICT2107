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
    private static final SpatialReference DEFAULT_SPATIAL_REFERENCE = SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR_AUXILIARY_SPHERE);
    private static final SimpleMarkerSymbol DEFAULT_CIRCLE = new SimpleMarkerSymbol(new Color(230, 0, 0), 1, SimpleMarkerSymbol.Style.CIRCLE);

    public static Graphic createGraphic(Covid19Case covid19Case, int numberOfDays) {
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
        final long active = confirmed - deaths - recovered;

        attributes.put("confirmed", confirmed);
        attributes.put("deaths", deaths);
        attributes.put("recovered", recovered);
        attributes.put("active", active);

        if (confirmed <= 50) {
            DEFAULT_CIRCLE.setSize(5);
        } else if (confirmed <= 200) {
            DEFAULT_CIRCLE.setSize(10);
        } else if (confirmed <= 400) {
            DEFAULT_CIRCLE.setSize(15);
        } else if (confirmed <= 800) {
            DEFAULT_CIRCLE.setSize(20);
        } else if (confirmed <= 1600) {
            DEFAULT_CIRCLE.setSize(25);
        } else if (confirmed <= 3000) {
            DEFAULT_CIRCLE.setSize(30);
        } else if (confirmed <= 17000) {
            DEFAULT_CIRCLE.setSize(35);
        } else if (confirmed <= 50000) {
            DEFAULT_CIRCLE.setSize(40);
        } else if (confirmed <= 100000) {
            DEFAULT_CIRCLE.setSize(45);
        }

        return new Graphic(geometry, DEFAULT_CIRCLE, attributes);
    }
}
