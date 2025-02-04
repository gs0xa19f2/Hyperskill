WITH passenger_income_personal AS (
    SELECT 
        p.id_psg,
        p.passenger_name,
        SUM(TIMESTAMPDIFF(SECOND, t.time_out, t.time_in) * 0.01) AS passenger_income_dollars
    FROM
        passenger AS p
    JOIN 
        pass_in_trip AS pit ON pit.id_psg = p.id_psg 
    JOIN 
        trip AS t ON pit.trip_no = t.trip_no
    GROUP BY
        p.id_psg,
        p.passenger_name
),
total_income AS (
    SELECT 
        id_psg,
        passenger_name,
        passenger_income_dollars,
        SUM(passenger_income_dollars) OVER() AS total_income
    FROM 
        passenger_income_personal
    ORDER BY 
        passenger_income_dollars DESC
),
passenger_income AS (
    SELECT 
        p.id_psg,
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
        total_income p
)
SELECT 
    id_psg,
    passenger_name,
    passenger_income_dollars,
    cumulative_share_percent,
    category
FROM 
    passenger_income;
