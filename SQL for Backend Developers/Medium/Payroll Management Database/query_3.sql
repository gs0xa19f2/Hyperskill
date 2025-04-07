CREATE PROCEDURE EmployeeTotalPay(
    IN first_name VARCHAR(45),
    IN last_name VARCHAR(45),
    IN total_hours INT,
    IN normal_hours INT,
    IN overtime_rate FLOAT,
    IN max_overtime_pay FLOAT,
    OUT total_pay FLOAT
)
BEGIN
    DECLARE hourly_rate FLOAT;
    DECLARE job_type VARCHAR(45);
    
    SELECT j.hourly_rate, j.type INTO hourly_rate, job_type
    FROM employees e
    JOIN jobs j ON e.job_id = j.id
    WHERE e.first_name = first_name AND e.last_name = last_name;
    
    IF job_type = 'Part Time' THEN
        SET total_pay = hourly_rate * total_hours;
    ELSE
        IF total_hours <= normal_hours THEN
            SET total_pay = hourly_rate * total_hours;
        ELSE
            SET total_pay = hourly_rate * normal_hours + 
                  LEAST((total_hours - normal_hours) * hourly_rate * overtime_rate, max_overtime_pay);
        END IF;
    END IF;
END;

CALL EmployeeTotalPay('Philip', 'Wilson', 2160, 2080, 1.5, 6000, @philip_pay);
CALL EmployeeTotalPay('Daisy', 'Diamond', 2100, 2080, 1.5, 6000, @daisy_pay);


DELIMITER //

CREATE FUNCTION TaxOwed(taxable_income FLOAT) RETURNS FLOAT
BEGIN
    DECLARE tax_owed FLOAT;
    
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

SELECT 
    ROUND(TaxOwed(@philip_pay), 1) AS 'Philip Wilson',
    ROUND(TaxOwed(@daisy_pay), 1) AS 'Daisy Diamond';
