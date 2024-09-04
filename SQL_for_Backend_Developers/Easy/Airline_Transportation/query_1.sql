ALTER TABLE Pass_in_trip 
MODIFY COLUMN trip_date DATE;

UPDATE Pass_in_trip
SET trip_date = CAST(trip_date as DATE);

SELECT trip_date
    FROM Pass_in_trip;
