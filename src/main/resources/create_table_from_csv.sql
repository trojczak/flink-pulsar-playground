CREATE TABLE Users
(
    id         INT,
    name       STRING,
    birth_year INT
) WITH (
    'connector' = 'filesystem',
    'path' = 'file:///home/rtk/programming/tme/projects/flink-pulsar-playground/data/users.csv',
    'format' = 'csv'
)