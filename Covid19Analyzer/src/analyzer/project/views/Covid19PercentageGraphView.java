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
import java.util.List;

public class Covid19PercentageGraphView extends AbstractView {
    private static final String VIEW_TITLE = "Covid-19 Graph View";

    private JPanel panel;
    private JPanel chart;

    private Covid19GraphViewModel viewModel;

    public Covid19PercentageGraphView() throws IOException {
        viewModel = new Covid19GraphViewModel();

        final CategoryDataset dataset = createDataset();
        final JFreeChart freeChart = ChartFactory.createLineChart("Top 5 Countries Recovery Rate", "Number of Days since 22 January 2020", "Cases", dataset, PlotOrientation.VERTICAL, true, true, false);
        freeChart.getCategoryPlot().getDomainAxis().setMaximumCategoryLabelWidthRatio(100);
        freeChart.getPlot().setBackgroundPaint(Color.white);

        final LineAndShapeRenderer lineAndShapeRenderer = (LineAndShapeRenderer) freeChart.getCategoryPlot().getRenderer();
        lineAndShapeRenderer.setBaseShapesVisible(true);

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
    }

    @Override
    protected String getTitle() {
        return VIEW_TITLE;
    }

    @Override
    protected JPanel getPanel() {
        return panel;
    }

    private CategoryDataset createDataset() {
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        final List<Covid19Case> covid19Cases = viewModel.getCovid19CasesByCountryRecoveredPercentage();

        for (int i = 0; i < 5; i++) {
            final Covid19Case covid19Case = covid19Cases.get(i);
            final double[] recoveredPercentage = covid19Case.getRecoveredPercentage();

            for (int j = 0; j < covid19Case.getConfirmed().length; j++) {
                dataset.addValue(recoveredPercentage[j], covid19Case.getCountry() + " Recovered", String.format("%d", j));
            }
        }

        return dataset;
    }
}
