CREATE INDEX customer_sales_product
ON sales(customer_id, product_id); 

CREATE VIEW sales_summary AS 
SELECT
    p.model,
    SUM(s.quantity) AS total_sold
FROM 
    sales s 
JOIN 
    products p ON s.product_id = p.product_id
GROUP BY 
    p.model;


SELECT * FROM sales_summary;
