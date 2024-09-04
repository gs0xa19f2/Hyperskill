SELECT 
    CONCAT(t.town_from, '-', t.town_to) AS route,
    AVG(TIMESTAMPDIFF(MINUTE, t.time_out, t.time_in)) as avg_flight_duration,
    COUNT(pit.ID_psg) as total_passengers,
    SUM(TIMESTAMPDIFF(SECOND, t.time_out, t.time_in) / 100) as total_income
FROM 
    Passenger AS p
INNER JOIN 
    Pass_in_trip AS pit ON p.ID_psg = pit.ID_psg
INNER JOIN 
    Trip AS t ON t.trip_no = pit.trip_no
GROUP BY 
    route
ORDER BY 
    total_income DESC;
