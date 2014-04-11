package de.example.jackson.databinding;

public class Weather {
    private Integer id;
    private String main;
    private String description;
    private String icon;

    public Integer getId() {
        return id;
    }

    public String getMain() {
        return main;
    }

    public String getDescription() {
        return description;
    }
    public String getIcon() {
        return icon;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public void setMain(final String main) {
        this.main = main;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setIcon(final String icon) {
        this.icon = icon;
    }
}
