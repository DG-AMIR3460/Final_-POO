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
    // Contador por paciente para generar IDs secuenciales únicos por paciente, no globales
    private final Map<Long, Integer> counters = new HashMap<>();

    // El ID codifica el patientId y un secuencial de 4 dígitos — facilita rastrear qué citas pertenecen a un paciente con solo leer el ID
    public String generateId(long patientId) {
        int n = counters.getOrDefault(patientId, 0);
        counters.put(patientId, n + 1);
        return String.format("A-%d-%04d", patientId, n);
    }

    public boolean add(Appointment appointment) {
        boolean added = appointments.add(appointment);
        // Solo se notifica si el add fue exitoso — evita disparar actualizaciones de vista innecesarias
        if (added) notifyObservers();
        return added;
    }

    public Optional<Appointment> findById(String id) {
        return appointments.stream().filter(a -> a.getId().equals(id)).findFirst();
    }

    // Retorna copia defensiva para que el llamador no pueda modificar la lista interna
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

    // Reutiliza getByDoctorSortedDesc y aplica el filtro de estado encima — evita duplicar la lógica de ordenamiento
    public List<Appointment> getByDoctorPendingSortedDesc(long doctorId) {
        return getByDoctorSortedDesc(doctorId).stream()
                .filter(a -> a.getStatus() == core.model.enums.AppointmentStatus.PENDING)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void addObserver(ModelObserver observer)    { observers.add(observer); }
    @Override
    public void removeObserver(ModelObserver observer) { observers.remove(observer); }
    // Notificación en masa usando method reference — recorre todos los observadores registrados y llama onModelChanged
    @Override
    public void notifyObservers() { observers.forEach(ModelObserver::onModelChanged); }
}