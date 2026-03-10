SELECT COUNT(DISTINCT "ЧЛВК_ИД") AS "Количество троечников"
FROM
    "Н_ВЕДОМОСТИ"
WHERE
    "ЧЛВК_ИД" IN (
        SELECT
            "ЧЛВК_ИД"
        FROM
            "Н_ВЕДОМОСТИ"
        GROUP BY
            "ЧЛВК_ИД"
        HAVING
           COUNT(CASE WHEN "ОЦЕНКА" = '3' THEN 1 END) > 0
           AND COUNT(CASE WHEN "ОЦЕНКА" IN ('2', '4', '5') THEN 1 END) = 0
           AND COUNT(CASE WHEN "ОЦЕНКА" = 'незачёт' THEN 1 END) = 0);