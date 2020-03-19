import com.esri.runtime.ArcGISRuntime;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Covid19AnalyzerProgram {

    public static void main(String[] args) {
        ArcGISRuntime.setClientID("B4bNpXdPJjeniCKf");

        JFrame window = new JFrame("Covid-19 Analyzer");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Covid19Analyzer covid19Analyzer = new Covid19Analyzer();
        window.getContentPane().add(covid19Analyzer.getPanel());

        window.setSize(1280, 720);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                covid19Analyzer.dispose();
            }
        });
    }
}
