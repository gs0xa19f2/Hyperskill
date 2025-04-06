SELECT 
    p.model,
    p.price,
    SUM(s.total_price) AS total_sale_per_model,
    i.quantity AS inventory_per_model,
    SUM(s.total_price) / i.quantity AS sales_inventory_ratio
FROM
    products p
JOIN
    sales s ON p.product_id = s.product_id
JOIN 
    inventory i ON p.product_id = i.product_id
GROUP BY 
    p.model, p.price, i.quantity
ORDER BY
    sales_inventory_ratio DESC;
