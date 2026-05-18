package core.model.repositories;

import core.model.entities.Appointment;
import core.model.observers.ModelObserver;
import core.model.observers.Observable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AppointmentRepository implements IAppointmentRepository {

    private final List<Appointment> appointments = new ArrayList<>();
    private final List<ModelObserver> observers = new ArrayList<>();
    private final Map<Long, Integer> counters = new HashMap<>();

    public String generateId(long patientId) {
        int n = counters.getOrDefault(patientId, 0);
        counters.put(patientId, n + 1);
        return String.format("A-%d-%04d", patientId, n);
    }

    public boolean add(Appointment appointment) {
        boolean added = appointments.add(appointment);
        if (added) notifyObservers();
        return added;
    }

    public Optional<Appointment> findById(String id) {
        return appointments.stream().filter(a -> a.getId().equals(id)).findFirst();
    }

    public List<Appointment> getAll() { return new ArrayList<>(appointments); }

    public List<Appointment> getByPatientSortedDesc(long patientId) {
        return appointments.stream()
                .filter(a -> a.getPatient().getId() == patientId)
                .sorted(Comparator.comparing(Appointment::getDatetime).reversed())
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Appointment> getByDoctorSortedDesc(long doctorId) {
        return appointments.stream()
                .filter(a -> a.getDoctor().getId() == doctorId)
                .sorted(Comparator.comparing(Appointment::getDatetime).reversed())
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Appointment> getByDoctorPendingSortedDesc(long doctorId) {
        return getByDoctorSortedDesc(doctorId).stream()
                .filter(a -> a.getStatus() == core.model.enums.AppointmentStatus.PENDING)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void addObserver(ModelObserver observer)    { observers.add(observer); }
    @Override
    public void removeObserver(ModelObserver observer) { observers.remove(observer); }
    @Override
    public void notifyObservers() { observers.forEach(ModelObserver::onModelChanged); }
}
