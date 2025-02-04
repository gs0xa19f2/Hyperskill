WITH avg_duration AS (
    SELECT 
        ac.company_name,
        t.town_from AS departure_city,
        t.town_to AS arrival_city,
        AVG(TIMESTAMPDIFF(MINUTE, t.time_out, t.time_in)) AS avg_flight_duration
    FROM 
        trip AS t
    JOIN 
        airline_company AS ac ON t.id_comp = ac.id_comp
    GROUP BY 
        ac.company_name, t.town_from, t.town_to
),
ranked_routes AS (
    SELECT 
        company_name,
        departure_city,
        arrival_city,
        avg_flight_duration,
        ROW_NUMBER() OVER (PARTITION BY company_name ORDER BY avg_flight_duration DESC) AS rn
    FROM 
        avg_duration
)
SELECT 
    company_name,
    departure_city,
    arrival_city,
    avg_flight_duration
FROM 
    ranked_routes
WHERE 
    rn <= 2;
