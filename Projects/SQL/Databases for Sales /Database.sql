-- Таблица продаж
CREATE TABLE IF NOT EXISTS sales (
    sale_id INT PRIMARY KEY AUTO_INCREMENT,
    sale_date DATE NOT NULL,
    customer_id INT NOT NULL,
    product_id INT NOT NULL,
    employee_id INT NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers (customer_id),
    FOREIGN KEY (product_id) REFERENCES products (product_id),
    FOREIGN KEY (employee_id) REFERENCES employees (employee_id)
);

-- Таблица производителей
CREATE TABLE IF NOT EXISTS manufacturers ( 
    manufacturer_id INT PRIMARY KEY AUTO_INCREMENT, 
    name VARCHAR(45) NOT NULL,
    country VARCHAR(45) NOT NULL
);

-- Таблица продуктов
CREATE TABLE IF NOT EXISTS products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    manufacturer_id INT NOT NULL,
    model VARCHAR(45) NOT NULL,
    price DECIMAL NOT NULL,
    horsepower INT NOT NULL, 
    fuel_efficiency INT NOT NULL,
    FOREIGN KEY (manufacturer_id) REFERENCES manufacturers (manufacturer_id)
);

-- Таблица инвентаря
CREATE TABLE IF NOT EXISTS inventory (
    product_id INT PRIMARY KEY NOT NULL,
    quantity INT NOT NULL,
    reorder_level INT NOT NULL DEFAULT 2,
    last_inventory_date DATE NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products (product_id)
);

-- Таблица сотрудников
CREATE TABLE IF NOT EXISTS employees (
    employee_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(45) NOT NULL,
    last_name VARCHAR(45) NOT NULL,
    position VARCHAR(45) NOT NULL,
    salary DECIMAL NOT NULL,
    address VARCHAR(45) NOT NULL,
    mobile VARCHAR(45) NOT NULL,
    is_active TINYINT NOT NULL
);

-- Таблица клиентов
CREATE TABLE IF NOT EXISTS customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(45) NOT NULL,
    last_name VARCHAR(45) NOT NULL,
    address VARCHAR(45) NOT NULL,
    city VARCHAR(45) NOT NULL,
    `state` VARCHAR(45) NOT NULL
);
