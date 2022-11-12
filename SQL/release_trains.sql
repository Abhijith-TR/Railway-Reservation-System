-- Function to insert trains into system

CREATE OR REPLACE FUNCTION release_train(
  train_number VARCHAR(5), 
  dep_date DATE, 
  ac_coaches integer, 
  sl_coaches integer
) RETURNS VOID AS
$$ 
  DECLARE

  BEGIN

    EXECUTE format('
      INSERT INTO %I
      VALUES(%L, %L, %L);
    ', train_number || '-ac', dep_date, ac_coaches * 18, ac_coaches * 18);

    EXECUTE format('
      INSERT INTO %I
      VALUES(%L, %L, %L);
    ', train_number || '-sl', dep_date, sl_coaches * 24, sl_coaches*24);

    EXCEPTION 

	    WHEN undefined_table THEN 
        EXECUTE format('
          SELECT add_train(''%s'')
        ', train_number);
        
        EXECUTE format('
          INSERT INTO %I
          VALUES(%L, %L, %L);
        ', train_number || '-ac', dep_date, ac_coaches * 18, ac_coaches * 18);

        EXECUTE format('
          INSERT INTO %I
          VALUES(%L, %L, %L);
        ', train_number || '-sl', dep_date, sl_coaches * 24, sl_coaches*24);

	    WHEN unique_violation THEN 
	      RAISE EXCEPTION 'Train already in booking system';

  END
$$ LANGUAGE plpgsql;
