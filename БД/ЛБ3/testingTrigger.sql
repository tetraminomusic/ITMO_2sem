--1
-- Проверка на несуществующий ID
INSERT INTO FLIGHT (TYPE_OF_RISK, Arrival_Time, Departure_Time, Arrival_Place, Departure_Place, ID_Pilot, ID_Airplane)
VALUES ('Low', '2024-12-01 12:00:00', '2024-12-01 10:00:00', 1, 2, 999, 1);
---Ожидаемый результат, что такого типа не существует


--2
-- Предположим, ID 6 — это метеоролог
INSERT INTO FLIGHT (TYPE_OF_RISK, Arrival_Time, Departure_Time, Arrival_Place, Departure_Place, ID_Pilot, ID_Airplane)
VALUES ('Low', '2024-12-01 12:00:00', '2024-12-01 10:00:00', 1, 2, 6, 1);

-- Ожидаемый результат: Ошибка "Назначенный сотрудник с ID = 6 не является пилотом"


--3
-- Сделаем лицензию просроченной
UPDATE HUMAN SET license_period = '2020-01-01' WHERE ID_Person = 1;

-- Пробуем назначить его на рейс
INSERT INTO FLIGHT (TYPE_OF_RISK, Arrival_Time, Departure_Time, Arrival_Place, Departure_Place, ID_Pilot, ID_Airplane)
VALUES ('Low', '2024-12-01 12:00:00', '2024-12-01 10:00:00', 1, 2, 1, 1)
-- Ожидаемый результат: Ошибка "У пилота с ID = 1 просрочен срок лицензии!"


--4
INSERT INTO FLIGHT (TYPE_OF_RISK, Arrival_Time, Departure_Time, Arrival_Place, Departure_Place, ID_Pilot, ID_Airplane)
VALUES ('Medium', '2025-01-01 15:00:00', '2025-01-01 10:00:00', 1, 2, 2, 2);

-- Ожидаемый результат: Запись успешно добавлена.


--5
-- Возьмем существующий рейс (например, ID 1) и сменим пилота на ID 6 (метеоролог)
UPDATE FLIGHT SET ID_Pilot = 6 WHERE ID_Flight = 1;
-- Ожидаемый результат: Ошибка "Назначенный сотрудник с ID = 6 не является пилотом"


