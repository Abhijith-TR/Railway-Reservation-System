-- DROP DATABASE train_system;
CREATE DATABASE train_system;
\c train_system;

CREATE TABLE trains (
  train_number VARCHAR(5) PRIMARY KEY
);

-- Trigger to create a new table for each train that is inserted into the trains table 

CREATE OR REPLACE FUNCTION create_train_table()
RETURNS TRIGGER AS $$
  DECLARE

  BEGIN
    EXECUTE format('
      CREATE TABLE if not exists %I (
        dep_date date PRIMARY KEY,
        ac_seats_total integer,
        sleeper_seats_total integer,
        ac_seats_left integer,
        sleeper_seats_left integer
      )
    ', NEW.train_number);

    EXECUTE format('
        CREATE TRIGGER insert_into_specific_train
        BEFORE INSERT 
        ON "' || NEW.train_number ||'"
        FOR EACH ROW
        EXECUTE PROCEDURE create_train_instance_table();
    ');

    return NEW;
  END
$$ LANGUAGE plpgsql;
 
CREATE TRIGGER insert_into_trains_table
  BEFORE INSERT
  ON trains
  FOR EACH ROW
  EXECUTE PROCEDURE create_train_table();

-- Trigger to create a new table for each instance of a train on a particular date

-- CREATE TRIGGER insert_into_specific_train
--   BEFORE INSERT 
--   ON || 
--   FOR EACH ROW
--   EXECUTE PROCEDURE create_train_instance_table(train_number VARCHAR(5));

CREATE OR REPLACE FUNCTION create_train_instance_table()
RETURNS TRIGGER AS $$
  DECLARE

  BEGIN
    EXECUTE FORMAT('
      CREATE TABLE %I (
        pnr VARCHAR(16),
        seat_number VARCHAR(10),
        passenger_name VARCHAR(500), 
        age INTEGER,
        gender VARCHAR(2)
      )
    ', TG_TABLE_NAME::text || '-' || NEW.dep_date);

    return NEW;
  END
$$ LANGUAGE plpgsql;

-- Function to insert trains into system

CREATE OR REPLACE PROCEDURE release_train(
  train_number VARCHAR(5), 
  dep_date DATE, 
  ac_coaches integer, 
  sl_coaches integer) AS
$$
  DECLARE

  BEGIN
    EXECUTE format('
      INSERT INTO %I
      VALUES(%L, %L, %L, %L, %L);
    ', train_number, dep_date, ac_coaches * 18, sl_coaches * 24, ac_coaches * 18, sl_coaches*24);
  EXCEPTION
    RAISE EXCEPTION 'Train has already been released on this date';
  END
$$ LANGUAGE plpgsql;