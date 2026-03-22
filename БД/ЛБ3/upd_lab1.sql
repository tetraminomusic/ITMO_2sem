
CREATE TYPE PRIORITY AS ENUM('Low', 'Medium', 'High');
CREATE TYPE WEATHER AS ENUM('Rainy', 'Windy', 'Foggy', 'Sunny', 'Snowy', 'Cloudy', 'Stormy');

--UPD
CREATE TABLE IF NOT EXISTS POST (
                                    ID_Post SMALLSERIAL PRIMARY KEY,
                                    Post_Name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS AIRPLANE (
                                        ID_Airplane SMALLSERIAL PRIMARY KEY,
                                        Serial_Number CHAR(5) NOT NULL UNIQUE,
                                        Max_Volume INTEGER NOT NULL CHECK (Max_Volume > 0),
                                        Inventor_Name VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS HUMAN (
                                     ID_Person SMALLSERIAL PRIMARY KEY,
                                     Human_Name VARCHAR(30) NOT NULL,
                                     ID_Post SMALLINT NOT NULL, -- Теперь это внешний ключ
                                     ID_License CHAR(5) UNIQUE,
                                     License_Period DATE,
                                     Is_License_Has_Period BOOLEAN DEFAULT TRUE,

                                     FOREIGN KEY (ID_Post) REFERENCES POST(ID_Post) ON UPDATE CASCADE ON DELETE RESTRICT


);

CREATE TABLE IF NOT EXISTS BASE (
                                    ID_Base SMALLSERIAL PRIMARY KEY,
                                    Name_Base VARCHAR(15) NOT NULL UNIQUE,
                                    Has_Runaway BOOLEAN NOT NULL DEFAULT FALSE,
                                    Importance_Level PRIORITY NOT NULL DEFAULT 'Low',
                                    Citizens_Number SMALLINT NOT NULL CHECK (Citizens_Number >= 0)
);

CREATE TABLE IF NOT EXISTS MEETING (
                                       ID_Meeting SMALLSERIAL PRIMARY KEY,
                                       Time_Meeting TIMESTAMP NOT NULL,
                                       Discussion_Subject VARCHAR(15) NOT NULL,
                                       Participants_Count SMALLINT NOT NULL CHECK (Participants_Count > 0),
                                       Taking_Place INT NOT NULL,

                                       FOREIGN KEY (Taking_Place) REFERENCES BASE (ID_Base)
                                           ON UPDATE CASCADE
                                           ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS FLIGHT (
                                      ID_Flight SMALLSERIAL PRIMARY KEY,
                                      TYPE_OF_RISK PRIORITY NOT NULL DEFAULT 'Low',
                                      Arrival_Time TIMESTAMP NOT NULL,
                                      Departure_Time TIMESTAMP NOT NULL,

                                      CHECK (Departure_Time < Arrival_Time),
                                      Arrival_Place INT NOT NULL,
                                      Departure_Place INT NOT NULL,
                                      ID_Pilot SMALLINT NOT NULL,
                                      ID_Airplane SMALLINT NOT NULL,

                                      FOREIGN KEY (Arrival_Place) REFERENCES BASE(ID_Base)
                                          ON UPDATE CASCADE
                                          ON DELETE RESTRICT,

                                      FOREIGN KEY (Departure_Place) REFERENCES BASE(ID_Base)
                                          ON UPDATE CASCADE
                                          ON DELETE RESTRICT,

                                      FOREIGN KEY (ID_Pilot) REFERENCES HUMAN(ID_Person)
                                          ON UPDATE CASCADE
                                          ON DELETE RESTRICT,

                                      FOREIGN KEY (ID_Airplane) REFERENCES AIRPLANE(ID_Airplane)
                                          ON UPDATE CASCADE
                                          ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS FLIGHT_HISTORY (
                                              PRIMARY KEY (ID_Meteorologist, ID_Pilot, ID_Flight),
                                              ID_Meteorologist SMALLINT NOT NULL,
                                              ID_Pilot SMALLINT NOT NULL,
                                              ID_Flight SMALLINT NOT NULL,

                                              FOREIGN KEY (ID_Meteorologist) REFERENCES HUMAN(ID_Person)
                                                  ON UPDATE CASCADE
                                                  ON DELETE RESTRICT,

                                              FOREIGN KEY (ID_Pilot) REFERENCES HUMAN(ID_Person)
                                                  ON UPDATE CASCADE
                                                  ON DELETE RESTRICT,

                                              FOREIGN KEY (ID_Flight) REFERENCES FLIGHT(ID_Flight)
                                                  ON UPDATE CASCADE
                                                  ON DELETE RESTRICT

);

CREATE TABLE IF NOT EXISTS METEOROLOGIST_DATA (
                                                  Air_Pressure SMALLINT NOT NULL CHECK (Air_Pressure > 0),
                                                  Weather WEATHER NOT NULL,
                                                  Speed_Of_Wind SMALLINT NOT NULL CHECK (Speed_Of_Wind >= 0),
                                                  ID_Meteorologist SMALLINT NOT NULL,
                                                  ID_Flight SMALLINT NOT NULL,

                                                  PRIMARY KEY (ID_Meteorologist, ID_Flight),

                                                  FOREIGN KEY (ID_Meteorologist) REFERENCES HUMAN(ID_Person)
                                                      ON UPDATE CASCADE
                                                      ON DELETE RESTRICT,

                                                  FOREIGN KEY (ID_Flight) REFERENCES FLIGHT(ID_Flight)
                                                      ON UPDATE CASCADE
                                                      ON DELETE RESTRICT
);

INSERT INTO POST (Post_Name) VALUES
                                 ('Pilot'),
                                 ('Meteorologist'),
                                 ('Analyst'),
                                 ('Technician'),
                                 ('Dispatcher');

INSERT INTO AIRPLANE (Serial_Number, Max_Volume, Inventor_Name) VALUES
                                ('SN001', 50000, 'Igor Sikorsky'),
                                ('SN002', 12000, 'Kelly Johnson'),
                                ('SN003', 80000, 'Burt Rutan'),
                                ('SN004', 15000, 'W. Messerschmitt'),
                                ('SN005', 45000, 'Marcel Dassault'),
                                ('SN006', 90000, 'Artem Mikoyan'),
                                ('SN007', 25000, 'Hugo Junkers'),
                                ('SN008', 35000, 'Jack Northrop'),
                                ('SN009', 60000, 'Andrei Tupolev'),
                                ('SN010', 55000, 'William Boeing');

INSERT INTO HUMAN (Human_Name, ID_Post, ID_License, License_Period, Is_License_Has_Period) VALUES
                                ('Ivanov Ivan', 1, 'L-101', '2028-12-31', true),
                                ('Anna Reed', 1, 'L-303', '2029-06-15', true),
                                ('John Doe', 1, 'L-505', '2030-01-01', true),
                                ('Alice Cooper', 1, 'L-707', '2027-11-20', true),
                                ('Bob Marley', 1, 'L-808', '2025-05-10', true),
                                ('Petrov Petr', 2, 'M-202', NULL, false),
                                ('Alex Sidorov', 2, 'M-404', '2028-10-10', true),
                                ('Sarah Connor', 2, 'M-909', NULL, false),
                                ('Elena Kuz', 3, 'A-606', NULL, false),
                                ('Viktor Reznov', 4, 'T-111', '2030-12-12', true),
                                ('Svetlana I.', 5, 'D-222', NULL, false),
                                ('Dmitry Petrenko', 4, 'T-333', NULL, false),
                                ('Ghost Riley', 1, 'L-999', '2029-01-01', true);

INSERT INTO BASE (Name_Base, Has_Runaway, Importance_Level, Citizens_Number) VALUES
                                ('North-Base', true, 'High', 150),
                                ('South-Base', true, 'Medium', 45),
                                ('East-Outpost', false, 'Low', 12),
                                ('West-Hub', true, 'High', 300),
                                ('Central-Port', true, 'Medium', 60),
                                ('Island-Point', false, 'Low', 8),
                                ('Secret-Lab', false, 'High', 25),
                                ('Mountain-Top', true, 'Medium', 30);

INSERT INTO MEETING (Time_Meeting, Discussion_Subject, Participants_Count, Taking_Place) VALUES
                                ('2023-11-01 10:00:00', 'Safety', 12, 1),
                                ('2023-11-02 14:00:00', 'Logistics', 5, 4),
                                ('2023-11-03 09:30:00', 'New routes', 8, 2),
                                ('2023-11-04 16:00:00', 'Budget', 15, 1),
                                ('2023-11-05 11:00:00', 'Training', 10, 5),
                                ('2023-11-06 13:00:00', 'Emergency', 20, 3),
                                ('2024-01-10 10:00:00', 'Strategy', 30, 4),
                                ('2024-02-15 09:00:00', 'Fuel Economy', 6, 8);

INSERT INTO FLIGHT (TYPE_OF_RISK, Arrival_Time, Departure_Time, Arrival_Place, Departure_Place, ID_Pilot, ID_Airplane) VALUES
                                ('Low',    '2023-12-01 12:00:00', '2023-12-01 08:00:00', 2, 1, 1, 1),
                                ('Medium', '2023-12-01 20:00:00', '2023-12-01 15:00:00', 4, 2, 2, 2),
                                ('High',   '2023-12-02 10:00:00', '2023-12-02 05:00:00', 1, 4, 3, 3),
                                ('Low',    '2023-12-02 18:00:00', '2023-12-02 14:00:00', 5, 1, 1, 4),
                                ('Medium', '2023-12-03 09:00:00', '2023-12-03 06:00:00', 1, 5, 2, 5),
                                ('High',   '2023-12-03 23:00:00', '2023-12-03 19:00:00', 4, 6, 13, 6),
                                ('Low',    '2024-01-05 15:00:00', '2024-01-05 10:00:00', 8, 4, 4, 7),
                                ('Medium', '2024-01-06 14:00:00', '2024-01-06 11:00:00', 2, 8, 1, 8),
                                ('Low',    '2024-01-10 22:00:00', '2024-01-10 18:00:00', 4, 1, 2, 9),
                                ('High',   '2024-01-12 06:00:00', '2024-01-12 01:00:00', 7, 5, 3, 10);

INSERT INTO FLIGHT_HISTORY (ID_Meteorologist, ID_Pilot, ID_Flight) VALUES
                                (6, 1, 1),
                                (7, 2, 2),
                                (6, 3, 3),
                                (7, 1, 4),
                                (6, 2, 5),
                                (7, 13, 6),
                                (8, 4, 7),
                                (6, 1, 8),
                                (7, 2, 9),
                                (8, 3, 10);

INSERT INTO METEOROLOGIST_DATA (Air_Pressure, Weather, Speed_Of_Wind, ID_Meteorologist, ID_Flight) VALUES
                                (750, 'Sunny', 5, 6, 1),
                                (745, 'Cloudy', 10, 7, 2),
                                (730, 'Stormy', 25, 6, 3),
                                (755, 'Foggy', 2, 7, 4),
                                (748, 'Rainy', 15, 6, 5),
                                (740, 'Snowy', 20, 7, 6),
                                (760, 'Sunny', 3, 8, 7),
                                (742, 'Windy', 30, 6, 8),
                                (750, 'Cloudy', 8, 7, 9),
                                (735, 'Rainy', 18, 8, 10);