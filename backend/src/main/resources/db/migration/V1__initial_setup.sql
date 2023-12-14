create table customer
(
  id bigserial primary key,
  name TEXT not null,
  email TEXT not null,
  age int not null
);