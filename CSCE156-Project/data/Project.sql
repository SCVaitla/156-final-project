-- Delete tables if they already exist
drop table if exists Purchase;
drop table if exists Product;
drop table if exists Invoice;
drop table if exists Email;
drop table if exists PersonCustomer;
drop table if exists Customer;
drop table if exists Person;
drop table if exists Address;
drop table if exists Country;
drop table if exists State;

-- Build the needed tables with the relevant fields
create table State (
	state_id int not null unique primary key auto_increment,
    name varchar(100) not null
);

create table Country (
	country_id int not null unique primary key auto_increment,
    name varchar(100) not null
);

create table Address (
	address_id int not null unique primary key auto_increment,
    street varchar(100),
    city varchar(100),
    zip varchar(15),
    state_id int,
    country_id int,
    foreign key (state_id) references State(state_id),
    foreign key (country_id) references Country(country_id),
    constraint Unique_Address unique (street, city, zip, state_id) -- Ensures no duplicate addresses
);

create table Person (
  person_id int not null unique primary key auto_increment,
  personCode varchar(45) not null unique,
  firstName varchar(100) not null,
  lastName varchar(100) not null,
  address_id int not null,
  foreign key (address_id) references Address(address_id)
);

create table Customer (
  customer_id int not null unique primary key auto_increment,
  customerCode varchar(45) not null unique,
  customerName varchar(100) not null,
  customerType varchar(10) not null, -- Indicates account type: business = B, personal = P
  address_id int not null,
  foreign key (address_id) references Address(address_id),
  constraint Customer_type check (customerType = "B" or customerType = "P") -- Ensures customers are only Business or Personal accounts
);

-- PersonCustomer associates a customer account with its primary contact (a person)
create table PersonCustomer (
	personCustomer_id int not null unique primary key auto_increment,
    person_id int not null,
    customer_id int not null unique, -- each customer has only one primary contact (person) so customer_id is unique
    foreign key (person_id) references Person(person_id),
    foreign key (customer_id) references Customer(customer_id)
);
-- DROP Table Email;
create table Email (
  email_id int not null unique primary key auto_increment,
  emailName varchar(100),
  person_id int not null,
  foreign key (person_id) references Person(person_id)
);

create table Invoice (
  invoice_id int not null unique primary key auto_increment,
  invoiceCode varchar(45) not null unique,
  person_id int not null,
  customer_id int not null,
  foreign key (person_id) references Person(person_id),
  foreign key (customer_id) references Customer(customer_id)
);

create table Product (
  product_id int not null unique primary key auto_increment,
  productCode varchar(45) not null unique,
  productType varchar(10) not null, -- rental (R), repair (F), concession (C), towing (T)
  label varchar(100) not null,
  -- These 7 fields are associated with a specific type of product. The field is left null if the product does not use it.
  dailyCost float, -- Used with: rental (R)
  deposit float, -- Used with: rental (R)
  cleaningFee float, -- Used with: rental (R)
  partsCost float, -- Used with: repair (F)
  hourlyLaborCost float, -- Used with: repair (F)
  unitCost float, -- Used with: concession (C)
  costPerMile float, -- Used with: towing (T)
  constraint Product_type check (productType in ("R", "F", "C", "T")), -- Ensures products are only of type R, F, C, or T
  constraint Unique_product unique (productType, label, dailyCost, deposit, cleaningFee, partsCost, hourlyLaborCost, unitCost, costPerMile) -- Ensures no duplicate products
);

-- Purchase table associates each purchase of a Product with the specified Invoice
create table Purchase (
  purchase_id int not null unique primary key auto_increment,
  invoice_id int not null,
  product_id int not null,
  -- These 5 fields are associated with a specific type of product. The field is left null if the product does not use it.
  daysRented float, -- Used with: rental (R)
  hoursWorked float, -- Used with: repair (F)
  quantity int, -- Used with: concession (C)
  associatedRepair int, -- Used with: concession (C), (only if a repair is associated with same invoice_id)
  milesTowed float, -- Used with: towing (T)
  foreign key (invoice_id) references Invoice(invoice_id),
  foreign key (product_id) references Product(product_id),
  foreign key (associatedRepair) references Product(product_id),
  constraint Unique_Purchase unique (invoice_id, product_id, daysRented, hoursWorked, quantity, associatedRepair, milesTowed) -- Ensures no duplicate purchases on an invoice
);


-- SELECT * FROM Purchase;
-- SELECT * FROM Product;
-- SELECT * FROM Invoice;
-- SELECT * FROM Email;
-- SELECT * FROM PersonCustomer;
-- SELECT * FROM Customer;
-- SELECT * FROM Person;
-- SELECT * FROM Address;
-- SELECT * FROM State;
-- SELECT * FROM Country;

