package analyzer.project.views;

import analyzer.project.IDisposable;
import analyzer.project.models.Covid19AnalyzerViewModel;
import analyzer.project.models.Covid19Case;
import analyzer.project.models.Covid19Repository;
import analyzer.project.models.MapUtility;
import com.esri.map.GraphicsLayer;
import com.esri.map.JMap;
import com.esri.map.MapOptions;
import com.esri.map.MapTip;
import com.esri.toolkit.overlays.NavigatorOverlay;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Covid19AnalyzerView implements IDisposable {
    private JPanel panel;
    private JMap map;
    private JPanel confirmedCasesByCountryPanel;
    private JLabel totalCaseConfirmedLabel;
    private JSlider daysSlider;

    private Covid19AnalyzerViewModel viewModel;
    private GraphicsLayer graphicsLayer;

    public JPanel getPanel() {
        return panel;
    }

    private void createUIComponents() throws IOException {
        viewModel = new Covid19AnalyzerViewModel();

        createDaysSliderUIComponents();
        createTotalConfirmedUIComponents();
        createConfirmedCasesByCountryUIComponents();
        createMapUIComponents();
    }

    private void createDaysSliderUIComponents() {
        final int amountOfDays = viewModel.getTotalAmountOfDays() - 1;

        daysSlider = new JSlider();
        daysSlider.setMaximum(amountOfDays);
        daysSlider.setValue(amountOfDays);
        daysSlider.addChangeListener(e -> {
            final int numberOfDays = daysSlider.getValue();

            updateTotalConfirmedUIComponents(numberOfDays);
            updateConfirmedCasesByCountryUIComponents(numberOfDays);
            updateMapUIComponents(numberOfDays);
        });
    }

    private void createTotalConfirmedUIComponents() {
        totalCaseConfirmedLabel = new JLabel();
        updateTotalConfirmedUIComponents(daysSlider.getValue());
    }

    private void updateTotalConfirmedUIComponents(int numberOfDays) {
        totalCaseConfirmedLabel.setText(NumberFormat.getInstance().format(viewModel.getTotalConfirmedCases(numberOfDays)));
    }

    private void createConfirmedCasesByCountryUIComponents() {
        confirmedCasesByCountryPanel = new JPanel();
        confirmedCasesByCountryPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        updateConfirmedCasesByCountryUIComponents(daysSlider.getValue());
    }

    private void updateConfirmedCasesByCountryUIComponents(int numberOfDays) {
        confirmedCasesByCountryPanel.removeAll();
        confirmedCasesByCountryPanel.invalidate();
        confirmedCasesByCountryPanel.updateUI();

        final List<Covid19Case> covid19Cases = viewModel.getCovid19CasesByCountry(numberOfDays, Covid19Repository.SORT_BY_CONFIRMED);
        covid19Cases.removeIf(covid19Case -> covid19Case.getConfirmed(numberOfDays) == 0);

        int rowAdded = 0;

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(covid19Cases.size(), 2);
        confirmedCasesByCountryPanel.setLayout(gridLayoutManager);

        final GridConstraints gridConstraints = new GridConstraints();

        for (final Covid19Case covid19Case : covid19Cases) {
            final JLabel confirmedLabel = new JLabel(NumberFormat.getInstance().format(covid19Case.getConfirmed(numberOfDays)));
            confirmedLabel.setForeground(new Color(230, 0, 0));

            gridConstraints.setColumn(0);
            gridConstraints.setRow(rowAdded);
            gridConstraints.setHSizePolicy(GridConstraints.SIZEPOLICY_FIXED);

            confirmedCasesByCountryPanel.add(confirmedLabel, gridConstraints);

            final JLabel countryLabel = new JLabel(covid19Case.getCountry());

            gridConstraints.setColumn(1);
            gridConstraints.setRow(rowAdded);
            gridConstraints.setFill(GridConstraints.FILL_HORIZONTAL);
            gridConstraints.setHSizePolicy(GridConstraints.SIZEPOLICY_WANT_GROW);

            confirmedCasesByCountryPanel.add(countryLabel, gridConstraints);

            rowAdded++;
        }
    }

    private void createMapUIComponents() {
        final MapOptions mapOptions = new MapOptions(MapOptions.MapType.TOPO);
        map = new JMap(mapOptions);

        final NavigatorOverlay navigator = new NavigatorOverlay();
        map.addMapOverlay(navigator);

        graphicsLayer = new GraphicsLayer();
        map.getLayers().add(graphicsLayer);

        final LinkedHashMap<String, String> displayFields = new LinkedHashMap<>();
        displayFields.put("location", "");
        displayFields.put("confirmed", "Confirmed: ");
        displayFields.put("deaths", "Deaths: ");
        displayFields.put("recovered", "Recovered: ");
        displayFields.put("active", "Active: ");

        graphicsLayer.setMapTip(new MapTip(displayFields));

        updateMapUIComponents(daysSlider.getValue());
    }

    private void updateMapUIComponents(int numberOfDays) {
        graphicsLayer.removeAll();

        final List<Covid19Case> covid19Cases = viewModel.getCovid19CasesByState(numberOfDays, Covid19Repository.SORT_BY_CONFIRMED);

        for (final Covid19Case covid19Case : covid19Cases) {
            if (covid19Case.getConfirmed(numberOfDays) == 0) continue;
            graphicsLayer.addGraphic(MapUtility.createGraphic(covid19Case, numberOfDays));
        }
    }

    @Override
    public void dispose() {
        map.dispose();
    }
}
