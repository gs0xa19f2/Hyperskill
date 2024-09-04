WITH AvgDuration AS (
    SELECT 
        ac.company_name,
        t.town_from AS departure_city,
        t.town_to AS arrival_city,
        AVG(TIMESTAMPDIFF(MINUTE, t.time_out, t.time_in)) AS avg_flight_duration
    FROM 
        Trip t
    JOIN 
        Airline_company ac ON t.ID_comp = ac.ID_comp
    GROUP BY 
        ac.company_name, t.town_from, t.town_to
),
RankedRoutes AS (
    SELECT 
        company_name,
        departure_city,
        arrival_city,
        avg_flight_duration,
        ROW_NUMBER() OVER (PARTITION BY company_name ORDER BY avg_flight_duration DESC) AS rn
    FROM 
        AvgDuration
)
SELECT 
    company_name,
    departure_city,
    arrival_city,
    avg_flight_duration
FROM 
    RankedRoutes
WHERE 
    rn <= 2;
