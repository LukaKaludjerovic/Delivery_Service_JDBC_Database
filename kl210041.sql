
CREATE TABLE [Admin]
( 
	[Username]           varchar(100)  NOT NULL 
)
go

CREATE TABLE [City]
( 
	[IdCity]             integer  IDENTITY  NOT NULL ,
	[Name]               varchar(100)  NOT NULL ,
	[PostCode]           integer  NOT NULL 
)
go

CREATE TABLE [Courier]
( 
	[NumberDelivered]    integer  NOT NULL ,
	[Profit]             decimal(10,3)  NOT NULL ,
	[Status]             integer  NOT NULL ,
	[Vehicle]            integer  NOT NULL ,
	[Username]           varchar(100)  NOT NULL 
)
go

CREATE TABLE [District]
( 
	[IdDistrict]         integer  IDENTITY  NOT NULL ,
	[Name]               varchar(100)  NOT NULL ,
	[XCoord]             decimal(10,3)  NOT NULL ,
	[YCoord]             decimal(10,3)  NOT NULL ,
	[City]               integer  NOT NULL 
)
go

CREATE TABLE [Offer]
( 
	[IdOffer]            integer  IDENTITY  NOT NULL ,
	[Package]            integer  NOT NULL ,
	[Percentage]         decimal(10,3)  NOT NULL ,
	[Courier]            varchar(100)  NOT NULL 
)
go

CREATE TABLE [Package]
( 
	[IdPackage]          integer  IDENTITY  NOT NULL ,
	[Origin]             integer  NOT NULL ,
	[Destination]        integer  NOT NULL ,
	[Type]               integer  NOT NULL ,
	[Weight]             decimal(10,3)  NOT NULL ,
	[Status]             integer  NOT NULL ,
	[Price]              decimal(10,3)  NULL ,
	[Time]               DATETIME  NULL ,
	[Courier]            varchar(100)  NULL ,
	[User]               varchar(100)  NOT NULL 
)
go

CREATE TABLE [Request]
( 
	[Vehicle]            integer  NOT NULL ,
	[Username]           varchar(100)  NOT NULL 
)
go

CREATE TABLE [User]
( 
	[Forename]           varchar(100)  NOT NULL ,
	[Surname]            varchar(100)  NOT NULL ,
	[Username]           varchar(100)  NOT NULL ,
	[Password]           varchar(100)  NOT NULL ,
	[NumberSent]         integer  NOT NULL 
)
go

CREATE TABLE [Vehicle]
( 
	[IdVehicle]          integer  IDENTITY  NOT NULL ,
	[Registration]       varchar(100)  NOT NULL ,
	[FuelType]           integer  NOT NULL ,
	[Consumption]        decimal(10,3)  NOT NULL 
)
go

ALTER TABLE [Admin]
	ADD CONSTRAINT [XPKAdmin] PRIMARY KEY  CLUSTERED ([Username] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XPKCity] PRIMARY KEY  CLUSTERED ([IdCity] ASC)
go

ALTER TABLE [Courier]
	ADD CONSTRAINT [XPKCourier] PRIMARY KEY  CLUSTERED ([Username] ASC)
go

ALTER TABLE [District]
	ADD CONSTRAINT [XPKDistrict] PRIMARY KEY  CLUSTERED ([IdDistrict] ASC)
go

ALTER TABLE [Offer]
	ADD CONSTRAINT [XPKOffer] PRIMARY KEY  CLUSTERED ([IdOffer] ASC)
go

ALTER TABLE [Package]
	ADD CONSTRAINT [XPKPackage] PRIMARY KEY  CLUSTERED ([IdPackage] ASC)
go

ALTER TABLE [Request]
	ADD CONSTRAINT [XPKRequest] PRIMARY KEY  CLUSTERED ([Vehicle] ASC,[Username] ASC)
go

ALTER TABLE [User]
	ADD CONSTRAINT [XPKUser] PRIMARY KEY  CLUSTERED ([Username] ASC)
go

ALTER TABLE [Vehicle]
	ADD CONSTRAINT [XPKVehicle] PRIMARY KEY  CLUSTERED ([IdVehicle] ASC)
go


ALTER TABLE [Admin]
	ADD CONSTRAINT [R_2] FOREIGN KEY ([Username]) REFERENCES [User]([Username])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Courier]
	ADD CONSTRAINT [R_6] FOREIGN KEY ([Vehicle]) REFERENCES [Vehicle]([IdVehicle])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Courier]
	ADD CONSTRAINT [R_3] FOREIGN KEY ([Username]) REFERENCES [User]([Username])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [District]
	ADD CONSTRAINT [R_1] FOREIGN KEY ([City]) REFERENCES [City]([IdCity])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Offer]
	ADD CONSTRAINT [R_11] FOREIGN KEY ([Package]) REFERENCES [Package]([IdPackage])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Offer]
	ADD CONSTRAINT [R_10] FOREIGN KEY ([Courier]) REFERENCES [Courier]([Username])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Package]
	ADD CONSTRAINT [R_8] FOREIGN KEY ([Origin]) REFERENCES [District]([IdDistrict])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Package]
	ADD CONSTRAINT [R_9] FOREIGN KEY ([Destination]) REFERENCES [District]([IdDistrict])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Package]
	ADD CONSTRAINT [R_12] FOREIGN KEY ([Courier]) REFERENCES [Courier]([Username])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Package]
	ADD CONSTRAINT [R_7] FOREIGN KEY ([User]) REFERENCES [User]([Username])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Request]
	ADD CONSTRAINT [R_5] FOREIGN KEY ([Vehicle]) REFERENCES [Vehicle]([IdVehicle])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Request]
	ADD CONSTRAINT [R_13] FOREIGN KEY ([Username]) REFERENCES [User]([Username])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go
