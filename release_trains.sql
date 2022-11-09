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
      VALUES(%L, %L, %L, %L, %L);
    ', train_number, dep_date, ac_coaches * 18, sl_coaches * 24, ac_coaches * 18, sl_coaches*24);


    EXCEPTION 
	    WHEN undefined_table THEN 
	      RAISE EXCEPTION 'Train not found';
	    WHEN unique_violation THEN 
	      RAISE EXCEPTION 'Train already in booking system';

  END
$$ LANGUAGE plpgsql;
