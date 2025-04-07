CREATE PROCEDURE GetEmployeesByDept(
    IN department_name VARCHAR(45)
)
BEGIN 
    SELECT 
        e.first_name,
        e.last_name,
        j.title AS job_title
    FROM 
        employees e
    JOIN 
        departments d ON e.department_id = d.id 
    JOIN 
        jobs j ON e.job_id = j.id
    WHERE
        d.name = department_name
    ORDER BY 
        e.first_name;
END;

CALL GetEmployeesByDept("Office of Finance");
