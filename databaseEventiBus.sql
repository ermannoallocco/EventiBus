DROP DATABASE IF EXISTS busConcerti;
CREATE DATABASE busConcerti;

DROP USER IF EXISTS 'bususer'@'localhost';
CREATE USER 'bususer'@'localhost' IDENTIFIED BY 'bususer';
GRANT ALL privileges ON ncc.* TO 'bususer'@'localhost';

USE busConcerti;

DROP TABLE IF EXISTS genere;
CREATE TABLE genere
(
	nome varchar(20) NOT NULL,
	codice int PRIMARY KEY NOT NULL AUTO_INCREMENT primary key
);

DROP TABLE IF EXISTS artista;
CREATE TABLE artista
(
	codice int NOT NULL AUTO_INCREMENT primary key,
	nome varchar(20) NOT NULL
);

DROP TABLE IF EXISTS haDei;
CREATE TABLE haDei
(
	codiceArtista int NOT NULL,
    codiceGenere int NOT NULL,
    primary key(codiceArtista,codiceGenere),
    foreign key(codiceGenere) references genere(codice),
    foreign key(codiceArtista) references artista(codice)
);

DROP TABLE IF EXISTS luogo;
CREATE TABLE luogo
(
	codice int NOT NULL AUTO_INCREMENT primary key,
	nome varchar(20) NOT NULL,
    regione varchar(20) NOT NULL,
	citta varchar(20) NOT NULL
);

DROP TABLE IF EXISTS bus;
CREATE TABLE bus
(
	capienza int NOT NULL,
    targa varchar(7) NOT NULL,
    kmlitro int not null,
    primary key(targa)
);

DROP TABLE IF EXISTS fermata;
CREATE TABLE fermata
(
	codice int NOT NULL AUTO_INCREMENT,
	regione varchar(20) NOT NULL,
	citta varchar(20) NOT NULL,
	nome varchar(50) NOT NULL,
    primary key(codice)
);

DROP TABLE IF EXISTS dista;
CREATE TABLE dista
(
	codiceFermata int NOT NULL,
    codiceLuogo int NOT NULL,
	km int NOT NULL,
    primary key(codiceFermata,codiceLuogo),
    foreign key(codiceLuogo) references luogo(codice),
    foreign key(codiceFermata) references fermata(codice)
);


DROP TABLE IF EXISTS trasporto;
CREATE TABLE trasporto
(
	codice int NOT NULL AUTO_INCREMENT,
	codiceFermata int NOT NULL,
	targa varchar(7) NOT NULL,
    benzinaLitro float not null,
    primary key(codice),
    foreign key(codiceFermata) references fermata(codice),
    foreign key(targa) references bus(targa)
);

DROP TABLE IF EXISTS evento;
CREATE TABLE evento
(
	codice int NOT NULL AUTO_INCREMENT,
	codiceArtista  int NOT NULL,
	codiceLuogo int NOT NULL,
    dataConcerto date not null,
    primary key(codice),
    foreign key(codiceArtista) references artista(codice),
    foreign key(codiceLuogo) references luogo(codice)
);

DROP TABLE IF EXISTS cliente;
CREATE TABLE cliente
(
	username varchar(20) NOT NULL,
    userpass varchar(20) NOT NULL,
	nome varchar(50) NOT NULL,
	cognome varchar(50) NOT NULL,
	telefono varchar(10) NOT NULL,
	dataNascita date not null,
    primary key(username)
);

DROP TABLE IF EXISTS privato;
CREATE TABLE privato
(
	username varchar(20) NOT NULL,
    primary key(username),
    foreign key(username) references cliente(username) 
);

DROP TABLE IF EXISTS azienda;
CREATE TABLE azienda
(
	username varchar(20) NOT NULL,
    primary key(username),
    nome varchar(20) NOT NULL,
    n_dipendenti int NOT NULL,
    foreign key(username) references cliente(username)
);


DROP TABLE IF EXISTS biglietto;
CREATE TABLE biglietto
(
	codice int NOT NULL AUTO_INCREMENT,
    username varchar(20) NOT NULL,
    codiceEvento int NOT NULL,
    codiceTrasporto int NOT NULL,
    dataAcquisto date not null,
    n_partecipanti int not null,
    primary key(codice,username),
    foreign key(username) references cliente(username),
    foreign key(codiceEvento) references evento(codice),
    foreign key(codiceTrasporto) references trasporto(codice)
);

/*popolamento
dati genere*/

/*popolamento
dati account cliente, privato e azienda*/

insert into cliente (username,userpass,nome, cognome, telefono, dataNascita) 
values  ('fipo','fipo','Filippo','Buontempi','3771234556','1980-11-23'),
		('debbo','debbo','Debora','Lavigna','3771234552','1990-12-25'),
		('tessa','tessa','Teresa','Statesa','3771234056','2001-02-05'),
		('ernia','ernia','Ernesto','Cipponi','3771234506','1999-01-30');


insert into privato (username) values ('fipo');
insert into privato (username) values ('debbo');
insert into privato (username) values ('ernia');

insert into azienda (username, nome, n_dipendenti) values ('tessa','Re dei Cuori','20');

insert into genere (nome) values ('pop');
insert into genere (nome) values ('rock');
insert into genere (nome) values ('hard rock');

/*popolamento
dati artista*/

insert into artista (nome) values ('AC DC');
insert into artista (nome) values ('Daft Punk');

insert into haDei ( codiceArtista, codiceGenere) 
values  ('1','2'),
		('1','3'),
        ('2','1'),
        ('2','2');

/*popolamento
dati luogo concerto*/

insert into luogo (nome,regione, citta) values ('Arenile Di Bagnoli','Campania','Napoli');
insert into luogo (nome,regione, citta) values ('Stadio Olimpico','Lazio','Roma');

/*popolamento
dati conducente e bus*/

insert into bus (targa, capienza,kmlitro) values ('AB654HJ','70',20);
insert into bus (targa, capienza,kmlitro) values ('CB678IL','50',15);
/*popolamento
dati fermata*/

insert into fermata ( regione, citta, nome) 
values  ('campania','pagani','piazza Sant Alfonso'),
		('lazio','roma','Aereoporto Ciampino');

insert into dista ( codiceFermata, codiceLuogo, km) 
values  ('1','2','274'),
		('2','2','24'),
        ('1','1','55'),
        ('2','1','206');

/*popolamento
dati eventi e trasporti*/

insert into trasporto ( codiceFermata, targa, benzinaLitro) 
values  ('1','AB654HJ','2.10'),
		('2','CB678IL','2.15');
        
insert into evento (codiceArtista, codiceluogo, dataConcerto) values ('1','1','2023-03-30');
insert into evento (codiceArtista, codiceluogo, dataConcerto) values ('2','2','2023-05-12');
insert into evento (codiceArtista, codiceluogo, dataConcerto) values ('1','1','2023-07-15');
insert into evento (codiceArtista, codiceluogo, dataConcerto) values ('2','2','2024-12-6');   



/*popolamento
dati prenotazione*/

insert into biglietto (username, codiceEvento, codiceTrasporto,dataAcquisto,n_partecipanti) values ('fipo','1','1','2022-07-06','2');
insert into biglietto (username, codiceEvento, codiceTrasporto,dataAcquisto,n_partecipanti) values ('tessa','2','2','2022-05-09','10');
insert into biglietto (username, codiceEvento, codiceTrasporto,dataAcquisto,n_partecipanti) values ('ernia','1','1','2022-01-23','9');
insert into biglietto (username, codiceEvento, codiceTrasporto,dataAcquisto,n_partecipanti) values ('debbo','2','2','2022-02-14','13');
