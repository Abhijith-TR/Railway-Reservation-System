CREATE OR REPLACE FUNCTION retrieve_ticket (
    IN spnr TEXT,
    OUT result TEXT
) AS $$
    DECLARE
        rec record;
        tableName TEXT;
    BEGIN
        result := '';

        tableName := SUBSTR(SPLIT_PART(spnr, '-', 1), 3) || 
            '-' || SPLIT_PART(spnr, '-', 2) || 
            '-' || SPLIT_PART(spnr, '-', 3) || 
            '-' || SPLIT_PART(spnr, '-', 4);
        
        FOR rec IN EXECUTE format('SELECT * FROM "%s" WHERE pnr = ''%s''', tableName, spnr) 
        LOOP
            result := result || '(' || rec.seat_number || ', ' || rec.passenger_name || ', ' || rec.berth || ')' || E'\n';
        END LOOP;

    END
$$ LANGUAGE plpgsql;

-- SELECT retrieve_ticket('SL04652-2022-11-01-5');