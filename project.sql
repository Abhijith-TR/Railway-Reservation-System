-- DROP DATABASE train_system;
CREATE DATABASE train_system;
\c train_system;

CREATE TABLE trains (
  train_number VARCHAR(5) PRIMARY KEY,
  train_name VARCHAR(50)
);

-- Trigger to create a new table for each train that is inserted into the trains table 

CREATE TRIGGER insert_into_trains_table
  BEFORE INSERT
  ON trains
  FOR EACH ROW
  EXECUTE PROCEDURE create_train_table();

CREATE OR REPLACE FUNCTION create_train_table()
RETURNS TRIGGER AS $$
  DECLARE

  BEGIN
    EXECUTE format('
      CREATE TABLE if not exists %I (
        dep_date date,
        ac_seats_total integer,
        sleeper_seats_total integer,
        ac_seats_left integer,
        sleeper_seats_left integer
      )
    ', NEW.train_number);

    

    return NEW;
  END
$$ LANGUAGE plpgsql;

-- Trigger to create a new table for each instance of a train on a particular date

CREATE TRIGGER insert_into_specific_train
  BEFORE INSERT 
  ON %I
  FOR EACH ROW
  EXECUTE PROCEDURE create_train_instance_table();

CREATE OR REPLACE FUNCTION create_train_instance_table()
RETURNS TRIGGER AS $$
  DECLARE

  BEGIN
    EXECUTE FORMAT('
      CREATE TABLE %I (
        pnr VARCHAR(16),
        passenger_name VARCHAR(500), 
        number_of_seats INTEGER,
        starting_seat VARCHAR(6)
      )
    ', )
    return NEW;
  END
$$ LANGUAGE plpgsql;