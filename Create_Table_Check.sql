CREATE TABLE trains(
  train_number VARCHAR(5),
  train_name VARCHAR(30)
);

CREATE TRIGGER create_train_table
  BEFORE INSERT
  ON trains
  FOR EACH ROW
  EXECUTE PROCEDURE create_train_table();

-- A table name with numbers will always have to be put inside quotes
-- Try and find a way around this
CREATE OR REPLACE FUNCTION create_train_table() 
RETURNS TRIGGER AS $$
  DECLARE

  BEGIN
    EXECUTE format('
      CREATE TABLE if not exists %I
        (
          ac_seats integer,
          sleeper_seats integer,
          dep_date date
        )
    ', 'T' || NEW.train_number);

    -- EXECUTE format('
    --   CREATE TRIGGER 
    -- ');
    return NEW;
  END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION create_train_date_table()
RETURNS TRIGGER AS $$
  DECLARE

  BEGIN

  END
$$ LANGUAGE plpgsql;