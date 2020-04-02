// Yong Jian Ming

package analyzer.project;

import analyzer.project.views.Covid19AnalyzerView;
import com.esri.runtime.ArcGISRuntime;

import javax.swing.*;
import java.io.IOException;
import java.text.NumberFormat;

public final class Covid19AnalyzerProgram {
    private static final String ARC_GIS_RUNTIME_CLIENT_ID = "B4bNpXdPJjeniCKf";

    public static void main(String[] args) throws IOException {
        // Pre Program Setup.
        NumberFormat.getInstance().setGroupingUsed(true);
        ArcGISRuntime.setClientID(ARC_GIS_RUNTIME_CLIENT_ID);

        final Covid19AnalyzerView covid19AnalyzerView = new Covid19AnalyzerView();
        covid19AnalyzerView.show(e -> e.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE));
    }
}
