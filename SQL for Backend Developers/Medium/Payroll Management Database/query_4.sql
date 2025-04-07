DELIMITER //

CREATE PROCEDURE EmployeePay(
    IN first_name VARCHAR(45),
    IN last_name VARCHAR(45),
    IN total_hours INT,
    IN normal_hours INT,
    IN overtime_rate FLOAT(8, 2),
    IN max_overtime_pay FLOAT(8, 2),
    OUT base_pay FLOAT(8, 2),
    OUT total_pay FLOAT(8, 2)
)
BEGIN
    DECLARE hourly_rate FLOAT(8, 2);
    DECLARE job_type VARCHAR(45);
    
    SELECT j.hourly_rate, j.type INTO hourly_rate, job_type
    FROM employees e
    JOIN jobs j ON e.job_id = j.id
    WHERE e.first_name = first_name AND e.last_name = last_name;
    
    IF job_type = 'Part Time' OR total_hours <= normal_hours THEN
        SET base_pay = hourly_rate * total_hours;
        SET total_pay = hourly_rate * total_hours;
    ELSE
        SET base_pay = hourly_rate * normal_hours;
        SET total_pay = hourly_rate * normal_hours + 
              LEAST((total_hours - normal_hours) * hourly_rate * overtime_rate, max_overtime_pay);
    END IF;
END //

DELIMITER ;

DELIMITER //

CREATE FUNCTION TaxOwed(taxable_income FLOAT(8, 2)) RETURNS FLOAT(8, 2)
BEGIN
    DECLARE tax_owed FLOAT(8, 2);
    
    SET tax_owed = CASE
        WHEN taxable_income <= 11000 THEN taxable_income * 0.10
        WHEN taxable_income <= 44725 THEN 1100 + (taxable_income - 11000) * 0.12
        WHEN taxable_income <= 95375 THEN 5147 + (taxable_income - 44725) * 0.22
        WHEN taxable_income <= 182100 THEN 16290 + (taxable_income - 95375) * 0.24
        WHEN taxable_income <= 231250 THEN 37104 + (taxable_income - 182100) * 0.32
        WHEN taxable_income <= 578125 THEN 52832 + (taxable_income - 231250) * 0.35
        ELSE 174238.25 + (taxable_income - 578125) * 0.37
    END;
    
    RETURN tax_owed;
END //

DELIMITER ;

CREATE TEMPORARY TABLE employee_hours (
    employee_name VARCHAR(100),
    hours_worked INT
);

INSERT INTO employee_hours VALUES
    ("Dixie Herda", 2095),
    ("Stephen West", 2091),
    ("Philip Wilson", 2160), 
    ("Robin Walker", 2083),
    ("Antoinette Matava", 2115),
    ("Courtney Walker", 2206),
    ("Gladys Bosch", 2090);

DELIMITER //

CREATE PROCEDURE PayrollReport(IN department_name VARCHAR(45))
BEGIN
        CREATE TEMPORARY TABLE IF NOT EXISTS temp_report (
        full_names VARCHAR(100),
        base_pay FLOAT(8, 2),
        overtime_pay FLOAT(8, 2),
        total_pay FLOAT(8, 2),
        tax_owed FLOAT(8, 2),
        net_income FLOAT(8, 2)
    );

        TRUNCATE TABLE temp_report;

        INSERT INTO temp_report (full_names, base_pay, overtime_pay, total_pay, tax_owed, net_income)
    SELECT 
        CONCAT_WS(' ', e.first_name, e.last_name),
        j.hourly_rate * LEAST(eh.hours_worked, 2080),
        CASE 
            WHEN j.type = 'Full Time' AND eh.hours_worked > 2080 
            THEN LEAST((eh.hours_worked - 2080) * j.hourly_rate * 1.5, 6000)
            ELSE 0 
        END,
        (j.hourly_rate * LEAST(eh.hours_worked, 2080)) + 
        CASE 
            WHEN j.type = 'Full Time' AND eh.hours_worked > 2080 
            THEN LEAST((eh.hours_worked - 2080) * j.hourly_rate * 1.5, 6000)
            ELSE 0 
        END,
        TaxOwed((j.hourly_rate * LEAST(eh.hours_worked, 2080)) + 
               CASE 
                   WHEN j.type = 'Full Time' AND eh.hours_worked > 2080 
                   THEN LEAST((eh.hours_worked - 2080) * j.hourly_rate * 1.5, 6000)
                   ELSE 0 
               END),
        (j.hourly_rate * LEAST(eh.hours_worked, 2080)) + 
        CASE 
            WHEN j.type = 'Full Time' AND eh.hours_worked > 2080 
            THEN LEAST((eh.hours_worked - 2080) * j.hourly_rate * 1.5, 6000)
            ELSE 0 
        END - TaxOwed((j.hourly_rate * LEAST(eh.hours_worked, 2080)) + 
                    CASE 
                        WHEN j.type = 'Full Time' AND eh.hours_worked > 2080 
                        THEN LEAST((eh.hours_worked - 2080) * j.hourly_rate * 1.5, 6000)
                        ELSE 0 
                    END)
    FROM 
        employees e
    JOIN departments d ON e.department_id = d.id
    JOIN jobs j ON e.job_id = j.id
    JOIN employee_hours eh ON CONCAT_WS(' ', e.first_name, e.last_name) = eh.employee_name
    WHERE 
        d.name = department_name;

        SELECT * FROM temp_report ORDER BY net_income DESC;

        DROP TEMPORARY TABLE temp_report;
END //

DELIMITER ;

CALL PayrollReport("City Ethics Commission");
