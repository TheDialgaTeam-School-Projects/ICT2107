// Yong Jian Ming

package analyzer.project.views;

import analyzer.project.ActionDelegate;

import javax.swing.*;

public abstract class AbstractView {
    protected abstract String getTitle();

    protected abstract JPanel getPanel();

    public void show() {
        show(e -> e.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE));
    }

    public void show(ActionDelegate<JFrame> action) {
        final JFrame window = new JFrame(getTitle());
        window.getContentPane().add(getPanel());
        window.setSize(1280, 720);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        action.invoke(window);
    }
}
