WITH PassengerIncomePersonal AS (
    SELECT 
        p.ID_psg,
        p.passenger_name,
        SUM(TIMESTAMPDIFF(SECOND, t.time_out, t.time_in) * 0.01) AS passenger_income_dollars
    FROM
        Passenger p
    JOIN 
        Pass_in_trip pit ON pit.ID_psg = p.ID_psg 
    JOIN 
        Trip t ON pit.trip_no = t.trip_no
    GROUP BY
        p.ID_psg,
        p.passenger_name
),
TotalIncome AS (
    SELECT 
        ID_psg,
        passenger_name,
        passenger_income_dollars,
        SUM(passenger_income_dollars) OVER() AS total_income
    FROM 
        PassengerIncomePersonal
    ORDER BY 
        passenger_income_dollars DESC
),
PassengerIncome AS (
    SELECT 
        p.ID_psg,
        p.passenger_name,
        p.passenger_income_dollars,
        ROUND(
            SUM(p.passenger_income_dollars) OVER (ORDER BY p.passenger_income_dollars DESC) / total_income * 100, 
            2
        ) AS cumulative_share_percent,
        CASE 
            WHEN ROUND(SUM(p.passenger_income_dollars) OVER (ORDER BY p.passenger_income_dollars DESC) / total_income * 100, 2) <= 80 THEN 'A'
            WHEN ROUND(SUM(p.passenger_income_dollars) OVER (ORDER BY p.passenger_income_dollars DESC) / total_income * 100, 2) <= 95 THEN 'B'
            ELSE 'C'
        END AS category
    FROM 
        TotalIncome p
)
SELECT 
    ID_psg,
    passenger_name,
    passenger_income_dollars,
    cumulative_share_percent,
    category
FROM 
    PassengerIncome;
