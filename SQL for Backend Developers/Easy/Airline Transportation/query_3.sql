SELECT 
    CONCAT(t.town_from, '-', t.town_to) AS route,
    AVG(TIMESTAMPDIFF(MINUTE, t.time_out, t.time_in)) AS avg_flight_duration,
    COUNT(pit.id_psg) AS total_passengers,
    SUM(TIMESTAMPDIFF(SECOND, t.time_out, t.time_in) / 100) AS total_income
FROM 
    passenger AS p
INNER JOIN 
    pass_in_trip AS pit ON p.id_psg = pit.id_psg
INNER JOIN 
    trip AS t ON t.trip_no = pit.trip_no
GROUP BY 
    route
ORDER BY 
    total_income DESC;
