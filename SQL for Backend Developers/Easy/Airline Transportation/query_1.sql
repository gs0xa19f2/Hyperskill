ALTER TABLE pass_in_trip
MODIFY COLUMN trip_date DATE;

UPDATE pass_in_trip
SET trip_date = CAST(trip_date AS DATE);

SELECT trip_date
FROM pass_in_trip;
