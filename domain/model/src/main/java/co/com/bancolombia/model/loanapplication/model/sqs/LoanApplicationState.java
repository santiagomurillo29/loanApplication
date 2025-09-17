package co.com.bancolombia.model.loanapplication.model.sqs;

public class LoanApplicationState {
    private Long id;
    private String state;
    private String email;
    private String type;

    public LoanApplicationState(Long id, String state, String email, String type) {
        this.id = id;
        this.state = state;
        this.email = email;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
