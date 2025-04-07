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

SELECT 
    ROUND(@philip_pay, 1) AS 'Philip Wilson',
    ROUND(@daisy_pay, 1) AS 'Daisy Diamond';
