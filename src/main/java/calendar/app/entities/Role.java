package calendar.app.entities;

public enum Role {
    USER,
    ADMIN;

    public String getName() {
        return this.name();
    }
}
