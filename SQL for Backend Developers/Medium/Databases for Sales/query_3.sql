SELECT 
    CONCAT_WS(" ", e.first_name, e.last_name) AS employee_name,
    e.position, 
    DATE_FORMAT(s.sale_date, '%M %Y') AS month_year,
    CASE
        WHEN SUM(s.total_price) / total_month.total_sum * 100 < 5 THEN 0
        WHEN SUM(s.total_price) / total_month.total_sum * 100 BETWEEN 5 AND 10 THEN 2000
        WHEN SUM(s.total_price) / total_month.total_sum * 100 BETWEEN 10 AND 20 THEN 5000
        WHEN SUM(s.total_price) / total_month.total_sum * 100 BETWEEN 20 AND 30 THEN 10000
        WHEN SUM(s.total_price) / total_month.total_sum * 100 BETWEEN 30 AND 40 THEN 15000
        ELSE 25000
    END AS employee_bonus
FROM
    sales s
JOIN
    employees e ON s.employee_id = e.employee_id
JOIN
    (
        SELECT 
            DATE_FORMAT(sale_date, '%Y-%m') AS month,
            SUM(total_price) AS total_sum
        FROM
            sales 
        GROUP BY
            DATE_FORMAT(sale_date, '%Y-%m')
    ) AS total_month ON DATE_FORMAT(s.sale_date, '%Y-%m') = total_month.month
WHERE
    e.position = 'Sales Associate'  GROUP BY
    s.employee_id,
    employee_name,
    e.position,
    month_year,
    total_month.total_sum
ORDER BY
    employee_bonus;
