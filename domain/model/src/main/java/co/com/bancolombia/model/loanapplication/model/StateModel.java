package co.com.bancolombia.model.loanapplication.model;

public class StateModel {
    private Long idState;
    private String name;
    private String description;

    public StateModel() {
    }

    public StateModel(Long idState) {
        this.idState = idState;
    }

    public StateModel(String description, String name, Long idState) {
        this.idState = idState;
        this.name = name;
        this.description = description;
    }

    public StateModel(Long idState, String name) {
        this.idState = idState;
        this.name = name;
    }

    public Long getIdState() {
        return idState;
    }

    public void setIdState(Long idState) {
        this.idState = idState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
