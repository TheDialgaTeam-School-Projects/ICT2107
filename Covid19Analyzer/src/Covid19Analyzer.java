import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.esri.map.GraphicsLayer;
import com.esri.map.JMap;
import com.esri.map.MapOptions;
import com.esri.map.MapTip;
import com.esri.toolkit.overlays.NavigatorOverlay;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.opencsv.exceptions.CsvValidationException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.List;

public class Covid19Analyzer implements IDisposable {
    private JPanel panel;
    private JMap map;
    private JPanel confirmedCasesByCountryPanel;
    private JLabel totalCaseConfirmedLabel;
    private JSlider daysSlider;

    private GraphicsLayer graphicsLayer;

    private static Graphic createGraphic(Covid19CaseOrigin covid19CaseOrigin, Covid19CaseAmount covid19CaseAmount) {
        LinkedHashMap<String, Object> attributes = new LinkedHashMap<>();

        if (covid19CaseOrigin.getState() == null || covid19CaseOrigin.getState().isEmpty()) {
            attributes.put("location", covid19CaseOrigin.getCountry());
        } else {
            attributes.put("location", covid19CaseOrigin.getState() + ", " + covid19CaseOrigin.getCountry());
        }

        attributes.put("confirmed", covid19CaseAmount.getConfirmed());
        attributes.put("deaths", covid19CaseAmount.getDeaths());
        attributes.put("recovered", covid19CaseAmount.getRecovered());
        attributes.put("active", covid19CaseAmount.getConfirmed() - covid19CaseAmount.getDeaths() - covid19CaseAmount.getRecovered());

        return new Graphic(getPointByLatLon(covid19CaseOrigin.getLat(), covid19CaseOrigin.getLon()), getSymbolByValue(covid19CaseAmount.getConfirmed()), attributes);
    }

    private static Point getPointByLatLon(double lat, double lon) {
        return GeometryEngine.project(lon, lat, SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR_AUXILIARY_SPHERE));
    }

    private static Symbol getSymbolByValue(long value) {
        Color color = new Color(230, 0, 0);

        SimpleMarkerSymbol circle = new SimpleMarkerSymbol(color, 1, SimpleMarkerSymbol.Style.CIRCLE);

        if (value <= 50) {
            circle.setSize(5);
        } else if (value <= 200) {
            circle.setSize(10);
        } else if (value <= 400) {
            circle.setSize(15);
        } else if (value <= 800) {
            circle.setSize(20);
        } else if (value <= 1600) {
            circle.setSize(25);
        } else if (value <= 3000) {
            circle.setSize(30);
        } else if (value <= 17000) {
            circle.setSize(35);
        } else if (value <= 50000) {
            circle.setSize(40);
        } else if (value <= 100000) {
            circle.setSize(45);
        }

        return circle;
    }

    public JPanel getPanel() {
        return panel;
    }

    private void createUIComponents() throws IOException, CsvValidationException {
        NumberFormat.getInstance().setGroupingUsed(true);

        createDaysSliderUIComponents();
        createTotalConfirmedUIComponents();
        createConfirmedCasesByCountryUIComponents();
        createMapUIComponents();
    }

    private void createDaysSliderUIComponents() throws IOException, CsvValidationException {
        int amountOfDays = Covid19Cases.getAmountOfDays();

        daysSlider = new JSlider();
        daysSlider.setMaximum(amountOfDays);
        daysSlider.setValue(amountOfDays);
        daysSlider.addChangeListener(e -> {
            int numberOfDays = daysSlider.getValue();

            try {
                updateTotalConfirmedUIComponents(numberOfDays);
                updateConfirmedCasesByCountryUIComponents(numberOfDays);
                updateMapUIComponents(numberOfDays);
            } catch (IOException | CsvValidationException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void createTotalConfirmedUIComponents() throws IOException {
        totalCaseConfirmedLabel = new JLabel();
        updateTotalConfirmedUIComponents(daysSlider.getValue());
    }

    private void updateTotalConfirmedUIComponents(int numberOfDays) throws IOException {
        totalCaseConfirmedLabel.setText(NumberFormat.getInstance().format(Covid19Cases.getTotalConfirmedCase(numberOfDays)));
    }

    private void createConfirmedCasesByCountryUIComponents() throws IOException {
        confirmedCasesByCountryPanel = new JPanel();
        confirmedCasesByCountryPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        updateConfirmedCasesByCountryUIComponents(daysSlider.getValue());
    }

    private void updateConfirmedCasesByCountryUIComponents(int numberOfDays) throws IOException {
        confirmedCasesByCountryPanel.removeAll();
        confirmedCasesByCountryPanel.invalidate();
        confirmedCasesByCountryPanel.updateUI();

        List<Covid19CaseOrigin> covid19CaseOrigins = Covid19Cases.getTotalConfirmedCaseByCountry(numberOfDays, Covid19Cases.COMPARE_MODE_CONFIRMED);
        covid19CaseOrigins.removeIf(covid19CaseOrigin -> covid19CaseOrigin.getCaseAmounts().get(numberOfDays).getConfirmed() == 0);

        int rowAdded = 0;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(covid19CaseOrigins.size(), 2);
        confirmedCasesByCountryPanel.setLayout(gridLayoutManager);

        GridConstraints gridConstraints = new GridConstraints();

        for (Covid19CaseOrigin covid19CaseOrigin : covid19CaseOrigins) {
            JLabel confirmedLabel = new JLabel(NumberFormat.getInstance().format(covid19CaseOrigin.getCaseAmounts().get(numberOfDays).getConfirmed()));
            confirmedLabel.setForeground(new Color(230, 0, 0));

            gridConstraints.setColumn(0);
            gridConstraints.setRow(rowAdded);
            gridConstraints.setHSizePolicy(GridConstraints.SIZEPOLICY_FIXED);

            confirmedCasesByCountryPanel.add(confirmedLabel, gridConstraints);

            JLabel countryLabel = new JLabel(covid19CaseOrigin.getCountry());

            gridConstraints.setColumn(1);
            gridConstraints.setRow(rowAdded);
            gridConstraints.setFill(GridConstraints.FILL_HORIZONTAL);
            gridConstraints.setHSizePolicy(GridConstraints.SIZEPOLICY_WANT_GROW);

            confirmedCasesByCountryPanel.add(countryLabel, gridConstraints);

            rowAdded++;
        }
    }

    private void createMapUIComponents() throws IOException, CsvValidationException {
        MapOptions mapOptions = new MapOptions(MapOptions.MapType.TOPO);
        map = new JMap(mapOptions);

        NavigatorOverlay navigator = new NavigatorOverlay();
        map.addMapOverlay(navigator);

        graphicsLayer = new GraphicsLayer();
        map.getLayers().add(graphicsLayer);

        LinkedHashMap<String, String> displayFields = new LinkedHashMap<>();
        displayFields.put("location", "");
        displayFields.put("confirmed", "Confirmed: ");
        displayFields.put("deaths", "Deaths: ");
        displayFields.put("recovered", "Recovered: ");
        displayFields.put("active", "Active: ");

        graphicsLayer.setMapTip(new MapTip(displayFields));

        updateMapUIComponents(daysSlider.getValue());
    }

    private void updateMapUIComponents(int numberOfDays) throws IOException, CsvValidationException {
        graphicsLayer.removeAll();

        List<Covid19CaseOrigin> covid19CaseOrigins = Covid19Cases.getCovid19CaseOriginByState();

        for (Covid19CaseOrigin covid19CaseOrigin : covid19CaseOrigins) {
            Covid19CaseAmount covid19CaseAmount = covid19CaseOrigin.getCaseAmounts().get(numberOfDays);
            if (covid19CaseAmount.getConfirmed() == 0) continue;
            graphicsLayer.addGraphic(createGraphic(covid19CaseOrigin, covid19CaseAmount));
        }
    }

    @Override
    public void dispose() {
        map.dispose();
    }
}
