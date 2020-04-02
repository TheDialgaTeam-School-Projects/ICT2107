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
import com.intellij.uiDesigner.core.Spacer;

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
    private static final String VIEW_BY_ACTIVE = "view_by_active";

    private static final Color CONFIRMED_COLOR = new Color(180, 0, 0);
    private static final Color DEATHS_COLOR = new Color(0, 0, 180);
    private static final Color RECOVERED_COLOR = new Color(0, 180, 0);
    private static final Color ACTIVE_COLOR = new Color(180, 135, 0);

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

                case VIEW_BY_ACTIVE:
                    viewBy = VIEW_BY_ACTIVE;
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

        JRadioButtonMenuItem viewByActiveMenuItem = new JRadioButtonMenuItem("By Active Cases");
        viewByActiveMenuItem.setActionCommand(VIEW_BY_ACTIVE);
        viewByActiveMenuItem.addActionListener(viewByAction);

        ButtonGroup viewByButtonGroup = new ButtonGroup();
        viewByButtonGroup.add(viewByConfirmedMenuItem);
        viewByButtonGroup.add(viewByDeathsMenuItem);
        viewByButtonGroup.add(viewByRecoveredMenuItem);
        viewByButtonGroup.add(viewByActiveMenuItem);

        JMenuItem viewGraph = new JMenuItem("Graph View By Country");
        viewGraph.addActionListener(e -> {
            try {
                final Covid19GraphView graphView = new Covid19GraphView();
                graphView.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        JMenuItem viewGraphPercentage = new JMenuItem("Top 5 Countries Recovery Rate");
        viewGraphPercentage.addActionListener(e -> {
            try {
                final Covid19PercentageGraphView graphView = new Covid19PercentageGraphView();
                graphView.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        viewMenu.add(viewByConfirmedMenuItem);
        viewMenu.add(viewByDeathsMenuItem);
        viewMenu.add(viewByRecoveredMenuItem);
        viewMenu.add(viewByActiveMenuItem);
        viewMenu.addSeparator();
        viewMenu.add(viewGraph);
        viewMenu.add(viewGraphPercentage);
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

            case VIEW_BY_ACTIVE:
                totalCasesLabel.setText("Total Active");
                totalCasesValueLabel.setForeground(ACTIVE_COLOR);
                totalCasesValueLabel.setText(NumberFormat.getInstance().format(viewModel.getTotalActiveCases(numberOfDays)));
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

            case VIEW_BY_ACTIVE:
                casesByCountryLabel.setText("Active Cases by Country");
                covid19Cases = viewModel.getCovid19CasesByCountry(numberOfDays, Covid19Repository.SORT_BY_ACTIVE);
                covid19Cases.removeIf(covid19Case -> covid19Case.getActive(numberOfDays) == 0);
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

                case VIEW_BY_ACTIVE:
                    confirmedLabel = new JLabel(NumberFormat.getInstance().format(covid19Case.getActive(numberOfDays)));
                    confirmedLabel.setForeground(ACTIVE_COLOR);
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

            case VIEW_BY_ACTIVE:
                covid19Cases = viewModel.getCovid19CasesByState(numberOfDays, Covid19Repository.SORT_BY_ACTIVE);
                covid19Cases.removeIf(e -> e.getActive(numberOfDays) == 0);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + viewBy);
        }

        for (final Covid19Case covid19Case : covid19Cases) {
            graphicsLayer.addGraphic(MapUtility.createGraphic(covid19Case, numberOfDays, viewBy));
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(5, 3, new Insets(8, 8, 8, 8), -1, -1));
        map = new JMap();
        map.setShowingCopyright(false);
        map.setShowingEsriLogo(false);
        map.setWrapAroundEnabled(true);
        panel.add(map, new GridConstraints(1, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(8, 8, 8, 8), -1, -1));
        panel.add(panel1, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        totalCasesValueLabel = new JLabel();
        Font totalCasesValueLabelFont = this.$$$getFont$$$(null, -1, 48, totalCasesValueLabel.getFont());
        if (totalCasesValueLabelFont != null) totalCasesValueLabel.setFont(totalCasesValueLabelFont);
        totalCasesValueLabel.setForeground(new Color(-1703936));
        panel1.add(totalCasesValueLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        totalCasesLabel = new JLabel();
        panel1.add(totalCasesLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        daysSlider = new JSlider();
        daysSlider.setMajorTickSpacing(7);
        daysSlider.setMinorTickSpacing(1);
        daysSlider.setPaintLabels(true);
        daysSlider.setPaintTicks(true);
        daysSlider.setSnapToTicks(true);
        panel.add(daysSlider, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 1, new Insets(8, 8, 8, 8), -1, -1));
        panel.add(panel2, new GridConstraints(2, 0, 3, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(300, -1), null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        casesByCountryLabel = new JLabel();
        panel2.add(casesByCountryLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(31);
        panel2.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        casesByCountryPanel = new JPanel();
        casesByCountryPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane1.setViewportView(casesByCountryPanel);
        casesByCountryPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8), null));
        final JLabel label1 = new JLabel();
        label1.setText("Number of Days since 22 January 2020");
        panel.add(label1, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JMenuBar menuBar1 = new JMenuBar();
        menuBar1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(menuBar1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        viewMenu = new JMenu();
        viewMenu.setText("View");
        menuBar1.add(viewMenu, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        menuBar1.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
