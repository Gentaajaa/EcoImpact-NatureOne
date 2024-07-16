package fxml_helloworld;

import java.time.LocalDate;

public class Report {
    private String name;
    private String contact;
    private String location;
    private String damageType;
    private String description;
    private LocalDate date;

    // Constructor
    public Report(String name, String contact, String location, String damageType, String description) {
        this.name = name;
        this.contact = contact;
        this.location = location;
        this.damageType = damageType;
        this.description = description;
        this.date = LocalDate.now();
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String getLocation() {
        return location;
    }

    public String getDamageType() {
        return damageType;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    // Setter for date
    public void setDate(LocalDate date) {
        this.date = date;
    }

}
