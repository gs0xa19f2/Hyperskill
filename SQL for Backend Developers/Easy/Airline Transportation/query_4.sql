WITH boeing_avg_flight AS (
    SELECT
        'Boeing' AS aircraft_type,
        AVG(TIMESTAMPDIFF(MINUTE, t.time_out, t.time_in)) AS avg_flight_duration,
        COUNT(t.plane_type) AS num_flights
    FROM 
        trip AS t
    WHERE 
        t.plane_type LIKE 'Boeing%'
),
airbus_avg_flight AS (
    SELECT 
        'Airbus' AS aircraft_type,
        AVG(TIMESTAMPDIFF(MINUTE, t.time_out, t.time_in)) AS avg_flight_duration,
        COUNT(t.plane_type) AS num_flights
    FROM 
        trip AS t
    WHERE 
        t.plane_type LIKE 'Airbus%'
)
SELECT
    *
FROM
    boeing_avg_flight
UNION ALL 
SELECT
    *
FROM 
    airbus_avg_flight;
