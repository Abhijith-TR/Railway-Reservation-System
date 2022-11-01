-- Function to book tickets

CREATE OR REPLACE PROCEDURE book_tickets (
  train_number VARCHAR(5),
  depdate DATE,
  preference CHAR(2),
  names text[],
  ages integer[],
  genders CHAR(1)[]
) AS
$$
  DECLARE

    arr_len INTEGER := array_length(names, 1);
    num_left INTEGER := 0;
    total_seats INTEGER := 0;
    start_seat INTEGER := 0;
    all_values TEXT := '';
  
  BEGIN
    EXECUTE format(
    '
      SELECT '||preference||'_seats_left, '||preference||'_seats_total FROM %I 
      WHERE dep_date = %L;
    ', train_number, depdate
    ) INTO num_left, total_seats;

    start_seat := total_seats - num_left +1;

    IF num_left < arr_len THEN

      RAISE EXCEPTION 'Not enough seats left';

    ELSE 

      EXECUTE format('
        UPDATE %I SET %s_seats_left = %s_seats_left -%s 
        WHERE dep_date = %L;
      ', train_number, preference, preference, arr_len, depdate);

    END IF;
    
    FOR ind IN 1..arr_len
    LOOP 
      IF all_values <> '' THEN all_values := all_values || ',';
      END IF;
      all_values := all_values || format (
        '(
            ''%s%s-%s-%s'',
            %L, %L,
            %s, %L
          )
        ', preference, train_number, depdate, start_seat, 
        start_seat+ind-1, names[ind],
        ages[ind], genders[ind]
      );
    END LOOP;

    EXECUTE format(
      '
        INSERT INTO "%s-%s" VALUES %s;
      ', train_number, depdate, all_values 
    );
    -- RAISE NOTICE '% \n %',names[1], names[2];

    RAISE NOTICE 'Booked tickets please check';
  END
$$ LANGUAGE plpgsql;
