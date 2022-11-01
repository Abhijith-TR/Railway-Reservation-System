\c postgres
DROP DATABASE IF EXISTS train_system;
\i './Railway-Reservation-System/project.sql'
\i './Railway-Reservation-System/trains.sql'
\i './Railway-Reservation-System/release_trains.sql'
\i './Railway-Reservation-System/book_ticket.sql'