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
import model.Dispensation;
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
	private ArrayList<Dispensation> dispensationer = new ArrayList<Dispensation>();
	private String filnavn = "IndstillingsInfo.xlsx";

	public ArrayList<Dispensation> getDispensationer() {
		return dispensationer;
	}

	public void setDispensationer(ArrayList<Dispensation> dispensationer) {
		this.dispensationer = dispensationer;
	}

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

		try (FileInputStream fis = new FileInputStream(filnavn)) {// evt. bare et tjek istedet for at oprette fis
			hentBeboereFraExcel(); // beboere oprettes
			hentDeadlinesFraExcel();
			hentFremlejerFraExcel();
			hentDispensationerFraExcel();// Lav
			hentStudiekontrollerfraExcel();
			hentVærelsesudlejningFraExcel();

			fis.close();
		} catch (Exception e) {
			System.out.println("Fil Oprettet");
			e.printStackTrace();

			createExcelFile();
		}

	}

	private void hentDispensationerFraExcel() {
		try {
			FileInputStream fis = new FileInputStream(filnavn);
			Workbook workbook = WorkbookFactory.create(fis);

			int startRække = 1;// Starter på 1 for ikke at tage overskrifter med

			int slutRække = workbook.getSheetAt(1).getLastRowNum();

			for (int i = startRække; i <= slutRække; i++) {
				Row row = workbook.getSheetAt(1).getRow(i);

				int kollonnenummer = 0;

				String værelse = row.getCell(kollonnenummer).getStringCellValue();

				++kollonnenummer; // Tælles op da "navn" ikke skal hentes fra filen, men en hel beboer skal
									// tilføres
				Beboer beboer = findBeboer(værelse);

				Date d = row.getCell(++kollonnenummer).getDateCellValue();
				LocalDate dispStart = konverterDateTilLocalDate(d);

				Date d1 = row.getCell(++kollonnenummer).getDateCellValue();
				LocalDate dispSlut = konverterDateTilLocalDate(d1);

				String dispID = row.getCell(++kollonnenummer).getStringCellValue();

				String deadlinesID = row.getCell(++kollonnenummer).getStringCellValue();

				boolean iGang = row.getCell(++kollonnenummer).getBooleanCellValue();

				ArrayList<Deadline> dispDeadlines = findDispensationsDeadlines(deadlinesID); // Denne skal sættes i gang
																								// ved en metode til at
				// separere '.' og hente tilhørende deadlines

				Dispensation dispensation = new Dispensation(beboer, dispStart, dispSlut, iGang, dispID, dispDeadlines,
						null);
				dispensationer.add(dispensation);

			}
			fis.close();
			workbook.close();
		} catch (EncryptedDocumentException | InvalidFormatException |

				IOException e) {
			System.out.println("Filen kan ikke findes");
			e.printStackTrace();
		}

	}

	/**
	 * Metoden skal anvendes til at finde deadlines der hører til en bestemt
	 * Dispensation.
	 * 
	 * @param deadlinesID
	 *            : String der inderholder samtlige id'er på deadlines tilhørende
	 *            dispensationen separeret med et '-'
	 * @return ArrayList der indeholder deadlines
	 */
	private ArrayList<Deadline> findDispensationsDeadlines(String deadlinesID) {
		ArrayList<Deadline> list = new ArrayList<Deadline>();

		String[] temp = deadlinesID.split("-");

		for (int i = 0; i < temp.length; i++) {
			String s = temp[i];

			for (int j = 0; j < deadlines.size(); j++) {
				if (s.equals(deadlines.get(j).getID())) {
					list.add(deadlines.get(j));
				}
			}
		}
		return list;
	}

	/**
	 * Anvendes til at finde en bestemt beboer i beboerarrayet
	 * 
	 * @param værelsesNummer
	 *            : værelsesnummeret på den beboer der skal findes
	 * @return
	 */
	public Beboer findBeboer(String værelsesNummer) {
		for (int i = 0; i < beboere.size(); i++) {
			if (beboere.get(i).getVærelse().equals(værelsesNummer)) {
				return beboere.get(i);
			}
		}
		return null;

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

				String studiekontrolID = row.getCell(++kollonnenummer).getStringCellValue();

				Studiekontrol studiekontrol = new Studiekontrol(null, afleveringsfrist, påmindelsesdato,
						begyndelsesdato, månedsnummer, afsluttet, studiekontrolID);
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
						// Læg for loop der håndterer fremlejere?
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

				boolean klaret = row.getCell(++kollonnenummer).getBooleanCellValue();

				String ID = row.getCell(++kollonnenummer).getStringCellValue();

				// Sidste del i deadline er null, da der altid vil være et ID på når der loades
				// fra excel
				Deadline deadline = new Deadline(hvem, hvad, hvornår, ID, null);
				deadline.setKlaret(klaret);
				deadlines.add(deadline);

			}
			fis.close();

			workbook.close();
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			System.out.println("Filen kan ikke findes");
			e.printStackTrace();
		}

	}

	public void opretDeadlineIExcel(Deadline deadline) {
		try {
			FileInputStream fis = new FileInputStream(filnavn);
			Workbook workbook = WorkbookFactory.create(fis);
			int startRække = 1;
			int slutRække = workbook.getSheetAt(3).getLastRowNum();
			boolean deadlineFindes = false;

			// Loop gennem excel dokumentet og find rækkepladsen
			for (int i = startRække; i <= slutRække; i++) {
				String s = workbook.getSheetAt(3).getRow(i).getCell(4).getStringCellValue();
				// Hvis det passer, så skriv til værelsesnummeret
				if (deadline.getID() != null) {

					if (s.equals(deadline.getID())) {

						int celleNr = 0;
						workbook.getSheetAt(3).getRow(i).getCell(celleNr).setCellValue(deadline.getHvem());

						workbook.getSheetAt(3).getRow(i).getCell(++celleNr).setCellValue(deadline.getHvad());

						Date d1 = konverterLocalDateTilDate(deadline.getHvornår());
						workbook.getSheetAt(3).getRow(i).getCell(++celleNr).setCellValue(d1);

						workbook.getSheetAt(3).getRow(i).getCell(++celleNr).setCellValue(deadline.isKlaret());

						workbook.getSheetAt(3).getRow(i).getCell(++celleNr).setCellValue(deadline.getID());

						deadlineFindes = true;
					}
				}
			}
			if (deadlineFindes == false) {
				int celleNr = 0;
				workbook.getSheetAt(3).createRow(slutRække + 1);
				workbook.getSheetAt(3).getRow(slutRække + 1).createCell(celleNr).setCellValue(deadline.getHvem());
				workbook.getSheetAt(3).getRow(slutRække + 1).createCell(++celleNr).setCellValue(deadline.getHvad());

				Date d1 = konverterLocalDateTilDate(deadline.getHvornår());
				workbook.getSheetAt(3).getRow(slutRække + 1).createCell(++celleNr).setCellValue(d1);

				workbook.getSheetAt(3).getRow(slutRække + 1).createCell(++celleNr).setCellValue(deadline.isKlaret());

				workbook.getSheetAt(3).getRow(slutRække + 1).createCell(++celleNr).setCellValue(deadline.getID());

			}

			// you have to close the input stream FIRST before writing to the same file.
			fis.close();

			// save your changes to the same file.
			workbook.write(new FileOutputStream(filnavn));
			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// public void redigerDeadlineIExcel(Deadline deadline) { // SKAL FIKSES
	// try (Workbook wb = WorkbookFactory.create(new File(filnavn))) {
	//
	// Sheet sheet = wb.getSheet("Deadlines");
	// int startRække = sheet.getFirstRowNum() + 1;
	// int slutRække = sheet.getLastRowNum();
	//
	// for (int i = startRække; i < slutRække; i++) {
	//
	// Row row = sheet.getRow(i);
	// int idKollonne = 3; // (3 = ID-kollonnen)
	// Cell cell;
	// if (row.getCell(idKollonne).getStringCellValue().equals(deadline.getID())) {
	// int start = 0;
	// cell = row.getCell(start);
	// cell.setCellValue(deadlines.get(i).getHvem());// Hvem
	// cell = row.getCell(++start);
	// cell.setCellValue(deadlines.get(i).getHvad());// Hvad
	// cell = row.getCell(++start);
	// Date hvornår = konverterLocalDateTilDate(deadlines.get(i).getHvornår());//
	// Hvornår
	// cell.setCellValue(hvornår);
	// cell = row.getCell(++start);
	// cell.setCellValue(deadlines.get(i).getID());// ID
	// cell = row.getCell(++start);
	// cell.setCellValue(deadlines.get(i).isKlaret());// Boolean klaret
	//
	// }
	//
	// }
	// FileOutputStream stream = new FileOutputStream(filnavn);
	// wb.write(stream);
	// wb.close();
	//
	// } catch (EncryptedDocumentException | InvalidFormatException | IOException e)
	// {
	// System.out.println("Filen kan ikke findes");
	// e.printStackTrace();
	// }
	// }
	public void opretDispensationIExcel(Dispensation dispensation) {
		try {
			FileInputStream fis = new FileInputStream(filnavn);
			Workbook workbook = WorkbookFactory.create(fis);
			int startRække = 1;
			int slutRække = workbook.getSheetAt(1).getLastRowNum();
			boolean dispensationFindes = false;

			// Loop gennem excel dokumentet og find rækkepladsen
			for (int i = startRække; i <= slutRække; i++) {
				String s = workbook.getSheetAt(1).getRow(i).getCell(4).getStringCellValue();
				// Hvis det passer, så skriv til værelsesnummeret
				if (dispensation.getID() != null) {

					if (s.equals(dispensation.getID())) {
						int celleNr = 0;
						workbook.getSheetAt(1).getRow(i).getCell(celleNr)
								.setCellValue(dispensation.getBeboer().getVærelse());

						workbook.getSheetAt(1).getRow(i).getCell(++celleNr)
								.setCellValue(dispensation.getBeboer().getNavn());

						Date d1 = konverterLocalDateTilDate(dispensation.getStartDato());
						workbook.getSheetAt(1).getRow(i).getCell(++celleNr).setCellValue(d1);

						Date d2 = konverterLocalDateTilDate(dispensation.getSlutDato());
						workbook.getSheetAt(1).getRow(i).getCell(++celleNr).setCellValue(d2);

						workbook.getSheetAt(1).getRow(i).getCell(++celleNr).setCellValue(dispensation.getID());

						String deadlinesStrings = dispensation.getDeadlinesNumbers();
						workbook.getSheetAt(1).getRow(i).getCell(++celleNr).setCellValue(deadlinesStrings);

						workbook.getSheetAt(1).getRow(i).getCell(++celleNr).setCellValue(dispensation.isiGang());

						dispensationFindes = true;
					}
				}
			}
			if (dispensationFindes == false) {

				int celleNr = 0;
				workbook.getSheetAt(1).createRow(slutRække + 1);

				workbook.getSheetAt(1).getRow(slutRække + 1).createCell(celleNr)
						.setCellValue(dispensation.getBeboer().getVærelse());

				workbook.getSheetAt(1).getRow(slutRække + 1).createCell(++celleNr)
						.setCellValue(dispensation.getBeboer().getNavn());

				Date d1 = konverterLocalDateTilDate(dispensation.getStartDato());
				workbook.getSheetAt(1).getRow(slutRække + 1).createCell(++celleNr).setCellValue(d1);

				Date d2 = konverterLocalDateTilDate(dispensation.getSlutDato());
				workbook.getSheetAt(1).getRow(slutRække + 1).createCell(++celleNr).setCellValue(d2);

				workbook.getSheetAt(1).getRow(slutRække + 1).createCell(++celleNr).setCellValue("disp" + slutRække); //

				String deadlinesStrings = dispensation.getDeadlinesNumbers();
				workbook.getSheetAt(1).getRow(slutRække + 1).createCell(++celleNr).setCellValue(deadlinesStrings);

				workbook.getSheetAt(1).getRow(slutRække + 1).createCell(++celleNr).setCellValue(dispensation.isiGang());

			}

			// you have to close the input stream FIRST before writing to the same file.
			fis.close();

			// save your changes to the same file.
			workbook.write(new FileOutputStream(filnavn));
			workbook.close();

		} catch (Exception e) {
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
		row2.createCell(++start).setCellValue("Dispensations ID");
		row2.createCell(++start).setCellValue("Deadline ID'er");
		row2.createCell(++start).setCellValue("I gang");

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
		row5.createCell(++start).setCellValue("afsluttet?");
		row5.createCell(++start).setCellValue("studiekontrol ID");

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
	 * Metoden tjekker først om værelset findes. og ellers skriver den
	 * værelsesnummeret ind nederst på listen
	 * 
	 * @param beboer
	 *            : Den beboer der oprettesi excelfilen
	 */
	public void opretBeboerIExcel(Beboer beboer) { //
		try {
			FileInputStream fis = new FileInputStream(filnavn);
			Workbook workbook = WorkbookFactory.create(fis);
			int startRække = 1;
			int slutRække = workbook.getSheetAt(0).getLastRowNum();
			boolean beboerFindes = false;

			// Loop gennem excel dokumentet og find rækkepladsen
			for (int i = startRække; i <= slutRække; i++) {
				String s = workbook.getSheetAt(0).getRow(i).getCell(0).getStringCellValue();
				// Hvis det passer, så skriv til værelsesnummeret
				if (s.equals(beboer.getVærelse())) {
					int celleNr = 0;
					workbook.getSheetAt(0).getRow(i).getCell(celleNr).setCellValue(beboer.getVærelse());

					workbook.getSheetAt(0).getRow(i).getCell(++celleNr).setCellValue(beboer.getNavn());

					Date d1 = konverterLocalDateTilDate(beboer.getIndflytningsdato());
					workbook.getSheetAt(0).getRow(i).getCell(++celleNr).setCellValue(d1);

					workbook.getSheetAt(0).getRow(i).getCell(++celleNr)
							.setCellValue(beboer.getUddannelse().getUddannelsessted());

					workbook.getSheetAt(0).getRow(i).getCell(++celleNr)
							.setCellValue(beboer.getUddannelse().getUddannelsesretning());

					Date d2 = konverterLocalDateTilDate(beboer.getUddannelse().getPåbegyndtDato());
					workbook.getSheetAt(0).getRow(i).getCell(++celleNr).setCellValue(d2);

					Date d3 = konverterLocalDateTilDate(beboer.getUddannelse().getForventetAfsluttetDato());
					workbook.getSheetAt(0).getRow(i).getCell(++celleNr).setCellValue(d3);

					Date d4 = konverterLocalDateTilDate(beboer.getLejeaftalensUdløb());
					workbook.getSheetAt(0).getRow(i).getCell(++celleNr).setCellValue(d4);

					workbook.getSheetAt(0).getRow(i).getCell(++celleNr).setCellValue(beboer.getTelefonnummer());

					String s1 = konverterEnumTilString((Studiekontrolstatus) beboer.getStudiekontrolstatus());
					workbook.getSheetAt(0).getRow(i).getCell(++celleNr).setCellValue(s1);

					beboerFindes = true;

				}
			}
			if (beboerFindes == false) {

				workbook.getSheetAt(0).createRow(slutRække + 1);
				workbook.getSheetAt(0).getRow(slutRække + 1).createCell(0).setCellValue(beboer.getVærelse());
				workbook.getSheetAt(0).getRow(slutRække + 1).createCell(1).setCellValue(beboer.getNavn());

				Date d1 = konverterLocalDateTilDate(beboer.getIndflytningsdato());
				workbook.getSheetAt(0).getRow(slutRække + 1).createCell(2).setCellValue(d1);


				workbook.getSheetAt(0).getRow(slutRække + 1).createCell(3)
						.setCellValue(beboer.getUddannelse().getUddannelsessted());

				workbook.getSheetAt(0).getRow(slutRække + 1).createCell(4)
						.setCellValue(beboer.getUddannelse().getUddannelsesretning());

				Date d2 = konverterLocalDateTilDate(beboer.getUddannelse().getPåbegyndtDato());
				workbook.getSheetAt(0).getRow(slutRække + 1).createCell(5).setCellValue(d2);

				Date d3 = konverterLocalDateTilDate(beboer.getUddannelse().getForventetAfsluttetDato());
				workbook.getSheetAt(0).getRow(slutRække + 1).createCell(6).setCellValue(d3);

				Date d4 = konverterLocalDateTilDate(beboer.getLejeaftalensUdløb());
				workbook.getSheetAt(0).getRow(slutRække + 1).createCell(7).setCellValue(d4);

				workbook.getSheetAt(0).getRow(slutRække + 1).createCell(8).setCellValue(beboer.getTelefonnummer());

				String s1 = konverterEnumTilString((Studiekontrolstatus) beboer.getStudiekontrolstatus());
				workbook.getSheetAt(0).getRow(slutRække + 1).createCell(9).setCellValue(s1);
			}

			// you have to close the input stream FIRST before writing to the same file.
			fis.close();

			// save your changes to the same file.
			workbook.write(new FileOutputStream(filnavn));
			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void opretVærelsesudlejning(Værelsesudlejning værelsesudlejning) {
		try {
			FileInputStream fis = new FileInputStream(filnavn);
			Workbook workbook = WorkbookFactory.create(fis);
			int startRække = 1;
			int slutRække = workbook.getSheetAt(5).getLastRowNum();
			boolean værelsesudlejningFindes = false;

			// Loop gennem excel dokumentet og find rækkepladsen
			for (int i = startRække; i <= slutRække; i++) {
				String sVærelse = workbook.getSheetAt(5).getRow(i).getCell(1).getStringCellValue(); // Hvilken celle?
				String sNavn = workbook.getSheetAt(5).getRow(i).getCell(2).getStringCellValue(); // Hvilken celle?

				// Hvis det passer, så skriv til værelsesnummeret
				if (sVærelse.equals(værelsesudlejning.getVærelse())) {
					if(sNavn == null){
						int celleNr = 0;

						Date d1 = konverterLocalDateTilDate(værelsesudlejning.getindflytningsdato());
						workbook.getSheetAt(5).getRow(i).getCell(celleNr).setCellValue(d1);

						++celleNr; //Går hen over værelsesnummeret

						workbook.getSheetAt(5).getRow(i).getCell(++celleNr)
								.setCellValue(værelsesudlejning.getNavn());

						Date d2 = konverterLocalDateTilDate(værelsesudlejning.getBehandlingsdato());
						workbook.getSheetAt(5).getRow(i).getCell(++celleNr).setCellValue(d2);

						workbook.getSheetAt(5).getRow(i).getCell(++celleNr).setCellValue(værelsesudlejning.getBehandlerInitialer());

						værelsesudlejningFindes = true;

					}
				}
			}
			if (værelsesudlejningFindes == false) {
				workbook.getSheetAt(5).createRow(slutRække + 1);
				int celleNr = 0;

				Date d1 = konverterLocalDateTilDate(værelsesudlejning.getindflytningsdato());
				workbook.getSheetAt(5).getRow(slutRække + 1).createCell(celleNr).setCellValue(d1);

				workbook.getSheetAt(5).getRow(slutRække + 1).createCell(++celleNr).setCellValue(værelsesudlejning.getVærelse());

				workbook.getSheetAt(5).getRow(slutRække + 1).getCell(++celleNr).setCellValue("");//Navn er null hvis værelset ikke skal udlejes

				Date d2 = konverterLocalDateTilDate(værelsesudlejning.getBehandlingsdato());
				workbook.getSheetAt(5).getRow(slutRække + 1).createCell(++celleNr)
						.setCellValue(d2);

				workbook.getSheetAt(5).getRow(slutRække + 1).createCell(++celleNr)
						.setCellValue(""); //Behandler initialer er = "" hvis ikke det er blevet udlejet endnu

			}

			// you have to close the input stream FIRST before writing to the same file.
			fis.close();

			// save your changes to the same file.
			workbook.write(new FileOutputStream(filnavn));
			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void opretStudiekontrollerIExcel(Studiekontrol studiekontrol) {
		try {
			FileInputStream fis = new FileInputStream(filnavn);
			Workbook workbook = WorkbookFactory.create(fis);
			int startRække = 1;
			int slutRække = workbook.getSheetAt(4).getLastRowNum();
			boolean studiekontrolFindes = false;

			// Loop gennem excel dokumentet og find rækkepladsen
			for (int i = startRække; i <= slutRække; i++) {
				String s = workbook.getSheetAt(4).getRow(i).getCell(5).getStringCellValue();

				// Hvis det passer, så skriv til værelsesnummeret
				if (s.equals(studiekontrol.getStudiekontrolID())) {
					int celleNr = 0;

					Date d1 = konverterLocalDateTilDate(studiekontrol.getAfleveringsfrist());
					workbook.getSheetAt(4).getRow(i).getCell(celleNr).setCellValue(d1);

					Date d2 = konverterLocalDateTilDate(studiekontrol.getPåmindelse());
					workbook.getSheetAt(4).getRow(i).getCell(++celleNr).setCellValue(d2);

					Date d3 = konverterLocalDateTilDate(studiekontrol.getPåbegyndelsesdato());
					workbook.getSheetAt(4).getRow(i).getCell(++celleNr).setCellValue(d3);

					workbook.getSheetAt(4).getRow(i).getCell(++celleNr).setCellValue(studiekontrol.getMånedsnummer());

					workbook.getSheetAt(4).getRow(i).getCell(++celleNr).setCellValue(studiekontrol.isAfsluttet());

					// workbook.getSheetAt(4).getRow(i).getCell(++celleNr).setCellValue(); - Behøver
					// ikke overskrive ID'et

					studiekontrolFindes = true;

				}
			}
			if (studiekontrolFindes == false) {
				workbook.getSheetAt(4).createRow(slutRække + 1);
				int celleNr = 0;

				Date d1 = konverterLocalDateTilDate(studiekontrol.getAfleveringsfrist());
				workbook.getSheetAt(4).getRow(slutRække + 1).createCell(celleNr).setCellValue(d1);

				Date d2 = konverterLocalDateTilDate(studiekontrol.getPåmindelse());
				workbook.getSheetAt(4).getRow(slutRække + 1).createCell(++celleNr).setCellValue(d2);

				Date d3 = konverterLocalDateTilDate(studiekontrol.getPåbegyndelsesdato());
				workbook.getSheetAt(4).getRow(slutRække + 1).getCell(++celleNr).setCellValue(d3);

				workbook.getSheetAt(4).getRow(slutRække + 1).createCell(++celleNr)
						.setCellValue(studiekontrol.getMånedsnummer());

				workbook.getSheetAt(4).getRow(slutRække + 1).createCell(++celleNr)
						.setCellValue(studiekontrol.isAfsluttet());

				workbook.getSheetAt(4).getRow(slutRække + 1).createCell(++celleNr)
						.setCellValue(studiekontrol.getStudiekontrolID());
			}

			// you have to close the input stream FIRST before writing to the same file.
			fis.close();

			// save your changes to the same file.
			workbook.write(new FileOutputStream(filnavn));
			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void opretFremlejerIExcel(Beboer beboer) { //
		try {
			FileInputStream fis = new FileInputStream(filnavn);
			Workbook workbook = WorkbookFactory.create(fis);
			int startRække = 1;
			int slutRække = workbook.getSheetAt(2).getLastRowNum();
			boolean beboerFindes = false;

			// Loop gennem excel dokumentet og find rækkepladsen
			for (int i = startRække; i <= slutRække; i++) {
				String s = workbook.getSheetAt(2).getRow(i).getCell(0).getStringCellValue();
				// Hvis det passer, så skriv til værelsesnummeret
				if (s.equals(beboer.getVærelse())) {
					int celleNr = 0;
					workbook.getSheetAt(2).getRow(i).getCell(celleNr).setCellValue(beboer.getVærelse());

					workbook.getSheetAt(2).getRow(i).getCell(++celleNr).setCellValue(beboer.getNavn());

					Date d1 = konverterLocalDateTilDate(beboer.getIndflytningsdato());
					workbook.getSheetAt(2).getRow(i).getCell(++celleNr).setCellValue(d1);

					workbook.getSheetAt(2).getRow(i).getCell(++celleNr)
							.setCellValue(beboer.getUddannelse().getUddannelsessted());

					workbook.getSheetAt(2).getRow(i).getCell(++celleNr)
							.setCellValue(beboer.getUddannelse().getUddannelsesretning());

					Date d2 = konverterLocalDateTilDate(beboer.getUddannelse().getPåbegyndtDato());
					workbook.getSheetAt(2).getRow(i).getCell(++celleNr).setCellValue(d2);

					Date d3 = konverterLocalDateTilDate(beboer.getUddannelse().getForventetAfsluttetDato());
					workbook.getSheetAt(2).getRow(i).getCell(++celleNr).setCellValue(d3);

					Date d4 = konverterLocalDateTilDate(beboer.getLejeaftalensUdløb());
					workbook.getSheetAt(2).getRow(i).getCell(++celleNr).setCellValue(d4);

					workbook.getSheetAt(2).getRow(i).getCell(++celleNr).setCellValue(beboer.getTelefonnummer());

					String s1 = konverterEnumTilString((Studiekontrolstatus) beboer.getStudiekontrolstatus());
					workbook.getSheetAt(2).getRow(i).getCell(++celleNr).setCellValue(s1);

					beboerFindes = true;

				}
			}
			if (beboerFindes == false) {

				workbook.getSheetAt(2).createRow(slutRække + 1);
				workbook.getSheetAt(2).getRow(slutRække + 1).createCell(0).setCellValue(beboer.getVærelse());
				workbook.getSheetAt(2).getRow(slutRække + 1).createCell(1).setCellValue(beboer.getNavn());

				Date d1 = konverterLocalDateTilDate(beboer.getIndflytningsdato());
				workbook.getSheetAt(2).getRow(slutRække + 1).createCell(2).setCellValue(d1);

				workbook.getSheetAt(2).getRow(slutRække + 1).createCell(3)
						.setCellValue(beboer.getUddannelse().getUddannelsessted());

				workbook.getSheetAt(2).getRow(slutRække + 1).createCell(4)
						.setCellValue(beboer.getUddannelse().getUddannelsesretning());

				Date d2 = konverterLocalDateTilDate(beboer.getUddannelse().getPåbegyndtDato());
				workbook.getSheetAt(2).getRow(slutRække + 1).createCell(5).setCellValue(d2);

				Date d3 = konverterLocalDateTilDate(beboer.getUddannelse().getForventetAfsluttetDato());
				workbook.getSheetAt(2).getRow(slutRække + 1).createCell(6).setCellValue(d3);

				Date d4 = konverterLocalDateTilDate(beboer.getLejeaftalensUdløb());
				workbook.getSheetAt(2).getRow(slutRække + 1).createCell(7).setCellValue(d4);

				workbook.getSheetAt(2).getRow(slutRække + 1).createCell(8).setCellValue(beboer.getTelefonnummer());

				String s1 = konverterEnumTilString((Studiekontrolstatus) beboer.getStudiekontrolstatus());
				workbook.getSheetAt(2).getRow(slutRække + 1).createCell(9).setCellValue(s1);
			}

			// you have to close the input stream FIRST before writing to the same file.
			fis.close();

			// save your changes to the same file.
			workbook.write(new FileOutputStream(filnavn));
			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public ArrayList<Beboer> getBeboere() {

		return beboere;
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
				LocalDate indflytning = konverterDateTilLocalDate(d);// HERRRR

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
				Beboer beboer = new Beboer(navn, værelse, uddannelse, indflytning, lejeaftalensUdløb, telefonnummer,
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
	 * Metoden tager en Excelcelle indeholdende en Date() og konverterer
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
