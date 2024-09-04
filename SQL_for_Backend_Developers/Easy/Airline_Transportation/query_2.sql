SELECT 
    p.passenger_name, 
    COUNT(t.trip_no) as num_flights,
    ac.company_name
FROM Passenger AS p
INNER JOIN Pass_in_trip AS pit
    ON p.ID_psg = pit.ID_psg
INNER JOIN Trip as t
    ON t.trip_no = pit.trip_no
INNER JOIN Airline_company as ac 
    ON ac.ID_comp = t.ID_comp
GROUP BY
    p.passenger_name, ac.company_name
HAVING
    num_flights > 1;
