package analyzer.project;

import analyzer.project.views.Covid19AnalyzerView;
import com.esri.runtime.ArcGISRuntime;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;

public final class Covid19AnalyzerProgram {
    private static final String PROGRAM_TITLE = "Covid-19 Analyzer";
    private static final String ARC_GIS_RUNTIME_CLIENT_ID = "B4bNpXdPJjeniCKf";

    public static void main(String[] args) {
        // Pre Program Setup.
        NumberFormat.getInstance().setGroupingUsed(true);
        ArcGISRuntime.setClientID(ARC_GIS_RUNTIME_CLIENT_ID);

        // Actual UI
        final JFrame window = new JFrame(PROGRAM_TITLE);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final Covid19AnalyzerView covid19AnalyzerView = new Covid19AnalyzerView();
        window.getContentPane().add(covid19AnalyzerView.getPanel());

        window.setSize(1280, 720);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                covid19AnalyzerView.dispose();
            }
        });
    }
}
