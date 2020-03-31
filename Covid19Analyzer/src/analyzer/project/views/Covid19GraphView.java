package analyzer.project.views;

import analyzer.project.models.Covid19Case;
import analyzer.project.models.Covid19GraphViewModel;
import com.intellij.uiDesigner.core.GridConstraints;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Covid19GraphView extends AbstractView {
    private static final String VIEW_TITLE = "Covid-19 Graph View";

    private JPanel panel;
    private JComboBox<Object> comboBox;
    private JPanel chart;
    private JButton loadButton;

    private Covid19GraphViewModel viewModel;

    public Covid19GraphView() throws IOException {
        viewModel = new Covid19GraphViewModel();

        createComboBoxUIComponents();
        createButtonsUIComponents();
    }

    @Override
    protected String getTitle() {
        return VIEW_TITLE;
    }

    @Override
    protected JPanel getPanel() {
        return panel;
    }

    private void createComboBoxUIComponents() {
        comboBox.setModel(new DefaultComboBoxModel<>(viewModel.getCovid19Countries().toArray()));
    }

    private void createButtonsUIComponents() {
        loadButton.addActionListener(e -> {
            final String selectedCountry = (String) comboBox.getSelectedItem();
            final CategoryDataset dataset = createDataset(selectedCountry);

            final JFreeChart freeChart = ChartFactory.createLineChart(selectedCountry, "Number of Days since 22 January 2020", "Cases", dataset, PlotOrientation.VERTICAL, true, true, false);
            freeChart.getCategoryPlot().getDomainAxis().setMaximumCategoryLabelWidthRatio(100);
            freeChart.getPlot().setBackgroundPaint(Color.white);

            final LineAndShapeRenderer lineAndShapeRenderer = (LineAndShapeRenderer) freeChart.getCategoryPlot().getRenderer();
            lineAndShapeRenderer.setBaseShapesVisible(true);
            lineAndShapeRenderer.setSeriesPaint(3, Color.orange);

            final ChartFrame chartFrame = new ChartFrame("Line Chart", freeChart);

            final GridConstraints gridConstraints = new GridConstraints();
            gridConstraints.setColumn(0);
            gridConstraints.setRow(0);
            gridConstraints.setHSizePolicy(GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW);
            gridConstraints.setVSizePolicy(GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW);
            gridConstraints.setFill(GridConstraints.FILL_BOTH);

            chart.removeAll();
            chart.invalidate();
            chart.updateUI();
            chart.add(chartFrame.getChartPanel(), gridConstraints);
        });
    }

    private CategoryDataset createDataset(String country) {
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        final Covid19Case covid19Case = viewModel.getCovid19CaseByCountry(country);

        for (int i = 0; i < covid19Case.getConfirmed().length; i++) {
            dataset.addValue(covid19Case.getConfirmed(i), "Confirmed", String.format("%d", i));
            dataset.addValue(covid19Case.getDeaths(i), "Deaths", String.format("%d", i));
            dataset.addValue(covid19Case.getRecovered(i), "Recovered", String.format("%d", i));
            dataset.addValue(covid19Case.getActive(i), "Active", String.format("%d", i));
        }

        return dataset;
    }
}
