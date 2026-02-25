
CREATE TYPE PRIORITY AS ENUM('Low', 'Medium', 'High');
CREATE TYPE WEATHER AS ENUM('Rainy', 'Windy', 'Foggy', 'Sunny', 'Snowy', 'Cloudy', 'Stormy');

CREATE TABLE IF NOT EXISTS AIRPLANE (
                            ID_Airplane SMALLSERIAL PRIMARY KEY,
                            Serial_Number CHAR(5) NOT NULL UNIQUE,
                            Max_Volume INTEGER NOT NULL CHECK (Max_Volume > 0),
                            Inventor_Name VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS HUMAN (
                            ID_Person SMALLSERIAL PRIMARY KEY,
                            Human_Name VARCHAR(30) NOT NULL,
                            Post VARCHAR(20) NOT NULL,
                            ID_Licence CHAR(5) UNIQUE,
                            Licence_Period DATE,
                            Is_License_Has_Period BOOLEAN DEFAULT TRUE,

                            CONSTRAINT chk_licence_logic CHECK (
                            (Is_License_Has_Period = TRUE AND Licence_Period IS NOT NULL AND Licence_Period > CURRENT_DATE) OR
                            (Is_License_Has_Period = FALSE AND Licence_Period IS NULL)
                            )
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

-- Вставка данных
INSERT INTO AIRPLANE (Serial_Number, Max_Volume, Inventor_Name) VALUES
                        ('SN001', 50000, 'Igor Sikorsky'),
                        ('SN002', 12000, 'Kelly Johnson'),
                        ('SN003', 80000, 'Burt Rutan'),
                        ('SN004', 15000, 'W. Messerschmitt'),
                        ('SN005', 45000, 'Marcel Dassault'),
                        ('SN006', 90000, 'Artem Mikoyan');

INSERT INTO HUMAN (Human_Name, Post, ID_Licence, Licence_Period, Is_License_Has_Period) VALUES
                        ('Ivanov Ivan', 'Pilot', 'L-101', '2028-12-31', true), 
                        ('Petrov Petr', 'Meteorologist', 'M-202', NULL, false),
                        ('Anna Reed', 'Pilot', 'L-303', '2029-06-15', true),  
                        ('Alex Sidorov', 'Meteorologist', 'M-404', '2028-10-10', true), 
                        ('John Doe', 'Pilot', 'L-505', '2030-01-01', true),  
                        ('Elena Kuz', 'Analyst', 'A-606', NULL, false);

INSERT INTO BASE (Name_Base, Has_Runaway, Importance_Level, Citizens_Number) VALUES
                        ('North-Base', true, 'High', 150),
                        ('South-Base', true, 'Medium', 45),
                        ('East-Outpost', false, 'Low', 12),
                        ('West-Hub', true, 'High', 300),
                        ('Central-Port', true, 'Medium', 60),
                        ('Island-Point', false, 'Low', 8);

INSERT INTO MEETING (Time_Meeting, Discussion_Subject, Participants_Count, Taking_Place) VALUES
                        ('2023-11-01 10:00:00', 'Safety', 12, 1),
                        ('2023-11-02 14:00:00', 'Logistics', 5, 4),
                        ('2023-11-03 09:30:00', 'New routes', 8, 2),
                        ('2023-11-04 16:00:00', 'Budget', 15, 1),
                        ('2023-11-05 11:00:00', 'Training', 10, 5),
                        ('2023-11-06 13:00:00', 'Emergency', 20, 3);


INSERT INTO FLIGHT (TYPE_OF_RISK, Arrival_Time, Departure_Time, Arrival_Place, Departure_Place, ID_Pilot, ID_Airplane) VALUES
                        ('Low', '2023-12-01 12:00:00', '2023-12-01 08:00:00', 2, 1, 1, 1),
                        ('Medium', '2023-12-01 20:00:00', '2023-12-01 15:00:00', 4, 2, 3, 2),
                        ('High', '2023-12-02 10:00:00', '2023-12-02 05:00:00', 1, 4, 5, 3),
                        ('Low', '2023-12-02 18:00:00', '2023-12-02 14:00:00', 5, 1, 1, 4),
                        ('Medium', '2023-12-03 09:00:00', '2023-12-03 06:00:00', 1, 5, 3, 5),
                        ('High', '2023-12-03 23:00:00', '2023-12-03 19:00:00', 4, 6, 5, 6);


INSERT INTO FLIGHT_HISTORY (ID_Meteorologist, ID_Pilot, ID_Flight) VALUES
                        (2, 1, 1),
                        (4, 3, 2),
                        (2, 5, 3),
                        (4, 1, 4),
                        (2, 3, 5),
                        (4, 5, 6);

INSERT INTO METEOROLOGIST_DATA (Air_Pressure, Weather, Speed_Of_Wind, ID_Meteorologist, ID_Flight) VALUES
                        (750, 'Sunny', 5, 2, 1),
                        (745, 'Cloudy', 10, 4, 2),
                        (730, 'Stormy', 25, 2, 3),
                        (755, 'Foggy', 2, 4, 4),
                        (748, 'Rainy', 15, 2, 5),
                        (740, 'Snowy', 20, 4, 6);