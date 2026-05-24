package core.model.entities;

// Administrator no agrega atributos propios; su rol se distingue por tipo en tiempo de ejecución mediante instanceof
public class Administrator extends User {
    public Administrator(long id, String username, String firstname, String lastname, String password) {
        super(id, username, firstname, lastname, password);
    }
}