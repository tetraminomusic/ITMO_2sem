CREATE OR REPLACE FUNCTION fn_validate_flight()
RETURNS TRIGGER AS $$

DECLARE
    pilot_info RECORD;

BEGIN

    -- Проверка на DELETE
    IF (tg_op = 'DELETE') THEN
        RETURN OLD;
    END IF;

    -- Достаём данные пилота
    SELECT h.*, p.Post_name INTO pilot_info FROM HUMAN h JOIN POST p USING (ID_Post) WHERE ID_Person = NEW.ID_Pilot;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Сотрудник с ID = % не был найден', NEW.ID_PILOT;
    END IF;

    IF (LOWER(pilot_info.Post_Name) != 'pilot') THEN
        RAISE EXCEPTION 'Назначенный сотрудник с ID = % не является пилотом', NEW.ID_PILOT;
    END IF;

    IF (pilot_info.is_license_has_period = TRUE) THEN
        IF (pilot_info.License_period < CURRENT_DATE OR pilot_info.License_period IS NULL) THEN
            RAISE EXCEPTION 'У пилота с ID = % просрочен срок лицензии! (Срок: %)', NEW.ID_Pilot, pilot_info.License_period;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_flight_security
BEFORE INSERT OR UPDATE OR DELETE
ON FLIGHT
FOR EACH ROW
EXECUTE FUNCTION fn_validate_flight();