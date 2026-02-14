
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'priority') THEN
            CREATE TYPE PRIORITY AS ENUM('Low', 'Medium', 'High');
        END IF;
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'weather') THEN
            CREATE TYPE WEATHER AS ENUM('Rainy', 'Windy', 'Foggy', 'Sunny', 'Snowy', 'Cloudy', 'Stormy');
        END IF;
    END $$;

CREATE TABLE IF NOT EXISTS AIRPLANE (
-- PK
                        ID_Airplane SMALLSERIAL PRIMARY KEY,
                        Serial_Number CHAR(5),
                        Max_Volume INTEGER,
                        Inventor_Name VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS HUMAN (
-- PK
                        ID_Person SMALLSERIAL PRIMARY KEY,
                        Human_Name VARCHAR(30),
                        Post VARCHAR(20),
                        ID_Licence CHAR(5),
                        Licence_Period DATE,
                        Is_License_Has_Period BOOLEAN
);

CREATE TABLE IF NOT EXISTS BASE (
-- PK
                        ID_Base SMALLSERIAL PRIMARY KEY,
                        Name_Base VARCHAR(15) UNIQUE,
                        Has_Runaway BOOLEAN,
                        Importance_Level PRIORITY,
                        Citizens_Number SMALLINT
);

CREATE TABLE IF NOT EXISTS MEETING (
-- PK
                        ID_Meeting SMALLSERIAL PRIMARY KEY,
                        Time_Meeting TIMESTAMP,
                        Discussion_Subject VARCHAR(15),
                        Participants_Count SMALLINT,
-- FK
                        Taking_Place VARCHAR(15),

                        FOREIGN KEY (Taking_Place) REFERENCES BASE (Name_Base)
);

CREATE TABLE IF NOT EXISTS FLIGHT (
-- PK
                        TYPE_OF_RISK PRIORITY,
                        ID_Flight SMALLINT PRIMARY KEY,
                        Arrival_Time TIMESTAMP,
                        Departure_Time TIMESTAMP,
-- FK
                        Arrival_Place VARCHAR(15),
                        Departure_Place VARCHAR(15),
                        ID_Pilot SMALLINT,
                        ID_Airplane SMALLINT,

                        FOREIGN KEY (Arrival_Place) REFERENCES BASE(Name_Base),
                        FOREIGN KEY (Departure_Place) REFERENCES BASE(Name_Base),
                        FOREIGN KEY (ID_Pilot) REFERENCES HUMAN(ID_Person),
                        FOREIGN KEY (ID_Airplane) REFERENCES AIRPLANE(ID_Airplane)
);

CREATE TABLE IF NOT EXISTS FLIGHT_HISTORY (
-- FK
                        ID_Meteorologist SMALLINT,
                        ID_Pilot SMALLINT,
                        ID_Flight SMALLINT,

                        FOREIGN KEY (ID_Meteorologist) REFERENCES HUMAN(ID_Person),
                        FOREIGN KEY (ID_Pilot) REFERENCES HUMAN(ID_Person),
                        FOREIGN KEY (ID_Flight) REFERENCES FLIGHT(ID_Flight)
);

CREATE TABLE IF NOT EXISTS METEOROLOGIST_DATA (
-- PK
                        Air_Pressure SMALLINT,
                        Weather WEATHER,
                        Speed_Of_Wind SMALLINT,
-- FK
                        ID_Meteorologist SMALLSERIAL,
                        ID_Flight SMALLSERIAL,

                        FOREIGN KEY (ID_Meteorologist) REFERENCES HUMAN(ID_Person),
                        FOREIGN KEY (ID_Flight) REFERENCES FLIGHT(ID_Flight)
);

INSERT INTO AIRPLANE (Serial_Number, Max_Volume, Inventor_Name) VALUES
                        ('SN001', 50000, 'Igor Sikorsky'),
                        ('SN002', 12000, 'Kelly Johnson'),
                        ('SN003', 80000, 'Burt Rutan'),
                        ('SN004', 15000, 'W. Messerschmitt'),
                        ('SN005', 45000, 'Marcel Dassault'),
                        ('SN006', 90000, 'Artem Mikoyan');

INSERT INTO HUMAN (Human_Name, Post, ID_Licence, Licence_Period, Is_License_Has_Period) VALUES
                        ('Ivanov Ivan', 'Pilot', 'L-101', '2025-12-31', true),
                        ('Petrov Petr', 'Meteorologist', 'M-202', NULL, false),
                        ('Anna Reed', 'Pilot', 'L-303', '2026-06-15', true),
                        ('Alex Sidorov', 'Meteorologist', 'M-404', '2024-10-10', true),
                        ('John Doe', 'Pilot', 'L-505', '2027-01-01', true),
                        ('Elena Kuz', 'Analyst', 'A-606', NULL, false);

INSERT INTO BASE (Name_Base, Has_Runaway, Importance_Level, Citizens_Number) VALUES
                        ('North-Base', true, 'High', 150),
                        ('South-Base', true, 'Medium', 45),
                        ('East-Outpost', false, 'Low', 12),
                        ('West-Hub', true, 'High', 300),
                        ('Central-Port', true, 'Medium', 60),
                        ('Island-Point', false, 'Low', 8);

INSERT INTO MEETING (Time_Meeting, Discussion_Subject, Participants_Count, Taking_Place) VALUES
                        ('2023-11-01 10:00:00', 'Safety', 12, 'North-Base'),
                        ('2023-11-02 14:00:00', 'Logistics', 5, 'West-Hub'),
                        ('2023-11-03 09:30:00', 'New routes', 8, 'South-Base'),
                        ('2023-11-04 16:00:00', 'Budget', 15, 'North-Base'),
                        ('2023-11-05 11:00:00', 'Training', 10, 'Central-Port'),
                        ('2023-11-06 13:00:00', 'Emergency', 20, 'East-Outpost');

INSERT INTO FLIGHT (TYPE_OF_RISK, ID_Flight, Arrival_Time, Departure_Time, Arrival_Place, Departure_Place, ID_Pilot, ID_Airplane) VALUES
                        ('Low', 101, '2023-12-01 12:00:00', '2023-12-01 08:00:00', 'South-Base', 'North-Base', 1, 1),
                        ('Medium', 102, '2023-12-01 20:00:00', '2023-12-01 15:00:00', 'West-Hub', 'South-Base', 3, 2),
                        ('High', 103, '2023-12-02 10:00:00', '2023-12-02 05:00:00', 'North-Base', 'West-Hub', 5, 3),
                        ('Low', 104, '2023-12-02 18:00:00', '2023-12-02 14:00:00', 'Central-Port', 'North-Base', 1, 4),
                        ('Medium', 105, '2023-12-03 09:00:00', '2023-12-03 06:00:00', 'North-Base', 'Central-Port', 3, 5),
                        ('High', 106, '2023-12-03 23:00:00', '2023-12-03 19:00:00', 'West-Hub', 'Island-Point', 5, 6);

INSERT INTO FLIGHT_HISTORY (ID_Meteorologist, ID_Pilot, ID_Flight) VALUES
                        (2, 1, 101),
                        (4, 3, 102),
                        (2, 5, 103),
                        (4, 1, 104),
                        (2, 3, 105),
                        (4, 5, 106);

INSERT INTO METEOROLOGIST_DATA (Air_Pressure, Weather, Speed_Of_Wind, ID_Meteorologist, ID_Flight) VALUES
                        (750, 'Sunny', 5, 2, 101),
                        (745, 'Cloudy', 10, 4, 102),
                        (730, 'Stormy', 25, 2, 103),
                        (755, 'Foggy', 2, 4, 104),
                        (748, 'Rainy', 15, 2, 105),
                        (740, 'Snowy', 20, 4, 106);