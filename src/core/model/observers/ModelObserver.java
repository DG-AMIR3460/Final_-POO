package core.model.observers;

// Contrato del patrón Observer — cualquier componente que necesite reaccionar a cambios en el modelo implementa esta interfaz
public interface ModelObserver {
    void onModelChanged();
}