package core.model.repositories;

import core.model.entities.Administrator;
import core.model.entities.Doctor;
import core.model.entities.Patient;
import core.model.entities.User;
import core.model.enums.Specialty;
import core.model.observers.ModelObserver;
import core.model.observers.Observable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements IUserRepository {

    private final List<User> users = new ArrayList<>();
    private final List<ModelObserver> observers = new ArrayList<>();

    public UserRepository() {
        loadFromJson();
        // El administrador por defecto se agrega en código para garantizar que siempre exista, independientemente del JSON
        users.add(new Administrator(0L, "admin", "Admin", "System", "admin123"));
    }

    // Deserialización polimórfica: el campo "type" del JSON decide qué subclase de User instanciar
    private void loadFromJson() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("json/users.json")));
            JSONArray arr = new JSONObject(content).getJSONArray("users");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String type = obj.getString("type");
                long id = obj.getLong("id");
                String username = obj.getString("username");
                String firstname = obj.getString("firstname");
                String lastname = obj.getString("lastname");
                String password = obj.getString("password");
                // El switch crea la subclase correcta según el tipo — patrón de fábrica implícito dentro del repositorio
                switch (type) {
                    case "admin" -> users.add(new Administrator(id, username, firstname, lastname, password));
                    case "patient" -> {
                        String email = obj.getString("email");
                        LocalDate birthdate = LocalDate.parse(obj.getString("birthdate"));
                        boolean gender = obj.getBoolean("gender");
                        long phone = obj.getLong("phone");
                        String address = obj.getString("address");
                        users.add(new Patient(id, username, firstname, lastname, password,
                                email, birthdate, gender, phone, address));
                    }
                    case "doctor" -> {
                        // fromJson maneja alias abreviados del JSON que no coinciden exactamente con los nombres del enum
                        Specialty specialty = Specialty.fromJson(obj.getString("specialty"));
                        String licence = obj.getString("licenceNumber");
                        String office = obj.getString("assignedOffice");
                        users.add(new Doctor(id, username, firstname, lastname, password,
                                specialty, licence, office));
                    }
                }
            }
        } catch (IOException e) {
            // Fallo silencioso con log — el sistema arranca vacío en lugar de lanzar excepción y bloquear la app
            System.err.println("Could not load users.json: " + e.getMessage());
        }
    }

    public Optional<User> findByUsername(String username) {
        return users.stream().filter(u -> u.getUsername().equals(username)).findFirst();
    }

    public Optional<User> findById(long id) {
        return users.stream().filter(u -> u.getId() == id).findFirst();
    }

    public boolean usernameExists(String username) {
        return users.stream().anyMatch(u -> u.getUsername().equals(username));
    }

    public boolean idExists(long id) {
        return users.stream().anyMatch(u -> u.getId() == id);
    }

    // Copia defensiva para que el llamador no pueda modificar la lista interna
    public List<User> getAll() { return new ArrayList<>(users); }

    // El filtro por instanceof aprovecha el polimorfismo de subtipo para extraer solo la subclase requerida
    public List<Doctor> getDoctors() {
        return users.stream()
                .filter(u -> u instanceof Doctor)
                .map(u -> (Doctor) u)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Patient> getPatients() {
        return users.stream()
                .filter(u -> u instanceof Patient)
                .map(u -> (Patient) u)
                .collect(java.util.stream.Collectors.toList());
    }

    public boolean add(User user) {
        boolean added = users.add(user);
        // Solo notifica si el usuario fue agregado efectivamente — evita disparar actualizaciones de vista innecesarias
        if (added) notifyObservers();
        return added;
    }

    @Override
    public void addObserver(ModelObserver observer)    { observers.add(observer); }
    @Override
    public void removeObserver(ModelObserver observer) { observers.remove(observer); }
    @Override
    public void notifyObservers() { observers.forEach(ModelObserver::onModelChanged); }
}