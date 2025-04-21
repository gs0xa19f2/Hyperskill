-- Список пассажиров с количеством перелетов более одного

SELECT 
    p.passenger_name, 
    COUNT(t.trip_no) AS num_flights,
    ac.company_name
FROM passenger AS p
INNER JOIN pass_in_trip AS pit
    ON p.id_psg = pit.id_psg
INNER JOIN trip AS t
    ON t.trip_no = pit.trip_no
INNER JOIN airline_company AS ac 
    ON ac.id_comp = t.id_comp
GROUP BY 
    p.passenger_name, ac.company_name
HAVING 
    num_flights > 1;
