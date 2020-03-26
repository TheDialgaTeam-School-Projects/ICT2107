package analyzer.project;

public interface ActionDelegate<T> {
    void invoke(T e);
}
