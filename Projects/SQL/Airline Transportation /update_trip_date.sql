-- Изменение формата даты в таблице Pass_in_trip
ALTER TABLE pass_in_trip
MODIFY COLUMN trip_date DATE;

-- Обновление данных для приведения формата
UPDATE pass_in_trip
SET trip_date = CAST(trip_date AS DATE);

-- Проверка изменений
SELECT trip_date
FROM pass_in_trip;
