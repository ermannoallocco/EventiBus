package eventiBus;
import java.sql.*;
import java.sql.SQLException;
import java.util.Scanner;

import connessione.Connection;

public class Main {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		Scanner input=new Scanner(System.in);
		Connection con = new Connection();
		con.createConnection();
		
		int risposta;

		do {

			System.out.println("[-1] Termina programma");
			System.out.println("[1] Un cliente prenota e visualizza il prezzo del bus per un dato artista in una determinata data");
			System.out.println("[2] Dato un artista, le possibili date di concerti con citta e regione");
			System.out.println("[3] Una azienda prenota e visualizza il prezzo del bus per il numero di dipendenti");
			System.out.println("[4] Visualizza il nome delle fermate possibili dei conducenti");
			System.out.println("[5] Dato un luogo per un concerto, gli artisti che hanno delle date");
			System.out.println("[6] Visualizzazione per ogni cliente, il numero di ordini effettuati");
			System.out.println("[7] Visualizzare per ogni artista il numero dei clienti che li hanno scelti");
			System.out.println("[8] Visualizzare per ogni artista il proprio genere");
			System.out.println("[9] Visualizzare il nome del manager e il numero di dipendenti di una azienda che hanno effettuato una prenotazione");
			System.out.println("[10] Visualizzare il nome, cognome e numero di telefono dei clienti che hanno effettuato una prenotazione");
			System.out.println("[11] Cancellazione di una prenotazione di un dato cliente");
			System.out.println("[12] Visualizzazione di tutte le fermate possibili");
		
			System.out.println("Inserisci un numero:");
			risposta=input.nextInt();
			input.nextLine();
			Statement query=con.createStatement();
			ResultSet set=null;
			
			if(risposta==-1) {
				System.out.println("Arrivederci");

			}
			else if(risposta==1) {
				// Non è possibile affidare un ordine ad un ristorante se la coda degli ordini supera il suo numero massimo di prenotazioni possibili.
				/*
				 * la coda degli ordini di un ristorante è piena quando select count(*) from ordine where statoOrdine='ordinato'and codRistorante=
				 * codRistoranteAttuale
				 * è uguale al numero di max prenotazioni di un ristorante
				 * la registrazione di un ordine va vietata in questo caso*/

				//verifica esistenza cliente
				System.out.println("Inserisci il nome del cliente che vuole effettuare l'ordine: ");
				String nomeCliente=input.nextLine();
				System.out.println("Inserisci il cognome del cliente che vuole effettuare l'ordine: ");
				String cognomeCliente=input.nextLine();

				set=query.executeQuery("select * from cliente where nome='"+nomeCliente+"' and cognome='"+cognomeCliente+"'");

				if(set.next())//il cliente esiste
				{
					String cf=set.getString("cf");//verrà inserito all'interno della relazione ordine
					set=query.executeQuery("select * from ristorante");

					//stampa della lista dei ristoranti + scelta del ristorante da parte dell'utente
					while(set.next()) {
						System.out.println(set.getInt("codice")+": "+set.getString("nome"));
					}

					System.out.println("Inserisci il codice del ristorante da cui vuoi ordinare: ");
					int codRistorante=input.nextInt();
					input.nextLine();

					//scelgo il codice per questo ordine:
					//il numero dell'ordine sarà il massimo numero di ordine odierno + 1
					GregorianCalendar data =new GregorianCalendar();
					int giorno=data.get(Calendar.DAY_OF_MONTH);
					int mese=data.get(Calendar.MONTH)+1;
					int anno=data.get(Calendar.YEAR);
					String dataOdierna=anno+"-"+mese+"-"+giorno;
					int codiceOrdine=0;

					set=query.executeQuery("select max(numeroOrdine)as maxNum from ordine where dataOrdine='"+dataOdierna+"' and codRistorante="+codRistorante);
					if(set.next()) {
						codiceOrdine=set.getInt("maxNum")+1;
					}

					//valutazione della coda degli ordini del ristorante:
					int maxPrenotazioni;
					int prenotazioniAttuali;

					set=query.executeQuery("select * from ristorante where codice=" +codRistorante);
					set.next();
					maxPrenotazioni=set.getInt("numMaxPrenotazioni");
					set=query.executeQuery("select count(*) as somma from ordine where statoOrdine='ordinato' and cod"
							+ "Ristorante=" +codRistorante );
					set.next();
					prenotazioniAttuali=set.getInt("somma");

					if(maxPrenotazioni==prenotazioniAttuali) {
						System.out.println("La coda di ordini del ristorante scelto è piena");
						continue;
					}

					//inseriamo una riga in ordine
					System.out.println("Vuoi inserire una descrizione all'ordine? [1]SI [-1]NO");
					int descrizioneBool=input.nextInt();
					String descrizione="";
					input.nextLine();//da fare dopo ogni nextInt

					if(descrizioneBool!=-1) {
						System.out.println("Inserisci la descrizione:");
						descrizione=input.nextLine();
					}

					System.out.println("Inserisci la via in cui consegnare:");
					String via=input.nextLine();
					System.out.println("Inserisci il numero civico:");
					String nCivico=input.nextLine();
					System.out.println("Inserisci il CAP:");
					String cap=input.nextLine();
					System.out.println("Inserisci il nominativo:");
					String nominativo=input.nextLine();

					//per facilità l'orario presunto è dato dall'ora corrente + 15
					int ora=data.get(Calendar.HOUR_OF_DAY);
					int minuti=data.get(Calendar.MINUTE);

					minuti+=15;
					//controllo minuti>=60 -> +1 ora
					if(minuti>=60)
					{
						ora+=1;
						minuti=minuti-60;
					}
					if(ora>=24)
					{
						ora=ora-24;
					}

					String orarioPresunto=ora+":"+minuti+":"+"00";

					// la insert into può o non può contenere la descrizione
					if(descrizione=="") {
						query.executeUpdate("INSERT INTO ordine (numeroOrdine, dataOrdine, codRistorante, CfDipendente, CfRider, CfCliente, statoOrdine, descrizione, via, cap, numeroCivico, orarioPresunto, orarioEffettivo, nominativoRitiro)"+
								"values ("+codiceOrdine+",'"+dataOdierna+"',"+codRistorante+",NULL,NULL,'"+cf+"','ordinato',NULL,'"+via+"','"+cap+"','"+nCivico+"','"+orarioPresunto+"',NULL,'"+nominativo+"')");
					}
					else {
						query.executeUpdate("INSERT INTO ordine (numeroOrdine, dataOrdine, codRistorante, CfDipendente, CfRider, CfCliente, statoOrdine, descrizione, via, cap, numeroCivico, orarioPresunto, orarioEffettivo, nominativoRitiro)"+
								"values ("+codiceOrdine+",'"+dataOdierna+"',"+codRistorante+",NULL,NULL,'"+cf+"','ordinato','"+descrizione+"','"+via+"','"+cap+"','"+nCivico+"','"+orarioPresunto+"',NULL,'"+nominativo+"')");

					}

					//stampa menu del ristorante scelto
					System.out.println("Menu del ristorante:");
					set=query.executeQuery("select * from menu where codRistorante="+codRistorante);
					while(set.next()) {
						System.out.println("Codice: "+set.getInt("numMenu")+"\n"+
								"Nome: "+set.getString("nome")+"\n"+
								"Descrizione: "+set.getString("descrizione")+"\n"+
								"Prezzo: "+set.getDouble("prezzo")+"€\n\n");
					}
					//popolazione della tabella composizione

					ArrayList<Integer> menuOrdinati=new ArrayList<Integer>();
					boolean controllo=false;
					/*se l'utente inserisce più di una volta lo stesso codMenu si violerà il vincolo di chiave primaria.
					 * conservo in un array tutti i codici di menu digitati dall'utente vietando l'inserimento più di una volta
					 dello stesso codice*/

					int n_ordini=0;
					while(true) {
						controllo=false;
						System.out.println("Inserisci il codice del menu: [-1 per terminare l'ordinazione]");
						int codMenu=input.nextInt();
						input.nextLine();
						if(codMenu==-1 && n_ordini>0) {
							break;
						}
						else if(codMenu==-1) {
							continue;
						}
						else {
							for(int i=0; i<menuOrdinati.size(); i++) {
								if(codMenu==menuOrdinati.get(i)) {
									System.out.println("Hai già ordinato questo piatto");
									controllo=true;
									break;
								}
							}
						}
						if(controllo) {
							continue;
						}
						menuOrdinati.add(codMenu);
						n_ordini++;
						System.out.println("Inserisci la quantità: ");
						int quantita=input.nextInt();
						input.nextLine();
						query.executeUpdate("INSERT INTO composizione (numOrdine, dataOrd, codRistorante, codRistoranteMenu, numMenu, quantita)"+
											"VALUES("+codiceOrdine+",'"+dataOdierna+"',"+codRistorante+","+codRistorante+","+codMenu+","+quantita+");");

					}
				}
				else//il cliente non esiste
				{
					System.out.println("Il cliente non esiste");
				}
			}
			else if(risposta==2) {
				//per consegnare un ordine bisogna prima assicurarsi che esista l'ordine da consegnare
				/*bisogna scegliere un dipendente o un rider da usare per consegnare l'ordine
				 *rv3:chi consegna più ordini nello stesso giorno deve avere per ogni ordine consegnato
				 *un orario di consegna diverso
				 *settare lo stato dell'ordine a consegnato*/

					/*select cf, nome, cognome from rider where cf in (
						select cfRider from impiego where NumSocieta in (
							select numSocieta from svolgimento where numServizio in(
								select codServizio from utilizzo where codRistorante= ...... )))
					questa query seleziona i rider che possono effettuare una consegna per un dato ristorante
					i rider in questione sono tutti quelli che lavorano per società di delivery che adempiono
					servizi di delivery per un ristorante
										*/


				System.out.println("Inserisci il numero dell'ordine: ");
				int numeroOrdine=input.nextInt();
				input.nextLine();
				System.out.println("Inserisci la data dell'ordine in questo formato: AAAA-MM-GG");
				String dataOrdine=input.nextLine();
				System.out.println("Inserisci il codice del ristorante da cui è partito l'ordine");
				int codRistorante=input.nextInt();
				input.nextLine();
				set=query.executeQuery("select * from ordine where numeroOrdine=" + numeroOrdine + " and dataOrdine='" + dataOrdine +
						"' and statoOrdine='ordinato' and codRistorante="+codRistorante);

				if(set.next()) //l'ordine esiste
				{
					System.out.println("Da chi è stato consegnato l'ordine? [1]Rider [2]Dipendente");
					int scelta=input.nextInt();

					if(scelta==1)
					{
						//seleziono tutti i rider che possono consegnare l'ordine
						set=query.executeQuery("select cf, nome, cognome from rider where cf in ( "
								+ "	select cfRider from impiego where NumSocieta in ( "
								+ "		select numSocieta from svolgimento where numServizio in( "
								+ "			select codServizio from utilizzo where codRistorante="+codRistorante+")))");

						ArrayList<String> riders = new ArrayList<String>();
						int cont=0;

						//stampo i rider affiancati da un codice di selezione
						while(set.next())
						{
							riders.add(set.getString("cf"));
							System.out.println(cont+": "+set.getString("nome")+" "+set.getString("cognome"));
							cont++;
						}

						if(cont==0) {
							System.out.println("Nessun rider può consegnare ordini per questo ristorante");
							continue;
						}
						System.out.println("Inserisci il numero del rider che ha effettuato la consegna: ");
						int nRider=input.nextInt();
						input.nextLine();

						String cfRider;
						cfRider=riders.get(nRider);

						System.out.println("Inserisci l'orario effettivo della consegna nel formato: HH:MM :");
						String orarioEffettivo;
						orarioEffettivo=input.nextLine();

						//verifica orario
						set=query.executeQuery("select orarioEffettivo from ordine "
								+"where dataOrdine='"+dataOrdine+"' and CfRider='"+cfRider+"' and statoOrdine='consegnato'");
						String orario;

						Boolean validitàOrario=true;
						while(set.next())
						{
							orario=set.getString("orarioEffettivo");
							if(orarioEffettivo.compareTo(orario)==0)
							{
								System.out.println("Orario di consegna già presente per il Rider");
								validitàOrario=false;
								break;
							}
						}

						if(!validitàOrario) {
							continue;
						}
						else
						{
							query.executeUpdate("UPDATE ordine SET cfRider='"+cfRider+"',statoOrdine='consegnato',orarioEffettivo='"+orarioEffettivo+"' "
									+"where numeroOrdine="+numeroOrdine+" AND dataOrdine='"+dataOrdine+"' and codRistorante="+codRistorante);
						}


					}
					else if(scelta==2)
					{
						ResultSet set2=query.executeQuery("select cf, nome, cognome "
								+ "from dipendente "
								+ "where codRistorante="+codRistorante+"");

						ArrayList<String> dipendenti = new ArrayList<String>();
						int cont=0;

						while(set2.next())
						{
							dipendenti.add(set2.getString("cf"));
							System.out.println(cont+": "+set2.getString("nome")+" "+set2.getString("cognome"));
							cont++;
						}

						System.out.println("Inserisci il numero del dipendente che ha effettuato la consegna: ");
						int nDipendente=input.nextInt();
						input.nextLine();

						String cfDipendente;
						cfDipendente=dipendenti.get(nDipendente);

						System.out.println("Inserisci l'orario effettivo della consegna nel formato: HH:MM :");
						String orarioEffettivo;
						orarioEffettivo=input.nextLine();

						//verifica orario
						set2=query.executeQuery("select orarioEffettivo from ordine "
								+"where dataOrdine='"+dataOrdine+"' and CfDipendente='"+cfDipendente+"' and statoOrdine='consegnato'");
						String orario;

						Boolean validitàOrario=true;
						while(set2.next())
						{
							orario=set2.getString("orarioEffettivo");
							if(orarioEffettivo.compareTo(orario)==0)
							{
								System.out.println("Orario di consegna già presente per il Dipendente");
								validitàOrario=false;
								break;
							}
						}

						if(!validitàOrario) {
							continue;
						}
						else
						{
							query.executeUpdate("UPDATE ordine SET cfDipendente='"+cfDipendente+"',statoOrdine='consegnato',orarioEffettivo='"+orarioEffettivo+"' "
									+"where numeroOrdine="+numeroOrdine+" AND dataOrdine='"+dataOrdine+"' and codRistorante="+codRistorante);
						}
					}
					else
					{
						System.out.println("Scelta non valida");
					}
				}
				else {
					System.out.println("L'ordine non esiste");
				}

			}
			else if(risposta==3) {

				/*
				 * un ordine si può effettuare se il numero di massime prenotazioni è > del numero di ordini attuali*/

				System.out.println("Inserisci il codice del ristorante dal quale vuoi ordinare:");
				set=query.executeQuery("select * from ristorante");
				//stampa della lista dei ristoranti + scelta del ristorante da parte dell'utente
				while(set.next()) {
					System.out.println(set.getInt("codice")+": "+set.getString("nome"));
				}
				int codRistorante=input.nextInt();
				input.nextLine();

				//valutazione della coda degli ordini del ristorante:
				int maxPrenotazioni;
				int prenotazioniAttuali;
				set=query.executeQuery("select * from ristorante where codice=" +codRistorante);
				set.next();
				maxPrenotazioni=set.getInt("numMaxPrenotazioni");
				set=query.executeQuery("select count(*) as somma from ordine where statoOrdine='ordinato' and cod"
						+ "Ristorante=" +codRistorante );
				set.next();
				prenotazioniAttuali=set.getInt("somma");

				if(maxPrenotazioni==prenotazioniAttuali) {
					System.out.println("La coda di ordini del ristorante scelto è piena");
				}
				else {
					System.out.println("E' possibile effettuare un ordine");
				}
			}
			else if(risposta==4) {
				/*
				 * i ristoranti disponibili all'accettazione di un ordine sono quei ristoranti
				 * la cui coda degli ordini ancora non è piena*/

				set=query.executeQuery("select codice, nome from ristorante rr "+
						"where numMaxPrenotazioni>( "+
						"select count(*) as nOrdini "+
						"from ristorante r join ordine o on r.codice=o.codRistorante "+
						"where statoOrdine='ordinato' and r.codice=rr.codice )");

				while(set.next()) {
					System.out.println(set.getString("nome"));
				}
			}
			else if(risposta==5) {
				/*
				 * un rider deve aver consegnato almeno un ordine al cliente che vuole valutarlo*/

				System.out.println("Inserisci il nome del cliente che vuole effettuare la valutazione: ");
				String nomeCliente=input.nextLine();
				System.out.println("Inserisci il cognome del cliente che vuole effettuare la valutazione: ");
				String cognomeCliente=input.nextLine();
				set=query.executeQuery("select * from cliente where nome='"+nomeCliente+"' and cognome='"+cognomeCliente+"'");
				set.next();
				String cfCliente=set.getString("cf");

				System.out.println("Lista dei rider che possono essere valutati: ");
				set=query.executeQuery("select * from ordine o join rider r on o.CfRider=r.cf "+
						"where statoOrdine='consegnato' and CfCliente='"+cfCliente+"'");

				ArrayList<String> riders=new ArrayList<String>();
				int cont=0;
				while(set.next()) {
					System.out.println(cont+": "+set.getString("nome")+" "+set.getString("cognome"));
					cont++;
					riders.add(set.getString("cf"));
				}

				if(cont==0) {
					System.out.println("Nessun rider può essere valutato da questo cliente");
					continue;
				}
				System.out.println("Inserisci il numero del rider che vuoi valutare: ");
				int nRider=input.nextInt();
				input.nextLine();
				String cfRider=riders.get(nRider);

				GregorianCalendar data=new GregorianCalendar();
				int giorno=data.get(Calendar.DAY_OF_MONTH);
				int mese=data.get(Calendar.MONTH)+1;
				int anno=data.get(Calendar.YEAR);
				String dataOdierna=anno+"-"+mese+"-"+giorno;

				System.out.println("Quante stelle vuoi dare al rider? (1-5)");
				int nStelle=input.nextInt();
				input.nextLine();
				if(nStelle>=1 && nStelle<=5) {
					query.executeUpdate("INSERT INTO valutazione (CfCliente, CfRider, numeroStelle, dataValutazione) "
							+ "VALUES('"+cfCliente+"','"+cfRider+"',"+nStelle+",'"+dataOdierna+"')");

				}
				else {
					System.out.println("Numero di stelle non valido");
				}
			}
			else if(risposta==6)
			{
				set=query.executeQuery("select nome,cognome, count(*) as nOrdini "
						+ "from cliente c join ordine o on c.cf=o.CfCliente "
						+ "group by cf");

				System.out.println("Il numero degli ordini per ogni cliente è: ");

				while(set.next())
				{
					System.out.println(set.getString("nome")+" "+set.getString("cognome")+" |n.Ordini: "+set.getInt("nOrdini")+"|");
				}
			}
			else if(risposta==7)
			{
				//qui otteniamo il servizioDelivery
				set=query.executeQuery("select * from serviziodelivery");
				ArrayList<Integer> deliveries = new ArrayList<Integer>();
				int cont=0;

				while(set.next())
				{
					System.out.println(cont+": "+set.getString("descrizione"));
					cont++;
					deliveries.add(set.getInt("codice"));
				}

				System.out.println("Inserisci il numero del servizio di delivery da affidare ad una società di delivery");
				int numeroSDelivery=input.nextInt();
				input.nextLine();
				int numServizio=deliveries.get(numeroSDelivery);

				set=query.executeQuery("select * from societadelivery");
				ArrayList<Integer> societies = new ArrayList<Integer>();
				cont=0;

				while(set.next())
				{
					System.out.println(cont+": "+set.getString("nome"));
					cont++;
					societies.add(set.getInt("numeroSocieta"));
				}

				System.out.println("Inserisci il numero della società di delivery a cui affidare il servizio di delivery");
				int numeroSocieta=input.nextInt();
				input.nextLine();
				int numSocieta=societies.get(numeroSocieta);

				query.executeUpdate("INSERT INTO svolgimento(numServizio,numSocieta) "
						+ "VALUES ("+numServizio+","+numSocieta+")");

			}
			else if(risposta==8)
			{
				System.out.println("Nome: ");
				String nome=input.nextLine();

				System.out.println("Cognome: ");
				String cognome=input.nextLine();

				System.out.println("Codice Fiscale: ");
				String cf=input.nextLine();

				System.out.println("Short Curriculum: ");
				String shortCurriculum=input.nextLine();

				System.out.println("Anni di esperienza: ");
				int anniEsperienza=input.nextInt();
				input.nextLine();

				System.out.println("Data di Assunzione: AAAA-MM-GG ");
				String dataAssunzione=input.nextLine();

				System.out.println("Tipo del contratto: ");
				String tipoContratto=input.nextLine();

				System.out.println("Ristoranti disponibili per l'assunzione: ");
				set=query.executeQuery("select * from ristorante");

				//stampa della lista dei ristoranti + scelta del ristorante da parte dell'utente
				while(set.next()) {
					System.out.println(set.getInt("codice")+": "+set.getString("nome"));
				}

				System.out.println("Scegli il ristorante per cui il dipendente lavorerà: ");
				int numRistorante=input.nextInt();
				input.nextLine();

				query.executeUpdate("INSERT INTO dipendente(cf,nome,cognome,shortcurriculum,anniEsperienza,codRistorante,dataAssunzione,tipoContratto) "
						+ "VALUES ('"+cf+"','"+nome+"','"+cognome+"','"+shortCurriculum+"',"+anniEsperienza+","+numRistorante+",'"+dataAssunzione+"','"+tipoContratto+"')");
			}
			else if(risposta==9) {

				set=query.executeQuery("select * from societadelivery "
						+ "where nome='FoodDelivery'");
				set.next();
				int foodDelivery=set.getInt("numeroSocieta");

				/*
				 * ServiziRistorante è una vista che mostra per ogni ristorante i suoi servizi di delivery:
				 * create view ServiziRistorante as select codRistorante, codServizio from utilizzo */

				set=query.executeQuery("select * from ristorante "
						+ "where exists (select * from dipendente d where d.codRistorante=codice) or "
						+ "exists (select * from svolgimento "
						+ "where numSocieta="+foodDelivery+" and numServizio in (select codServizio from ServiziRistorante s "
						+ "where s.codRistorante=codice)"
						+ ")");

				while(set.next()) {
					System.out.println(set.getString("nome"));

				}
			}
			else if(risposta==10) {

				set=query.executeQuery("select * from ordine where statoOrdine='consegnato' and CfRider not in "
						+ "(select CfRider from valutazione)");
				while(set.next()) {
					System.out.println("Codice Ordine: "+set.getInt("numeroOrdine")+"\nData Ordine: "+set.getString("dataOrdine")+"\n" );
				}

			}
			else if(risposta==11) {

				set=query.executeQuery("select * from ristorante");
				//stampa della lista dei ristoranti + scelta del ristorante da parte dell'utente
				while(set.next()) {
					System.out.println(set.getInt("codice")+": "+set.getString("nome"));
				}
				System.out.println("Inserisci il codice del ristorante da cui vuoi cancellare l'ordine: ");
				int codRistorante=input.nextInt();
				input.nextLine();

				//2 arrayList così da conservare entrambe le chiavi dell'ordine
				ArrayList<String> dateOrdini=new ArrayList<String>();
				ArrayList<Integer> codiciOrdini=new ArrayList<Integer>();
				int cont=0;

				set=query.executeQuery("select * from ordine where statoOrdine<>'consegnato' and codRistorante="+codRistorante);

				if(set.next()) {
					System.out.println(cont+":");
					System.out.println("Codice Ordine: "+set.getInt("numeroOrdine")+"\nData Ordine: "+set.getString("dataOrdine")+"\n" );
					dateOrdini.add(set.getString("dataOrdine"));
					codiciOrdini.add(set.getInt("numeroOrdine"));
					cont++;

					while(set.next()) {
						System.out.println(cont+":");
						System.out.println("Codice Ordine: "+set.getInt("numeroOrdine")+"\nData Ordine: "+set.getString("dataOrdine")+"\n" );
						dateOrdini.add(set.getString("dataOrdine"));
						codiciOrdini.add(set.getInt("numeroOrdine"));
						cont++;
					}
				}
				else {
					System.out.println("Non ci sono ordini non consegnati per il ristorante scelto");
					continue;
				}


				System.out.println("Inserisci il numero dell'ordine da cancellare: ");
				int n=input.nextInt();
				input.nextLine();
				int numeroOrdine=codiciOrdini.get(n);
				String dataOrdine=dateOrdini.get(n);

				try {
					query.executeUpdate("delete from ordine where numeroOrdine="+numeroOrdine+" and dataOrdine='"+dataOrdine+"' and codRistorante="+codRistorante);
					System.out.println("Cancellazione effettuata");
				}
				catch(Exception e) {
					System.out.println("Cancellazione non riuscita");
				}


			}
			else if(risposta==12) {

				GregorianCalendar data =new GregorianCalendar();
				int giorno=data.get(Calendar.DAY_OF_MONTH);
				int mese=data.get(Calendar.MONTH)+1;
				int anno=data.get(Calendar.YEAR);
				String dataOdierna=anno+"-"+mese+"-"+giorno;

				data.add(Calendar.DAY_OF_MONTH, -7); //diminuisco la dataOdierna di sette giorni
				/*Le persone da stampare avranno consegnato pacchi a Giuseppe verdi tra queste date, estremi compresi*/
				giorno=data.get(Calendar.DAY_OF_MONTH);
				mese=data.get(Calendar.MONTH)+1;
				anno=data.get(Calendar.YEAR);
				String dataMinima=anno+"-"+mese+"-"+giorno;

				set=query.executeQuery("select nome, cognome from dipendente where cf in (select CfDipendente from ordine where CfCliente= (select cf from cliente where nome='Giuseppe' and cognome='Verdi') and statoOrdine='consegnato' and dataOrdine>='"+dataMinima+"' and dataOrdine<='"+dataOdierna+"')"
						+ "union "
						+ "select nome, cognome from rider where cf in (select CfRider from ordine where CfCliente= (select cf from cliente where nome='Giuseppe' and cognome='Verdi')  and statoOrdine='consegnato' and dataOrdine>='"+dataMinima+"' and dataOrdine<='"+dataOdierna+"')");

				while(set.next()) {

					System.out.println(set.getString("nome")+" "+set.getString("cognome"));

				}

			}
			else if(risposta==13) {

				/*
				 * la coda degli ordini di un ristorante è composta da tutti gli ordini non consegnati in quella giornata
				 * il numero di ordini in stato 'ordinato' deve essere <= del numero di MaxPrenotazioni per quel ristorante
				 * la registrazione di un ordine viene vietata nell'operazione 1 se l'aggiunta dell'ordine porta ad avere un numero di ordini
				 * maggiore del numero di MaxPrenotazioni
				 * alter table ristorante
				 add constraint MaxPrenotazioniTot check(numMaxPrenotazioni >= (select count(*) as somma from ordine O where O.statoOrdine='ordinato' and O.codRistorante=codRistorante))
				 Questo vincolo controlla che non ci siano più ordini di quanti un ristorante possa avere, tuttavia MySql non accetta la funzione count. Non so in che maniera implementarla.*/

				/*select numeroOrdine, m.nome, quantita
				from ordine o join composizione c on (c.numOrdine=o.numeroOrdine and c.dataOrd=o.dataOrdine and o.codRistorante=c.CodRistorante) join
													menu m on(m.numMenu=c.numMenu and m.codRistorante=c.codRistorante)
				where statoOrdine='ordinato'
				Questa query seleziona, per ogni ordine in stato 'ordinato', i menu da cui è composto  */

				GregorianCalendar data =new GregorianCalendar();
				int giorno=data.get(Calendar.DAY_OF_MONTH);
				int mese=data.get(Calendar.MONTH)+1;
				int anno=data.get(Calendar.YEAR);
				String dataOdierna=anno+"-"+mese+"-"+giorno;

				Statement query2=con.createStatement();
				Statement query3=con.createStatement();
				set=query.executeQuery("select * from ristorante");
				ResultSet set2=null;
				ResultSet set3=null;
				while(set.next()) {
					System.out.println("Codice: "+set.getInt("codice"));
					System.out.println("Nome: "+set.getString("nome"));
					System.out.println("Telefono: "+set.getString("telefono"));
					System.out.println("Indirizzo: "+set.getString("via")+" "+set.getString("nCivico")+" "+set.getString("cap")+"\n");
					System.out.println("Coda ordini attuale: ");
					set2=query2.executeQuery("select * from ordine where statoOrdine='ordinato' and codRistorante="+set.getInt("codice")+" and dataOrdine='"+dataOdierna+"'");

					while(set2.next()) {
						System.out.println("Numero Ordine: "+set2.getInt("numeroOrdine"));
						System.out.println("Descrizione: "+set2.getString("descrizione"));
						System.out.println("Indirizzo: "+set2.getString("via")+" "+set2.getString("numeroCivico")+" "+set2.getString("cap"));
						System.out.println("Nominativo ritiro: "+set2.getString("nominativoRitiro"));
						System.out.println("Menu: ");
						set3=query3.executeQuery("select numeroOrdine, m.nome as nomePiatto, quantita "
								+ "					from ordine o join composizione c on (c.numOrdine=o.numeroOrdine and c.dataOrd=o.dataOrdine and o.codRistorante=c.CodRistorante) join "
								+ "														menu m on(m.numMenu=c.numMenu and m.codRistorante=c.codRistorante) "
								+ "					where statoOrdine='ordinato' and numeroOrdine="+set2.getInt("numeroOrdine")+" and dataOrdine='"+dataOdierna+"'");
						while(set3.next()) {
							System.out.println("- "+set3.getString("nomePiatto")+"/quantità: "+set3.getInt("quantita"));

						}
					}
					System.out.println("--------------------------------------------");
				}

			}
			else if(risposta==14) {

				set=query.executeQuery("select nome,cognome,avg(numeroStelle) as MediaValutazione "
						+ "from rider r join valutazione v on r.cf=v.CfRider "
						+ "group by cf, nome, cognome");
				while(set.next()) {
					System.out.println(set.getString("nome")+" "+set.getString("cognome")+": "+set.getDouble("MediaValutazione"));

				}

			}
			else if(risposta==15) {
				System.out.println("Inserisci il nome del rider: ");
				String nome=input.nextLine();
				System.out.println("Inserisci il cognome del rider: ");
				String cognome=input.nextLine();
				set=query.executeQuery("select * from rider where nome='"+nome+"' and cognome='"+cognome+"'");
				ArrayList<String> riders=new ArrayList<String>();
				int cont=0;
				String cfR;

				while(set.next()) {
					riders.add(set.getString("cf"));
					cont++;
				}

				if(cont==1) {
					cfR=riders.get(0);
				}
				else if(cont>1){
					for(int i=0; i<cont; i++) {

						System.out.println(i+": "+riders.get(i));
					}
					System.out.println("Inserisci il numero del rider: ");
					int codice=input.nextInt();
					input.nextLine();
					cfR=riders.get(codice);
				}
				else {
					System.out.println("Il rider non esiste");
					continue;
				}

				GregorianCalendar data =new GregorianCalendar();
				int giorno=data.get(Calendar.DAY_OF_MONTH);
				int mese=data.get(Calendar.MONTH)+1;
				int anno=data.get(Calendar.YEAR);
				String dataOdierna=anno+"-"+mese+"-"+giorno;

				data.add(Calendar.DAY_OF_MONTH, -7); //diminuisco la dataOdierna di sette giorni
				giorno=data.get(Calendar.DAY_OF_MONTH);
				mese=data.get(Calendar.MONTH)+1;
				anno=data.get(Calendar.YEAR);
				String dataMinima=anno+"-"+mese+"-"+giorno;


				set=query.executeQuery("select nome,cognome,avg(numeroStelle) MediaValutazione "
						+ "from rider r join valutazione v on r.cf=v.CfRider "
						+ "where r.cf='"+cfR+"' "
						+ "group by cf, nome, cognome"
						+ "");
				if(set.next()) {
					double mediaRider= set.getDouble("MediaValutazione");
					set=query.executeQuery("select distinct nome, cognome "
							+ "from cliente c join valutazione v on c.cf=v.CfCliente "
							+ "where numeroStelle<"+mediaRider+" and dataValutazione>='"+dataMinima+"' and dataValutazione<='"+dataOdierna+"'");
					while(set.next()) {
						System.out.println(set.getString("nome")+" "+set.getString("cognome"));
					}

				}
				else {
					System.out.println("Non esistono valutazioni per il rider");
					continue;
				}


			}
			else {
				System.out.println("Riprova");
			}
			
		}while(risposta!=-1);
	}

}
