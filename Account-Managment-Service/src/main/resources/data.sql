CREATE TABLE CUSTOMER_ACCOUNT (
  id INT PRIMARY KEY AUTO_INCREMENT,
  account_created TIMESTAMP,
  last_name VARCHAR(50),
  account_balance DECIMAL(10,2),
  account_number VARCHAR(20),
  account_status VARCHAR(20),
  account_type VARCHAR(20),
  address VARCHAR(100),
  first_name VARCHAR(50)
);

CREATE TABLE address (
id INT PRIMARY KEY AUTO_INCREMENT,
  street_address VARCHAR(255),
  suburb VARCHAR(255),
  city VARCHAR(255)
);



