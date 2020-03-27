package analyzer.project.views;

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
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.List;

public final class Covid19AnalyzerView extends AbstractView {
    private static final String VIEW_TITLE = "Covid-19 Analyzer";
    private static final String VIEW_BY_CONFIRMED = "view_by_confirmed";
    private static final String VIEW_BY_DEATHS = "view_by_deaths";
    private static final String VIEW_BY_RECOVERED = "view_by_recovered";
    private static final Color CONFIRMED_COLOR = new Color(180, 0, 0);
    private static final Color DEATHS_COLOR = new Color(0, 0, 180);
    private static final Color RECOVERED_COLOR = new Color(0, 180, 0);

    private JPanel panel;
    private JMenu viewMenu;
    private JSlider daysSlider;
    private JLabel totalCasesLabel;
    private JLabel totalCasesValueLabel;
    private JLabel casesByCountryLabel;
    private JPanel casesByCountryPanel;
    private JMap map;

    private Covid19AnalyzerViewModel viewModel;
    private GraphicsLayer graphicsLayer;

    private String viewBy;
    private int numberOfDays;

    public Covid19AnalyzerView() throws IOException {
        viewModel = new Covid19AnalyzerViewModel();
        viewBy = VIEW_BY_CONFIRMED;
        numberOfDays = viewModel.getTotalAmountOfDays() - 1;

        createMenuUIComponents();
        createDaysSliderUIComponents();
        createMapUIComponents();

        updateCasesByDay();
    }

    @Override
    protected String getTitle() {
        return VIEW_TITLE;
    }

    @Override
    protected JPanel getPanel() {
        return panel;
    }

    private void createMenuUIComponents() {
        ActionListener viewByAction = e -> {
            switch (e.getActionCommand()) {
                case VIEW_BY_CONFIRMED:
                    viewBy = VIEW_BY_CONFIRMED;
                    break;

                case VIEW_BY_DEATHS:
                    viewBy = VIEW_BY_DEATHS;
                    break;

                case VIEW_BY_RECOVERED:
                    viewBy = VIEW_BY_RECOVERED;
                    break;
            }

            updateCasesByDay();
        };

        JRadioButtonMenuItem viewByConfirmedMenuItem = new JRadioButtonMenuItem("By Confirmed Cases", true);
        viewByConfirmedMenuItem.setActionCommand(VIEW_BY_CONFIRMED);
        viewByConfirmedMenuItem.addActionListener(viewByAction);

        JRadioButtonMenuItem viewByDeathsMenuItem = new JRadioButtonMenuItem("By Deaths Cases");
        viewByDeathsMenuItem.setActionCommand(VIEW_BY_DEATHS);
        viewByDeathsMenuItem.addActionListener(viewByAction);

        JRadioButtonMenuItem viewByRecoveredMenuItem = new JRadioButtonMenuItem("By Recovered Cases");
        viewByRecoveredMenuItem.setActionCommand(VIEW_BY_RECOVERED);
        viewByRecoveredMenuItem.addActionListener(viewByAction);

        ButtonGroup viewByButtonGroup = new ButtonGroup();
        viewByButtonGroup.add(viewByConfirmedMenuItem);
        viewByButtonGroup.add(viewByDeathsMenuItem);
        viewByButtonGroup.add(viewByRecoveredMenuItem);

        JMenuItem viewGraph = new JMenuItem("Graph View By Country");
        viewGraph.addActionListener(e -> {
            try {
                final Covid19GraphView graphView = new Covid19GraphView();
                graphView.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        viewMenu.add(viewByConfirmedMenuItem);
        viewMenu.add(viewByDeathsMenuItem);
        viewMenu.add(viewByRecoveredMenuItem);
        viewMenu.addSeparator();
        viewMenu.add(viewGraph);
    }

    private void createDaysSliderUIComponents() {
        daysSlider.setMaximum(numberOfDays);
        daysSlider.setValue(numberOfDays);
        daysSlider.addChangeListener(e -> {
            numberOfDays = daysSlider.getValue();
            updateCasesByDay();
        });
    }

    private void createMapUIComponents() {
        final MapOptions mapOptions = new MapOptions(MapOptions.MapType.TOPO);
        map.setMapOptions(mapOptions);

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
    }

    private void updateCasesByDay() {
        updateTotalCaseUIComponents();
        updateCasesByCountryUIComponents();
        updateMapUIComponents();
    }

    private void updateTotalCaseUIComponents() {
        switch (viewBy) {
            case VIEW_BY_CONFIRMED:
                totalCasesLabel.setText("Total Confirmed");
                totalCasesValueLabel.setForeground(CONFIRMED_COLOR);
                totalCasesValueLabel.setText(NumberFormat.getInstance().format(viewModel.getTotalConfirmedCases(numberOfDays)));
                break;

            case VIEW_BY_DEATHS:
                totalCasesLabel.setText("Total Deaths");
                totalCasesValueLabel.setForeground(DEATHS_COLOR);
                totalCasesValueLabel.setText(NumberFormat.getInstance().format(viewModel.getTotalDeathCases(numberOfDays)));
                break;

            case VIEW_BY_RECOVERED:
                totalCasesLabel.setText("Total Recovered");
                totalCasesValueLabel.setForeground(RECOVERED_COLOR);
                totalCasesValueLabel.setText(NumberFormat.getInstance().format(viewModel.getTotalRecoveredCases(numberOfDays)));
                break;
        }
    }

    private void updateCasesByCountryUIComponents() {
        final List<Covid19Case> covid19Cases;

        switch (viewBy) {
            case VIEW_BY_CONFIRMED:
                casesByCountryLabel.setText("Confirmed Cases by Country");
                covid19Cases = viewModel.getCovid19CasesByCountry(numberOfDays, Covid19Repository.SORT_BY_CONFIRMED);
                covid19Cases.removeIf(covid19Case -> covid19Case.getConfirmed(numberOfDays) == 0);
                break;

            case VIEW_BY_DEATHS:
                casesByCountryLabel.setText("Deaths Cases by Country");
                covid19Cases = viewModel.getCovid19CasesByCountry(numberOfDays, Covid19Repository.SORT_BY_DEATHS);
                covid19Cases.removeIf(covid19Case -> covid19Case.getDeaths(numberOfDays) == 0);
                break;

            case VIEW_BY_RECOVERED:
                casesByCountryLabel.setText("Recovered Cases by Country");
                covid19Cases = viewModel.getCovid19CasesByCountry(numberOfDays, Covid19Repository.SORT_BY_RECOVERED);
                covid19Cases.removeIf(covid19Case -> covid19Case.getRecovered(numberOfDays) == 0);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + viewBy);
        }

        casesByCountryPanel.removeAll();
        casesByCountryPanel.invalidate();
        casesByCountryPanel.updateUI();

        int rowAdded = 0;

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(covid19Cases.size(), 2);
        casesByCountryPanel.setLayout(gridLayoutManager);

        final GridConstraints gridConstraints = new GridConstraints();

        for (final Covid19Case covid19Case : covid19Cases) {

            final JLabel confirmedLabel;

            switch (viewBy) {
                case VIEW_BY_CONFIRMED:
                    confirmedLabel = new JLabel(NumberFormat.getInstance().format(covid19Case.getConfirmed(numberOfDays)));
                    confirmedLabel.setForeground(CONFIRMED_COLOR);
                    break;

                case VIEW_BY_DEATHS:
                    confirmedLabel = new JLabel(NumberFormat.getInstance().format(covid19Case.getDeaths(numberOfDays)));
                    confirmedLabel.setForeground(DEATHS_COLOR);
                    break;

                case VIEW_BY_RECOVERED:
                    confirmedLabel = new JLabel(NumberFormat.getInstance().format(covid19Case.getRecovered(numberOfDays)));
                    confirmedLabel.setForeground(RECOVERED_COLOR);
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + viewBy);
            }

            gridConstraints.setColumn(0);
            gridConstraints.setRow(rowAdded);
            gridConstraints.setHSizePolicy(GridConstraints.SIZEPOLICY_FIXED);

            casesByCountryPanel.add(confirmedLabel, gridConstraints);

            final JLabel countryLabel = new JLabel(covid19Case.getCountry());

            gridConstraints.setColumn(1);
            gridConstraints.setRow(rowAdded);
            gridConstraints.setFill(GridConstraints.FILL_HORIZONTAL);
            gridConstraints.setHSizePolicy(GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW);

            casesByCountryPanel.add(countryLabel, gridConstraints);

            rowAdded++;
        }
    }

    private void updateMapUIComponents() {
        graphicsLayer.removeAll();

        final List<Covid19Case> covid19Cases;

        switch (viewBy) {
            case VIEW_BY_CONFIRMED:
                covid19Cases = viewModel.getCovid19CasesByState(numberOfDays, Covid19Repository.SORT_BY_CONFIRMED);
                covid19Cases.removeIf(e -> e.getConfirmed(numberOfDays) == 0);
                break;

            case VIEW_BY_DEATHS:
                covid19Cases = viewModel.getCovid19CasesByState(numberOfDays, Covid19Repository.SORT_BY_DEATHS);
                covid19Cases.removeIf(e -> e.getDeaths(numberOfDays) == 0);
                break;

            case VIEW_BY_RECOVERED:
                covid19Cases = viewModel.getCovid19CasesByState(numberOfDays, Covid19Repository.SORT_BY_RECOVERED);
                covid19Cases.removeIf(e -> e.getRecovered(numberOfDays) == 0);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + viewBy);
        }

        for (final Covid19Case covid19Case : covid19Cases) {
            graphicsLayer.addGraphic(MapUtility.createGraphic(covid19Case, numberOfDays, viewBy));
        }
    }
}
