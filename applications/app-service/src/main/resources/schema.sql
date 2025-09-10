DROP TABLE IF EXISTS state CASCADE;
DROP TABLE IF EXISTS loan_type CASCADE;
DROP TABLE IF EXISTS loan_application CASCADE;


CREATE TABLE IF NOT EXISTS state (
    id_state BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS loan_type (
    id_loan_type BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    min_amount NUMERIC(20,2) NOT NULL,
    max_amount NUMERIC(20,2) NOT NULL,
    interest_rate NUMERIC(5,2) NOT NULL,
    auto_validation BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS loan_application (
    id_loan_application BIGSERIAL PRIMARY KEY,
    amount NUMERIC(20,2) NOT NULL,
    term INT NOT NULL,
    email VARCHAR(150) NOT NULL,
    id_state BIGINT NOT NULL DEFAULT 1,
    id_loan_type BIGINT NOT NULL,
    FOREIGN KEY (id_state) REFERENCES state(id_state),
    FOREIGN KEY (id_loan_type) REFERENCES loan_type(id_loan_type)
);

INSERT INTO state (name, description) VALUES
('PENDING', 'Loan application pending review'),
('APPROVED', 'Loan application approved'),
('REJECTED', 'Loan application rejected');

INSERT INTO loan_type (name, min_amount, max_amount, interest_rate, auto_validation) VALUES
('Personal Loan', 5000.00, 10000.00, 12.50, TRUE),
('Car Loan', 1000.00, 20000.00, 10.00, FALSE),
('Mortgage', 5000.00, 50000.00, 8.50, FALSE);