package main.model.repositories;

import main.model.entities.Hospitalization;
import main.model.observers.ModelObserver;
import main.model.observers.Observable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HospitalizationRepository implements IHospitalizationRepository {

    private final List<Hospitalization> hospitalizations = new ArrayList<>();
    private final List<ModelObserver> observers = new ArrayList<>();
    private final Map<Long, Integer> counters = new HashMap<>();

    public String generateId(long patientId) {
        int n = counters.getOrDefault(patientId, 0);
        counters.put(patientId, n + 1);
        return String.format("H-%d-%04d", patientId, n);
    }

    public boolean add(Hospitalization h) {
        boolean added = hospitalizations.add(h);
        if (added) notifyObservers();
        return added;
    }

    public Optional<Hospitalization> findById(String id) {
        return hospitalizations.stream().filter(h -> h.getId().equals(id)).findFirst();
    }

    public List<Hospitalization> getAll() { return new ArrayList<>(hospitalizations); }

    public List<Hospitalization> getByPatient(long patientId) {
        return hospitalizations.stream()
                .filter(h -> h.getPatient().getId() == patientId)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Hospitalization> getByDoctor(long doctorId) {
        return hospitalizations.stream()
                .filter(h -> h.getDoctor().getId() == doctorId)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void addObserver(ModelObserver observer)    { observers.add(observer); }
    @Override
    public void removeObserver(ModelObserver observer) { observers.remove(observer); }
    @Override
    public void notifyObservers() { observers.forEach(ModelObserver::onModelChanged); }
}
