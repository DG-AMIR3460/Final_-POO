package core.model.observers;

public interface Observable {
    void addObserver(ModelObserver observer);
    void removeObserver(ModelObserver observer);
    void notifyObservers();
}
