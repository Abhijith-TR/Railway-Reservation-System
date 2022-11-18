CREATE OR REPLACE FUNCTION retrieve_ticket (
    IN spnr TEXT,
    OUT result TEXT
) AS $$
    DECLARE
        rec record;
        tableName TEXT;
        tdate TEXT;
        tname TEXT;
    BEGIN
        result := '';
        tdate :=  SPLIT_PART(spnr, '-', 2) || 
            '-' || SPLIT_PART(spnr, '-', 3) || 
            '-' || SPLIT_PART(spnr, '-', 4);

        tname := SUBSTR(SPLIT_PART(spnr, '-', 1), 3);
        tableName :=  tname || '-' || tdate;
        
        FOR rec IN EXECUTE format('SELECT * FROM "%s" WHERE pnr = ''%s''', tableName, spnr) 
        LOOP
            result := result || '('|| tname ||', '|| rec.seat_number || ', ' || rec.passenger_name || ', ' || rec.berth || ', ' || tdate || ')' || E'\n';
        END LOOP;

    END
$$ LANGUAGE plpgsql;

-- SELECT retrieve_ticket('SL04652-2022-11-01-5');