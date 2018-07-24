package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.Beboer;
import model.Deadline;
import model.Studiekontrol;
import model.Studiekontrolstatus;
import model.Uddannelse;
import model.Værelsesudlejning;

/**
 * Klassen binder modellageret sammen med brugerinteraktionen i viewet
 * 
 * @author Janus
 *
 */

public class ExcelConnection {
	// Controller oprettes selv som et objekt i viewet
	private ArrayList<Beboer> beboere = new ArrayList<Beboer>();
	private ArrayList<Deadline> deadlines = new ArrayList<Deadline>();
	private ArrayList<Beboer> fremlejere;
	private ArrayList<Studiekontrol> studiekontroller = new ArrayList<Studiekontrol>();
	private ArrayList<Værelsesudlejning> værelsesudlejning;
	private String filnavn = "IndstillingsInfo.xlsx";
	int il = 0;
	public ArrayList<Deadline> getDeadlines() {
		return deadlines;
	}

	public void setDeadlines(ArrayList<Deadline> deadlines) {
		this.deadlines = deadlines;
	}

	public ArrayList<Beboer> getFremlejere() {
		return fremlejere;
	}

	public void setFremlejere(ArrayList<Beboer> fremlejere) {
		this.fremlejere = fremlejere;
	}

	public ArrayList<Studiekontrol> getStudiekontroller() {
		return studiekontroller;
	}

	public void setStudiekontroller(ArrayList<Studiekontrol> studiekontroller) {
		this.studiekontroller = studiekontroller;
	}

	public ArrayList<Værelsesudlejning> getVærelsesudlejning() {
		return værelsesudlejning;
	}

	public void setVærelsesudlejning(ArrayList<Værelsesudlejning> værelsesudlejning) {
		this.værelsesudlejning = værelsesudlejning;
	}

	public void setBeboere(ArrayList<Beboer> beboere) {
		this.beboere = beboere;
	}

	public ExcelConnection() {

		try(FileInputStream fis = new FileInputStream(filnavn)){//evt. bare et tjek istedet for at oprette fis
			hentBeboereFraExcel(); // beboere oprettes
			hentDeadlinesFraExcel();
			hentFremlejerFraExcel();
			hentStudiekontrollerfraExcel();
			hentVærelsesudlejningFraExcel();
			
			fis.close();
		} catch( Exception e){
			
			e.printStackTrace();
			
			createExcelFile();
		}


	}

	private void hentStudiekontrollerfraExcel() {
		try {
			FileInputStream fis = new FileInputStream(filnavn);
			Workbook workbook = WorkbookFactory.create(fis);

			int startRække = 1;// +1 for ikk at tageoverskriften med

			int slutRække = workbook.getSheetAt(4).getLastRowNum();

			// Opretter studiekontrolelementerne uden beboere der skal indgå
			for (int i = startRække; i < slutRække; i++) {
				Row row = workbook.getSheetAt(4).getRow(i);

				int kollonnenummer = 0;

				Date d1 = row.getCell(kollonnenummer).getDateCellValue();
				LocalDate afleveringsfrist = konverterDateTilLocalDate(d1);

				Date d2 = row.getCell(++kollonnenummer).getDateCellValue();
				LocalDate påmindelsesdato = konverterDateTilLocalDate(d2);

				Date d3 = row.getCell(++kollonnenummer).getDateCellValue();
				LocalDate begyndelsesdato = konverterDateTilLocalDate(d3);

				int månedsnummer = (begyndelsesdato.getMonthValue() + 4) % 12; // de +4 giver den måned der påbegyndes
																				// for.
				Boolean afsluttet = row.getCell(++kollonnenummer).getBooleanCellValue();

				Studiekontrol studiekontrol = new Studiekontrol(null, afleveringsfrist, påmindelsesdato,
						begyndelsesdato, månedsnummer, afsluttet);
				studiekontroller.add(studiekontrol);

			}
			// tilføjer beboere til studiekontroller hvis der er nogen
			if (studiekontroller.size() > 0) {
				for (int j = 0; j < studiekontroller.size(); j++) {
					int måned = studiekontroller.get(j).getMånedsnummer();
					ArrayList<Beboer> list = new ArrayList<Beboer>();

					for (int i = 0; i < beboere.size(); i++) {
						if (beboere.get(i).getLejeaftalensUdløb().getMonthValue() == måned) {
							if (beboere.get(i).getStudiekontrolstatus() != Studiekontrolstatus.IKKEIGANG)
								list.add(beboere.get(i));
						}
					//Læg for loop der håndterer fremlejere?
					}
					studiekontroller.get(j).setBeboere(list);
				}
			}
			
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			
			e.printStackTrace();
		}

		// Første to loops henter alle de igangværende studiekontroller ind

	}

	/**
	 * Metoden henter både udlejede og ikke udlejede værelser og gemmer dem i en
	 * ArrayList ved navn værelsesudlejning
	 */
	private void hentVærelsesudlejningFraExcel() {

		try {
			FileInputStream fis = new FileInputStream(filnavn);
			Workbook workbook = WorkbookFactory.create(fis);

			int startRække = 1;// +1 for ikk at tage overskriften med

			int slutRække = workbook.getSheetAt(5).getLastRowNum();

			for (int i = startRække; i < slutRække; i++) {
				Row row = workbook.getSheetAt(5).getRow(i);

				int kollonnenummer = 0;

				Date d1 = row.getCell(kollonnenummer).getDateCellValue();
				LocalDate indflytningsdato = konverterDateTilLocalDate(d1);

				String værelse = row.getCell(++kollonnenummer).getStringCellValue();

				String navn = row.getCell(++kollonnenummer).getStringCellValue();

				Date d2 = row.getCell(++kollonnenummer).getDateCellValue();
				LocalDate behandlingsdato = konverterDateTilLocalDate(d2);

				String behandlerinitialer = row.getCell(++kollonnenummer).getStringCellValue();

				Værelsesudlejning v = new Værelsesudlejning(indflytningsdato, værelse, navn, behandlingsdato,
						behandlerinitialer);
				værelsesudlejning.add(v);

			}
			fis.close();
			workbook.close();
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Metoden konverterer String til Enum
	 * 
	 * @param s
	 *            String i forbindelse med studiekontrolstatus for beboer som skal
	 *            konverteres til ENUM
	 * @return Enum tilsvarende string der gemmes i Exceldokumentet
	 */
	private Enum<Studiekontrolstatus> konverterStringTilEnum(String s) {
		Enum<Studiekontrolstatus> status;
		switch (s) {
		case "Ikke i gang":
			status = Studiekontrolstatus.IKKEIGANG;
			return status;
		case "Modtaget, ikke godkendt":
			status = Studiekontrolstatus.MODTAGETIKKEGODKENDT;
			return status;
		case "Ikke Modtaget":
			status = Studiekontrolstatus.IKKEAFLEVERET;
			return status;
		case "Sendt til boligselskab":
			status = Studiekontrolstatus.SENDTTILBOLIGSELSKAB;
			return status;
		case "Godkendt":
			status = Studiekontrolstatus.GODKENDT;
			return status;
		default:
			return null;
		}

	}

	/**
	 * Metoden konverterer Enum til String
	 * 
	 * @param studiekontrolstatus
	 *            Den Enum der skal konverteres til en string der kan gemmes i
	 *            Exceldokumentet
	 * @return String på studiekontrolstatus
	 */
	private String konverterEnumTilString(Studiekontrolstatus studiekontrolstatus) {
		String s;
		switch (studiekontrolstatus) {
		case IKKEIGANG:
			s = "Ikke i gang";
			return s;
		case MODTAGETIKKEGODKENDT:
			s = "Modtaget, ikke godkendt";
			return s;
		case IKKEAFLEVERET:
			s = "Ikke Modtaget";
			return s;
		case SENDTTILBOLIGSELSKAB:
			s = "Sendt til boligselskab";
			return s;
		case GODKENDT:
			s = "Godkendt";
			return s;
		default:
			return null;
		}

	}

	/**
	 * Henter fremelejer fra excel dokument
	 */
	private void hentFremlejerFraExcel() {
		try {
			FileInputStream fis = new FileInputStream(filnavn);
			Workbook workbook = WorkbookFactory.create(fis);

			;
			int startRække = 1;// 1 for ikk at tageoverskriften med

			int slutRække = workbook.getSheetAt(2).getLastRowNum();

			for (int i = startRække; i < slutRække; i++) {
				Row row = workbook.getSheetAt(2).getRow(i);
				// Load de forskellige ting til "beboere her"
				int kollonnenummer = 0;

				String værelse = row.getCell(kollonnenummer).getStringCellValue();

				String navn = row.getCell(++kollonnenummer).getStringCellValue();

				Date d1 = row.getCell(++kollonnenummer).getDateCellValue();
				LocalDate fremlejeStartdato = konverterDateTilLocalDate(d1);

				String uddannelsessted = row.getCell(++kollonnenummer).getStringCellValue();

				String uddannelsesretning = row.getCell(++kollonnenummer).getStringCellValue();

				Date d2 = row.getCell(++kollonnenummer).getDateCellValue();
				LocalDate uddStart = konverterDateTilLocalDate(d2);

				Date d3 = row.getCell(++kollonnenummer).getDateCellValue();
				LocalDate uddSlut = konverterDateTilLocalDate(d3);

				Date d4 = row.getCell(++kollonnenummer).getDateCellValue();
				LocalDate fremlejeSlutdato = konverterDateTilLocalDate(d4);

				String telefonnummer = row.getCell(++kollonnenummer).getStringCellValue();

				Enum<Studiekontrolstatus> studiekontrolstatus = konverterStringTilEnum(
						row.getCell(++kollonnenummer).getStringCellValue());
				Uddannelse uddannelse = new Uddannelse(uddannelsessted, uddannelsesretning, uddStart, uddSlut);

				Beboer beboer = new Beboer(værelse, navn, uddannelse, fremlejeStartdato, fremlejeSlutdato,
						telefonnummer, studiekontrolstatus);
				fremlejere.add(beboer);

			}
			fis.close();
			workbook.close();
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			System.out.println("Filen kan ikke findes");
			e.printStackTrace();
		}

	}

	/**
	 * Henter deadlines fra Excel
	 */
	private void hentDeadlinesFraExcel() {
//		try {
//			FileInputStream fis = new FileInputStream(filnavn);
//			Workbook workbook = WorkbookFactory.create(fis);
//
//			int startRække = 1;// Starter på 1 for ikke at tage overskrifter med
//
//			int slutRække = workbook.getSheetAt(0).getLastRowNum();
//
//
//			for (int i = startRække; i <= slutRække; i++) {
//				Row row = workbook.getSheetAt(0).getRow(i);
//
//				int kollonnenummer = 0;
//
//				String værelse = row.getCell(kollonnenummer).getStringCellValue();
//
//				String navn = row.getCell(++kollonnenummer).getStringCellValue();
//
//				Date d = row.getCell(++kollonnenummer).getDateCellValue();
//				LocalDate indflytning = konverterDateTilLocalDate(d);//HERRRR
//
//				String uddannelsessted = row.getCell(++kollonnenummer).getStringCellValue();
//
//				String uddannelsesretning = row.getCell(++kollonnenummer).getStringCellValue();
//
//				Date d1 = row.getCell(++kollonnenummer).getDateCellValue();
//				LocalDate uddStart = konverterDateTilLocalDate(d1);
//
//				Date d2 = row.getCell(++kollonnenummer).getDateCellValue();
//				LocalDate uddSlut = konverterDateTilLocalDate(d2);
//
//				Date d3 = row.getCell(++kollonnenummer).getDateCellValue();
//				LocalDate lejeaftalensUdløb = konverterDateTilLocalDate(d3);
//
//				String telefonnummer = row.getCell(++kollonnenummer).getStringCellValue();
//
//				Enum<Studiekontrolstatus> studiekontrolstatus = konverterStringTilEnum(
//						row.getCell(++kollonnenummer).getStringCellValue());
//
//				Uddannelse uddannelse = new Uddannelse(uddannelsessted, uddannelsesretning, uddStart, uddSlut);
//				Beboer beboer = new Beboer(værelse, navn, uddannelse, indflytning, lejeaftalensUdløb, telefonnummer,
//						studiekontrolstatus);
//				beboere.add(beboer);
//
//			}
//			fis.close();
//			workbook.close();
//		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
//			System.out.println("Filen kan ikke findes");
//			e.printStackTrace();
//		}
		
		try {
			FileInputStream fis = new FileInputStream(filnavn);
			Workbook workbook = WorkbookFactory.create(fis);

			int startRække = 1;// 1 for ikk at tageoverskriften med

			int slutRække = workbook.getSheetAt(3).getLastRowNum();

			for (int i = startRække; i <= slutRække; i++) {
				Row row = workbook.getSheetAt(3).getRow(i);
				// Load de forskellige ting til "beboere her"
				int kollonnenummer = 0;

				String hvem = row.getCell(kollonnenummer).getStringCellValue();

				String hvad = row.getCell(++kollonnenummer).getStringCellValue();

				Date d = row.getCell(++kollonnenummer).getDateCellValue();
				LocalDate hvornår = konverterDateTilLocalDate(d);
				
				String ID = row.getCell(++kollonnenummer).getStringCellValue();

				// Sidste del i deadline er null, da der altid vil være et ID på
				Deadline deadline = new Deadline(hvem, hvad, hvornår, ID, null);
				deadlines.add(deadline);

			}
			fis.close();

			workbook.close();
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			System.out.println("Filen kan ikke findes");
			e.printStackTrace();
		}

	}

	public void skrivAlleDeadlinesTilExcel() throws InvalidFormatException {
		try (FileInputStream fis = new FileInputStream(filnavn);) {
			Workbook workbook = WorkbookFactory.create(fis);
			workbook.getSheet("Deadlines").getRow(2).getCell(2).setCellValue("Dette er den nye værdi");

			// sheet = wb.getSheetAt(0);
			// for (int i = 0; i < deadlines.size(); i++) {
			// Row row = sheet.getRow(i);
			// // Load de forskellige ting til "beboere her"
			// int kollonnenummer = 0;
			// Cell cell;
			// cell = row.getCell(kollonnenummer);
			// cell.setCellValue(deadlines.get(i).getHvem());//Hvem
			// cell = row.getCell(++kollonnenummer);
			// cell.setCellValue(deadlines.get(i).getHvad());//Hvad
			// cell = row.getCell(++kollonnenummer);
			// Date hvornår =
			// konverterLocalDateTilDate(deadlines.get(i).getHvornår());//Hvornår
			// cell.setCellValue(hvornår);
			// cell = row.getCell(++kollonnenummer);
			// cell.setCellValue(deadlines.get(i).getID());//ID
			// cell = row.getCell(++kollonnenummer);
			// cell.setCellValue(deadlines.get(i).isKlaret());
			//
			// }
			// FileOutputStream stream = new FileOutputStream(filnavn);
			fis.close();

			workbook.write(new FileOutputStream(filnavn));
			workbook.close();

			// Modify the workbook as you wish
			// As an example, we override the first cell of the first row in the first sheet
			// (0-based indices)
			// workbook.getSheetAt(0).getRow(0).getCell(0).setCellValue("new value for A1");

			// you have to close the input stream FIRST before writing to the same file.

			// save your changes to the same file.

		} catch (EncryptedDocumentException | IOException e) {
			System.out.println("Filen kan ikke findes");
			e.printStackTrace();
		}
	}

	public void redigerDeadlineIExcel(Deadline deadline) { // SKAL FIKSES
		try (Workbook wb = WorkbookFactory.create(new File(filnavn))) {

			Sheet sheet = wb.getSheet("Deadlines");
			int startRække = sheet.getFirstRowNum() + 1;
			int slutRække = sheet.getLastRowNum();

			for (int i = startRække; i < slutRække; i++) {

				Row row = sheet.getRow(i);
				int idKollonne = 3; // (3 = ID-kollonnen)
				Cell cell;
				if (row.getCell(idKollonne).getStringCellValue().equals(deadline.getID())) {
					int start = 0;
					cell = row.getCell(start);
					cell.setCellValue(deadlines.get(i).getHvem());// Hvem
					cell = row.getCell(++start);
					cell.setCellValue(deadlines.get(i).getHvad());// Hvad
					cell = row.getCell(++start);
					Date hvornår = konverterLocalDateTilDate(deadlines.get(i).getHvornår());// Hvornår
					cell.setCellValue(hvornår);
					cell = row.getCell(++start);
					cell.setCellValue(deadlines.get(i).getID());// ID
					cell = row.getCell(++start);
					cell.setCellValue(deadlines.get(i).isKlaret());// Boolean klaret

				}

			}
			FileOutputStream stream = new FileOutputStream(filnavn);
			wb.write(stream);
			wb.close();

		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			System.out.println("Filen kan ikke findes");
			e.printStackTrace();
		}
	}

	/**
	 * Metoden opretter fanerne og overskrifter i en excelfil. Skal anvendes hvis
	 * filen ikke kan findes. VIRKER MEN MÆRKELIGT VED SHEET 2+++
	 */
	public void createExcelFile() {
		Workbook wb = new XSSFWorkbook();
		int start = 0;

		// Overskrifter til Beboerliste sheet
		Sheet sheet1 = wb.createSheet("Beboerliste");
		Row row1 = sheet1.createRow(0);

		row1.createCell(start).setCellValue("Værelse");
		row1.createCell(++start).setCellValue("Navn");
		row1.createCell(++start).setCellValue("Indflytningsdato");
		row1.createCell(++start).setCellValue("Uddannelsessted");
		row1.createCell(++start).setCellValue("Uddannelsesretning");
		row1.createCell(++start).setCellValue("Uddannelse påbegyndt:");
		row1.createCell(++start).setCellValue("Uddannelse forventes afsluttet");
		row1.createCell(++start).setCellValue("Udløbsdato på lejeaftale");
		row1.createCell(++start).setCellValue("Telefonnummer");
		row1.createCell(++start).setCellValue("Studiekontrolstatus");

		start = 0;

		// overksrifter til Dispensationer sheet
		Sheet sheet2 = wb.createSheet("Dispensationer");
		Row row2 = sheet2.createRow(0);
		// For at finde de tilhørende deadlines, så kan der ud fra værelsesnummer om
		// navn sammensættes en key der gives som søgebetingelse deadlines
		row2.createCell(start).setCellValue("Værelse");
		row2.createCell(++start).setCellValue("Navn");
		row2.createCell(++start).setCellValue("StartDato");
		row2.createCell(++start).setCellValue("SlutDato");
		row2.createCell(++start).setCellValue("Dispensations-ID");

		start = 0;

		// Overskrifter til fremlejer-sheet
		Sheet sheet3 = wb.createSheet("Fremlejer");
		Row row3 = sheet3.createRow(0);

		row3.createCell(start).setCellValue("Værelse");// Noget må bugge siden den kører som den skal med ++ her?
		row3.createCell(++start).setCellValue("navn");
		row3.createCell(++start).setCellValue("Fremleje Startdato");
		row3.createCell(++start).setCellValue("uddannelsessted");
		row3.createCell(++start).setCellValue("uddannelsesretning");
		row3.createCell(++start).setCellValue("uddannelsesstart");
		row3.createCell(++start).setCellValue("uddannelse afsluttes:");
		row3.createCell(++start).setCellValue("Fremleje slutdato");
		row3.createCell(++start).setCellValue("Telefonnummer");
		row3.createCell(++start).setCellValue("Studiekontrolstatus");

		start = 0;

		// Overskrifter til deadlines sheet
		Sheet sheet4 = wb.createSheet("Deadlines");
		Row row4 = sheet4.createRow(0);

		row4.createCell(start).setCellValue("Hvem");
		row4.createCell(++start).setCellValue("Hvad");
		row4.createCell(++start).setCellValue("Hvornår");
		row4.createCell(++start).setCellValue("Status");
		row4.createCell(++start).setCellValue("ID");


		start = 0;

		// Overskrifter til studiekontroller sheet
		Sheet sheet5 = wb.createSheet("Studiekontroller");
		Row row5 = sheet5.createRow(0);

		row5.createCell(start).setCellValue("Afleveringsfrist");
		row5.createCell(++start).setCellValue("Påmindelsesdato");
		row5.createCell(++start).setCellValue("Påbegyndelsesdato");
		row5.createCell(++start).setCellValue("Månedsnummer for den påbegyndte studiekontrol");
		row5.createCell(++start).setCellValue("Status på studiekontrol");

		start = 0;

		// Overskrifter til værelsesudlejning
		Sheet sheet6 = wb.createSheet("Værelsesudlejning");
		Row row6 = sheet6.createRow(0);
		row6.createCell(start).setCellValue("indflytningsdato");
		row6.createCell(++start).setCellValue("Værelse");
		row6.createCell(++start).setCellValue("Navn");
		row6.createCell(++start).setCellValue("Behandlingsdato");
		row6.createCell(++start).setCellValue("behandlerInitialer");

		try {
			FileOutputStream stream = new FileOutputStream(filnavn);
			wb.write(stream);
			stream.close();
			wb.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}
/**
 * Metoden tjekker først om værelset findes. og ellers skriver den værelsesnummeret ind nederst på listen
 * @param beboer : Den beboer der oprettesi excelfilen
 */
	public void opretBeboerIExcel(Beboer beboer) { //
		try {
		FileInputStream fis = new FileInputStream(filnavn);
		Workbook workbook = WorkbookFactory.create(fis);
		int startRække = 1;
		int slutRække = workbook.getSheetAt(0).getLastRowNum();
		boolean beboerFindes = false;
		
		//Loop gennem excel dokumentet og find rækkepladsen
		for(int i = startRække; i<=slutRække;i++) {
			String s = workbook.getSheetAt(0).getRow(i).getCell(0).getStringCellValue();
			//Hvis det passer, så skriv til værelsesnummeret
			if(s.equals(beboer.getVærelse())) {
				int celleNr = 0;
				workbook.getSheetAt(0).getRow(i).getCell(celleNr++).setCellValue(beboer.getVærelse());
				
				workbook.getSheetAt(0).getRow(i).getCell(celleNr++).setCellValue(beboer.getNavn());
				
				Date d1 = konverterLocalDateTilDate(beboer.getIndflytningsdato());
				workbook.getSheetAt(0).getRow(i).getCell(celleNr++).setCellValue(d1);
				
				workbook.getSheetAt(0).getRow(i).getCell(celleNr++).setCellValue(beboer.getUddannelse().getUddannelsessted());
				
				workbook.getSheetAt(0).getRow(i).getCell(celleNr++).setCellValue(beboer.getUddannelse().getUddannelsesretning());
				
				Date d2 = konverterLocalDateTilDate(beboer.getUddannelse().getPåbegyndtDato());
				workbook.getSheetAt(0).getRow(i).getCell(celleNr++).setCellValue(d2);
				
				Date d3 = konverterLocalDateTilDate(beboer.getUddannelse().getForventetAfsluttetDato());
				workbook.getSheetAt(0).getRow(i).getCell(celleNr++).setCellValue(d3);
				
				Date d4 = konverterLocalDateTilDate(beboer.getLejeaftalensUdløb());
				workbook.getSheetAt(0).getRow(i).getCell(celleNr++).setCellValue(d4);
				
				workbook.getSheetAt(0).getRow(i).getCell(celleNr++).setCellValue(beboer.getTelefonnummer());
				
				String s1 = konverterEnumTilString((Studiekontrolstatus) beboer.getStudiekontrolstatus());
				workbook.getSheetAt(0).getRow(i).getCell(celleNr++).setCellValue(s1);
				
				beboerFindes = true;
				
			}
		}
		if (beboerFindes == false) {
			il++;
			System.out.println(il);
			workbook.getSheetAt(0).createRow(slutRække+1);
			workbook.getSheetAt(0).getRow(slutRække+1).createCell(0).setCellValue(beboer.getVærelse());
			workbook.getSheetAt(0).getRow(slutRække+1).createCell(1).setCellValue(beboer.getNavn());
			
			Date d1 = konverterLocalDateTilDate(beboer.getIndflytningsdato());
			workbook.getSheetAt(0).getRow(slutRække+1).createCell(2).setCellValue(d1);
//			Date d1 = konverterLocalDateTilDate(beboer.getIndflytningsdato());//HERRRRRRRRRRRR
//			createExcelDateFormat(workbook, 0, slutRække+1, 2, beboer.getIndflytningsdato());
			
			workbook.getSheetAt(0).getRow(slutRække+1).createCell(3).setCellValue(beboer.getUddannelse().getUddannelsessted());
			
			workbook.getSheetAt(0).getRow(slutRække+1).createCell(4).setCellValue(beboer.getUddannelse().getUddannelsesretning());
			
			Date d2 = konverterLocalDateTilDate(beboer.getUddannelse().getPåbegyndtDato());
			workbook.getSheetAt(0).getRow(slutRække+1).createCell(5).setCellValue(d2);
			
			Date d3 = konverterLocalDateTilDate(beboer.getUddannelse().getForventetAfsluttetDato());
			workbook.getSheetAt(0).getRow(slutRække+1).createCell(6).setCellValue(d3);
			
			Date d4 = konverterLocalDateTilDate(beboer.getLejeaftalensUdløb());
			workbook.getSheetAt(0).getRow(slutRække+1).createCell(7).setCellValue(d4);
			
			workbook.getSheetAt(0).getRow(slutRække+1).createCell(8).setCellValue(beboer.getTelefonnummer());
			
			String s1 = konverterEnumTilString((Studiekontrolstatus) beboer.getStudiekontrolstatus());
			workbook.getSheetAt(0).getRow(slutRække+1).createCell(9).setCellValue(s1);
		}

		//you have to close the input stream FIRST before writing to the same file.
		fis.close() ;

		//save your changes to the same file.
		workbook.write(new FileOutputStream(filnavn)); 
		workbook.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		

//		row.createCell(kollonne).setCellValue("Værelse");
//		row.createCell(kollonne++).setCellValue("Navn");
//		row.createCell(kollonne++).setCellValue("Indflytningsdato");
//		row.createCell(kollonne++).setCellValue("Uddannelsessted");
//		row.createCell(kollonne++).setCellValue("Uddannelsesretning");
//		row.createCell(kollonne++).setCellValue("Uddannelse påbegyndt:");
//		row.createCell(kollonne++).setCellValue("Uddannelse forventes afsluttet");
//		row.createCell(kollonne++).setCellValue("Udløbsdato på lejeaftale");
//		row.createCell(kollonne++).setCellValue("Telefonnummer");
//		row.createCell(kollonne++).setCellValue("Studiekontrolstatus");
	

	
	}

	public void opdaterBeboerinfoIExcel(Beboer beboer) {
		try (Workbook wb = WorkbookFactory.create(new File(filnavn))) {

			Sheet sheet = wb.getSheet("Studiekontroller");
			int startRække = sheet.getFirstRowNum();// +1 for ikk at tageoverskriften med

			int slutRække = sheet.getLastRowNum();

			boolean værelseFundet = false;
			// Opretter studiekontrolelementerne uden beboere der skal indgå
			for (int i = startRække; i < slutRække; i++) {
				Row row = sheet.getRow(i);

				int kollonnenummer = 0;

				// Hvis værelsesnummer = celleindholdet, så overskriv rækken
				if (beboer.getVærelse().equals(row.getCell(kollonnenummer).getStringCellValue())) {
					Cell cell;
					cell = row.getCell(kollonnenummer);
					cell.setCellValue(beboer.getNavn()); // navn

					cell = row.getCell(++kollonnenummer);
					Date indflytningsdato = konverterLocalDateTilDate(beboer.getIndflytningsdato());
					cell.setCellValue(indflytningsdato); // indflytningsdato

					cell = row.getCell(++kollonnenummer);
					cell.setCellValue(beboer.getUddannelse().getUddannelsessted());// Uddannelsessted

					cell = row.getCell(++kollonnenummer);
					cell.setCellValue(beboer.getUddannelse().getUddannelsesretning());// Uddannelsesretning

					cell = row.getCell(++kollonnenummer);
					Date uddStart = konverterLocalDateTilDate(beboer.getUddannelse().getPåbegyndtDato());
					cell.setCellValue(uddStart); // uddannelse påbegyndt

					cell = row.getCell(++kollonnenummer);
					Date uddSlut = konverterLocalDateTilDate(beboer.getUddannelse().getForventetAfsluttetDato());
					cell.setCellValue(uddSlut); // uddannelse forventet afsluttet

					cell = row.getCell(++kollonnenummer);
					Date lejeaftaleSlutdato = konverterLocalDateTilDate(beboer.getLejeaftalensUdløb());
					cell.setCellValue(lejeaftaleSlutdato); // lejeaftalens udløb

					cell = row.getCell(++kollonnenummer);
					cell.setCellValue(beboer.getTelefonnummer()); // telefonenummer

					cell = row.getCell(++kollonnenummer);
					Studiekontrolstatus s = (Studiekontrolstatus) beboer.getStudiekontrolstatus();
					cell.setCellValue(konverterEnumTilString(s)); // Studiekontrolstatus

					værelseFundet = true;
				}

			}
			// Indsæt beboer på en tom plads i slutningen af listen
			if (værelseFundet = false) {
				Row row = sheet.createRow(slutRække + 1);

				int kollonnenummer = 0;

				Cell cell;

				row.createCell(kollonnenummer).setCellValue(beboer.getVærelse());// værelse

				row.createCell(++kollonnenummer).setCellValue(beboer.getNavn()); // navn

				cell = row.createCell(++kollonnenummer);
				Date indflytningsdato = konverterLocalDateTilDate(beboer.getIndflytningsdato());
				cell.setCellValue(indflytningsdato); // indflytningsdato

				row.getCell(++kollonnenummer).setCellValue(beboer.getUddannelse().getUddannelsessted());// Uddannelsessted

				row.createCell(++kollonnenummer).setCellValue(beboer.getUddannelse().getUddannelsesretning());// Uddannelsesretning

				cell = row.createCell(++kollonnenummer);
				Date uddStart = konverterLocalDateTilDate(beboer.getUddannelse().getPåbegyndtDato());
				cell.setCellValue(uddStart); // uddannelse påbegyndt

				cell = row.createCell(++kollonnenummer);
				Date uddSlut = konverterLocalDateTilDate(beboer.getUddannelse().getForventetAfsluttetDato());
				cell.setCellValue(uddSlut); // uddannelse forventet afsluttet

				cell = row.getCell(++kollonnenummer);
				Date lejeaftaleSlutdato = konverterLocalDateTilDate(beboer.getLejeaftalensUdløb());
				cell.setCellValue(lejeaftaleSlutdato); // lejeaftalens udløb

				row.createCell(++kollonnenummer).setCellValue(beboer.getTelefonnummer()); // telefonenummer

				cell = row.createCell(++kollonnenummer);
				Studiekontrolstatus s = (Studiekontrolstatus) beboer.getStudiekontrolstatus();
				cell.setCellValue(konverterEnumTilString(s)); // Studiekontrolstatus
			}
			FileOutputStream stream = new FileOutputStream(filnavn);
			wb.write(stream);
			wb.close();
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			System.out.println("Filen kan ikke findes");
			e.printStackTrace();
		}

	}

	public ArrayList<Beboer> getBeboere() {

		return beboere;
	}

	/**
	 * Metoden kan anvendes hvis man vil skrive datoen "pænere" til excel.
	 * til date, samt indsætter dette i cellen.
	 * 
	 * @param row
	 * @param wb
	 * @param rækkeplads
	 * @param date
	 * @return
	 */
	public Cell createExcelDateFormat(Workbook workbook, int sheetnummer, int rækkeplads, int celleplads, LocalDate date) {// Test om den sætter det
																								// ind i rette kolllonne
		Cell c = workbook.getSheetAt(sheetnummer).getRow(rækkeplads).createCell(celleplads);
		DataFormat format = workbook.createDataFormat();
		CellStyle datestyle = workbook.createCellStyle();
		datestyle.setDataFormat(format.getFormat("dd.MM.yyyy"));
		c.setCellStyle(datestyle);
		Date d = konverterLocalDateTilDate(date);
		c.setCellValue(d); // new Date()
		return c;
	}


	public void hentBeboereFraExcel() {
		
		try {
			FileInputStream fis = new FileInputStream(filnavn);
			Workbook workbook = WorkbookFactory.create(fis);

			int startRække = 1;// Starter på 1 for ikke at tage overskrifter med

			int slutRække = workbook.getSheetAt(0).getLastRowNum();


			for (int i = startRække; i <= slutRække; i++) {
				Row row = workbook.getSheetAt(0).getRow(i);

				int kollonnenummer = 0;

				String værelse = row.getCell(kollonnenummer).getStringCellValue();

				String navn = row.getCell(++kollonnenummer).getStringCellValue();

				Date d = row.getCell(++kollonnenummer).getDateCellValue();
				LocalDate indflytning = konverterDateTilLocalDate(d);//HERRRR

				String uddannelsessted = row.getCell(++kollonnenummer).getStringCellValue();

				String uddannelsesretning = row.getCell(++kollonnenummer).getStringCellValue();

				Date d1 = row.getCell(++kollonnenummer).getDateCellValue();
				LocalDate uddStart = konverterDateTilLocalDate(d1);

				Date d2 = row.getCell(++kollonnenummer).getDateCellValue();
				LocalDate uddSlut = konverterDateTilLocalDate(d2);

				Date d3 = row.getCell(++kollonnenummer).getDateCellValue();
				LocalDate lejeaftalensUdløb = konverterDateTilLocalDate(d3);

				String telefonnummer = row.getCell(++kollonnenummer).getStringCellValue();

				Enum<Studiekontrolstatus> studiekontrolstatus = konverterStringTilEnum(
						row.getCell(++kollonnenummer).getStringCellValue());

				Uddannelse uddannelse = new Uddannelse(uddannelsessted, uddannelsesretning, uddStart, uddSlut);
				Beboer beboer = new Beboer(værelse, navn, uddannelse, indflytning, lejeaftalensUdløb, telefonnummer,
						studiekontrolstatus);
				beboere.add(beboer);

			}
			fis.close();
			workbook.close();
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			System.out.println("Filen kan ikke findes");
			e.printStackTrace();
		}

	}

	/**
	 *
	 *Metoden tager en Excelcelle indeholdende en Date() og konverterer
	 * Date-objektet til et LocalDate Objekt.
	 * 
	 * @param cell
	 *            er cellen indeholdende Datoen.
	 * @return LocalDate objektet.
	 */
	public LocalDate konverterDateTilLocalDate(Date d) {
		// herunder konverteres date til LocalDate
		Instant instant = d.toInstant();
		ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
		LocalDate localDate = zdt.toLocalDate();
		return localDate;
	}

	/**
	 * Metoden konverterer et localdate objekt til et Date() objekt.
	 * 
	 * @param dato
	 *            : datoen der skal konverteres
	 * @return Date : et DateObjekt()
	 */
	public Date konverterLocalDateTilDate(LocalDate dato) {
		Date date = Date.from(dato.atStartOfDay(ZoneId.systemDefault()).toInstant());
		return date;
	}
}
