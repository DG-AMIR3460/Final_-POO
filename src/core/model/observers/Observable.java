package core.model.observers;

// Define el lado emisor del patrón Observer — los repositorios implementan esta interfaz para notificar cambios a las vistas
public interface Observable {
    void addObserver(ModelObserver observer);
    void removeObserver(ModelObserver observer);
    void notifyObservers();
}