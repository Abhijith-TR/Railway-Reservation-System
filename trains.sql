-- Table to store all trains by train number
CREATE TABLE trains (
  train_number VARCHAR(5) PRIMARY KEY
);

-- Trigger to create a new table for each train that is inserted into the trains table 

CREATE OR REPLACE FUNCTION add_train(train_number VARCHAR(5))
RETURNS VOID
AS $$
  DECLARE
  BEGIN
    EXECUTE format(
      '
        INSERT INTO trains values (%L);
      ', train_number
    );

    EXCEPTION
      WHEN unique_violation THEN
        RAISE EXCEPTION 'Train already exists';
  END
$$ LANGUAGE plpgsql;

-- Trigger to create a new table for each instance of a train on a particular date


CREATE OR REPLACE FUNCTION create_train_instance_table()
RETURNS TRIGGER AS $$
  DECLARE

  BEGIN
    EXECUTE FORMAT('
      CREATE TABLE %I (
        pnr VARCHAR(32),
        seat_number VARCHAR(10),
        passenger_name VARCHAR(500), 
        age INTEGER,
        gender VARCHAR(2),
        berth CHAR(2)
      )
    ', TG_TABLE_NAME::text || '-' || NEW.dep_date);

    return NEW;
    EXCEPTION
      WHEN unique_violation THEN
        RAISE EXCEPTION 'Train already exists';
  END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION create_train_table()
RETURNS TRIGGER AS $$
  DECLARE

  BEGIN
    EXECUTE format('
      CREATE TABLE if not exists %I (
        dep_date date PRIMARY KEY,
        ac_seats_total INTEGER,
        sl_seats_total INTEGER,
        ac_seats_left INTEGER,
        sl_seats_left INTEGER
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
    EXCEPTION
      WHEN OTHERS THEN
        RAISE EXCEPTION 'Train already exists';
  END
$$ LANGUAGE plpgsql;

CREATE TRIGGER insert_into_trains_table
  BEFORE INSERT
  ON trains
  FOR EACH ROW
  EXECUTE PROCEDURE create_train_table();
