package view;

import java.time.LocalDate;
import java.util.ArrayList;

import controller.ExcelConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Beboer;
import model.Deadline;
import model.Dispensation;
import model.Studiekontrolstatus;
import model.Værelsesudlejning;

public class GUI_PopUps {
	// ExcelConnection ec = new ExcelConnection();
	private Stage stage = new Stage();
	private GUI_PopUps_Deadlines popUpDead = new GUI_PopUps_Deadlines();

	public void opretNyBeboeroplysninger(ExcelConnection ec, TableView<Beboer> tView1, TableView<Beboer> tView2,
			TableView<Beboer> tView3, TableView<Beboer> tView4, TableView<Beboer> tView5, TableView<Beboer> tView6) {
		stage.setTitle("Rediger beboeroplysninger");
		// stage.initModality(Modality.APPLICATION_MODAL);

		GridPane layout = new GridPane();

		Label l1 = new Label("Værelse:");
		Label l2 = new Label("Navn:");
		Label l3 = new Label("Indflytningsdato:");
		Label l4 = new Label("Telefonnummer:");
		Label l5 = new Label("Uddannelsessted");
		Label l6 = new Label("Uddannelsesretning");
		Label l7 = new Label("Uddannelse påbegyndt");
		Label l8 = new Label("Forventet uddannelsesafslutning");
		Label l9 = new Label("Lejeaftalens udløb:");
		Label l10 = new Label("Studiekontrol status:");

		TextField værelse = new TextField();
		TextField navn = new TextField();
		DatePicker indflytningsdato = new DatePicker();
		TextField telefonnummer = new TextField();
		TextField uddannelsessted = new TextField();
		TextField uddannelsesretning = new TextField();
		DatePicker uddStart = new DatePicker();
		DatePicker uddSlut = new DatePicker();
		DatePicker lejeaftalensUdløb = new DatePicker();
		ComboBox<String> studiekontrolStatus = new ComboBox<String>();
		studiekontrolStatus.getItems().addAll("Ikke i gang", "Modtaget, ikke godkendt", "Ikke Modtaget",
				"Sendt til boligselskab", "Godkendt");

		Button gemButton = new Button("Opret beboer");
		gemButton.setOnAction(e -> {
			Studiekontrolstatus status = (Studiekontrolstatus) ec
					.konverterStringTilEnum(studiekontrolStatus.getValue());
			Beboer b = new Beboer(navn.getText(), værelse.getText(), indflytningsdato.getValue(),
					lejeaftalensUdløb.getValue(), telefonnummer.getText(), status, uddannelsessted.getText(),
					uddannelsesretning.getText(), uddStart.getValue(), uddSlut.getValue());

			ec.opretBeboerIExcel(b);
			ec.getBeboere().clear();
			ec.hentBeboereFraExcel();

			tView1.getItems().add(b);
			tView1.refresh();

			char c = værelse.getText().charAt(0);
			switch (c) {
			case '2':
				tView2.getItems().add(b);
				tView2.refresh();
				break;
			case '3':
				tView3.getItems().add(b);
				tView3.refresh();
				break;
			case '4':
				tView4.getItems().add(b);
				tView4.refresh();
				break;
			case '5':
				tView5.getItems().add(b);
				tView5.refresh();
				break;
			case '6':
				tView6.getItems().add(b);
				tView6.refresh();
				break;
			default:
				System.out.println("Værelset findes ikke");
			}

			stage.close();
		});
		Button annullerButton = new Button("Annuller");
		annullerButton.setOnAction(e -> {
			stage.close();
		});

		layout.add(l1, 3, 3);
		layout.add(l2, 3, 6);
		layout.add(l3, 3, 9);
		layout.add(l4, 3, 12);
		layout.add(l5, 3, 15);
		layout.add(l6, 3, 18);
		layout.add(l7, 3, 21);
		layout.add(l8, 3, 24);
		layout.add(l9, 3, 27);
		layout.add(l10, 3, 30);

		// Sætter tekstfelter og datepicker på layout
		layout.add(værelse, 5, 3);
		layout.add(navn, 5, 6);
		layout.add(indflytningsdato, 5, 9);
		layout.add(telefonnummer, 5, 12);
		layout.add(uddannelsessted, 5, 15);
		layout.add(uddannelsesretning, 5, 18);
		layout.add(uddStart, 5, 21);
		layout.add(uddSlut, 5, 24);
		layout.add(lejeaftalensUdløb, 5, 27);
		layout.add(studiekontrolStatus, 5, 30);

		// Sætter buttons på layout
		layout.add(gemButton, 3, 33);
		layout.add(annullerButton, 5, 33);
		layout.setPrefSize(500, 700);

		Scene scene = new Scene(layout);
		stage.setScene(scene);
		stage.showAndWait();
	}

	public void opretNyFremlejer(ExcelConnection ec, TableView<Beboer> tView) {
		stage.setTitle("Opret Fremlejer");
		// stage.initModality(Modality.APPLICATION_MODAL);

		GridPane layout = new GridPane();

		Label l1 = new Label("Værelse:");
		Label l2 = new Label("Navn:");
		Label l3 = new Label("Indflytningsdato:");
		Label l4 = new Label("Telefonnummer:");
		Label l5 = new Label("Uddannelsessted");
		Label l6 = new Label("Uddannelsesretning");
		Label l7 = new Label("Uddannelse påbegyndt");
		Label l8 = new Label("Forventet uddannelsesafslutning");
		Label l9 = new Label("Lejeaftalens udløb:");

		TextField værelse = new TextField();
		TextField navn = new TextField();
		DatePicker indflytningsdato = new DatePicker();
		TextField telefonnummer = new TextField();
		TextField uddannelsessted = new TextField();
		TextField uddannelsesretning = new TextField();
		DatePicker uddStart = new DatePicker();
		DatePicker uddSlut = new DatePicker();
		DatePicker lejeaftalensUdløb = new DatePicker();

		Button gemButton = new Button("Opret beboer");
		gemButton.setOnAction(e -> {
			Beboer b = new Beboer(navn.getText(), værelse.getText(), indflytningsdato.getValue(),
					lejeaftalensUdløb.getValue(), telefonnummer.getText(), Studiekontrolstatus.IKKEIGANG,
					uddannelsessted.getText(), uddannelsesretning.getText(), uddStart.getValue(), uddSlut.getValue());

			ec.opretFremlejerIExcel(b);
			ec.getFremlejere().clear();
			ec.hentFremlejerFraExcel();
			tView.getItems().add(b);
			tView.refresh();
			stage.close();
		});
		Button annullerButton = new Button("Annuller");
		annullerButton.setOnAction(e -> {
			stage.close();
		});

		layout.add(l1, 3, 3);
		layout.add(l2, 3, 6);
		layout.add(l3, 3, 9);
		layout.add(l4, 3, 12);
		layout.add(l5, 3, 15);
		layout.add(l6, 3, 18);
		layout.add(l7, 3, 21);
		layout.add(l8, 3, 24);
		layout.add(l9, 3, 27);

		// Sætter tekstfelter og datepicker på layout
		layout.add(værelse, 5, 3);
		layout.add(navn, 5, 6);
		layout.add(indflytningsdato, 5, 9);
		layout.add(telefonnummer, 5, 12);
		layout.add(uddannelsessted, 5, 15);
		layout.add(uddannelsesretning, 5, 18);
		layout.add(uddStart, 5, 21);
		layout.add(uddSlut, 5, 24);
		layout.add(lejeaftalensUdløb, 5, 27);

		// Sætter buttons på layout
		layout.add(gemButton, 3, 33);
		layout.add(annullerButton, 5, 33);
		layout.setPrefSize(500, 700);

		Scene scene = new Scene(layout);
		stage.setScene(scene);
		stage.showAndWait();
	}

	@SuppressWarnings("unchecked")
	public void opretDispensation(ExcelConnection ec, TableView<Dispensation> tableView,
			TableView<Deadline> tViewHMenu) {// TableView<Dispensation> tView -> anavendes evt. hvis hovedmenu deadlines
												// også skal opdateres
		stage.setTitle("Rediger beboeroplysninger");
		// stage.initModality(Modality.APPLICATION_MODAL);

		GridPane layout = new GridPane();

		Label l1 = new Label("Værelse:");
		Label l2 = new Label("Navn:");
		Label l3 = new Label("Begrundelse for dispensation:");
		Label l4 = new Label("Start dato:");
		Label l5 = new Label("Slut dato");
		Label l6 = new Label("Betingelser");
		Label l7 = new Label("Indstillingsformandens navn");
		// Label l8 = new Label("Forventet uddannelsesafslutning");
		// Label l9 = new Label("Lejeaftalens udløb:");
		// Label l10 = new Label("Studiekontrol status:");

		TextField værelse = new TextField();
		TextField navn = new TextField();
		TextArea begrundelse = new TextArea();
		DatePicker startDato = new DatePicker();
		DatePicker slutDato = new DatePicker();
		TextField formandsNavn = new TextField();
		ComboBox<String> studiekontrolStatus = new ComboBox<String>();

		studiekontrolStatus.getItems().addAll("Ikke i gang", "Modtaget, ikke godkendt", "Ikke Modtaget",
				"Sendt til boligselskab", "Godkendt");

		// Venstre side af menuen

		TableColumn<Deadline, LocalDate> hvornårColumn = new TableColumn<Deadline, LocalDate>("Dato");
		hvornårColumn.setCellValueFactory(new PropertyValueFactory<>("hvornår")); // ændr i format så dato bliver:
																					// dd/MM/YYYY
		TableColumn<Deadline, String> hvadColumn = new TableColumn<Deadline, String>("Hvad:");
		hvadColumn.setCellValueFactory(new PropertyValueFactory<>("hvad"));
		TableColumn<Deadline, String> hvemColumn = new TableColumn("Hvem:");
		hvemColumn.setCellValueFactory(new PropertyValueFactory<>("hvem"));

		TableView<Deadline> tView = new TableView<Deadline>();
		// tView.setItems(getDeadlines());

		tView.getColumns().addAll(hvornårColumn, hvadColumn, hvemColumn);

		// Herunder oprettes en metode til at håndtere dobbeltklik og hente objekter
		// (hel række)
		tView.setRowFactory(tv -> {
			TableRow<Deadline> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {

					Deadline clickedRow = row.getItem();
					popUpDead.changeDeadline(clickedRow, ec, tView);
					// tView.refresh();
				}
			});
			return row;
		});
		tView.setRowFactory(tv -> {
			TableRow<Deadline> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {

					// Deadline clickedRow = row.getItem();
					popUpDead.createDeadline(ec, tView);
					// tView.refresh();
				}
			});
			return row;
		});
		// Test af om det kan gemmes på arrayliste uden at returne
		// ArrayList<Deadline> list = new ArrayList<Deadline>();

		Button tilføjButton = new Button("Tilføj deadline");
		tilføjButton.setOnAction(event -> popUpDead.createDeadline(ec, tView));

		Button fjernButton = new Button("Fjern deadline");
		fjernButton.setOnAction(event -> {
			ObservableList<Deadline> deadlineValgt, alleDeadlines;
			alleDeadlines = tView.getItems();
			deadlineValgt = tView.getSelectionModel().getSelectedItems();
			Deadline d = tView.getSelectionModel().getSelectedItem();
			d.setKlaret(true);
			ec.opretDeadlineIExcel(d);
			deadlineValgt.forEach(alleDeadlines::remove);

		});

		// Bunden af menuen
		Button gemButton = new Button("Opret Dispensation");
		gemButton.setOnAction(e -> {
			ObservableList<Deadline> list = tView.getItems();
			ArrayList<Deadline> listDeads = new ArrayList<Deadline>();
			// Deadlines
			for (Deadline d : list) { // Virker
				Deadline deadline = new Deadline(d.getHvem(), d.getHvad(), d.getHvornår(), null, ec);
				ec.opretDeadlineIExcel(deadline);
				ec.getDeadlines().clear();
				ec.hentDeadlinesFraExcel();
				listDeads.add(ec.getDeadlines().get(ec.getDeadlines().size() - 1));
			}

			// Tjekker om dispensationen er i gang - Bør måske være igangværende og kommende
			// dispensationer?
			boolean iGang = false;
			if (startDato.getValue().isBefore(LocalDate.now()) || startDato.getValue().equals(LocalDate.now())) {

				if (slutDato.getValue().isAfter(LocalDate.now()) || slutDato.getValue().equals(LocalDate.now())) {
					iGang = true;
				}
			}

			Beboer b = ec.findBeboer(værelse.getText());
			Dispensation disp = new Dispensation(b, startDato.getValue(), slutDato.getValue(), iGang, null, listDeads,
					ec);

			ec.opretDispensationIExcel(disp);
			ec.getDispensationer().clear();
			ec.hentDispensationerFraExcel();
			tableView.getItems().add(disp);
			tableView.refresh();

			stage.close();
		});
		Button annullerButton = new Button("Annuller");
		annullerButton.setOnAction(e -> {

			stage.close();
		});

		// Højre layout
		layout.add(l1, 7, 3);
		layout.add(l2, 7, 6);
		layout.add(l3, 7, 9);
		layout.add(l4, 7, 12);
		layout.add(l5, 7, 15);
		layout.add(l7, 7, 18);

		// Sætter tekstfelter og datepicker på layout
		layout.add(værelse, 9, 3);
		layout.add(navn, 9, 6);
		layout.add(begrundelse, 9, 9);
		layout.add(startDato, 9, 12);
		layout.add(slutDato, 9, 15);
		layout.add(formandsNavn, 9, 18);
		// layout.add(studiekontrolStatus, 9, 30);

		layout.add(l6, 2, 3, 3, 1);
		layout.add(tView, 2, 5, 3, 10);
		layout.add(tilføjButton, 2, 20);
		layout.add(fjernButton, 4, 20);

		// Sætter Bunden af layout
		layout.add(gemButton, 7, 24);
		layout.add(annullerButton, 9, 24);

		layout.setPrefSize(500, 700);

		Scene scene = new Scene(layout);
		stage.setScene(scene);
		stage.showAndWait();
	}

	public void redigerBeboeroplysninger(Beboer beboer, ExcelConnection ec, TableView<Beboer> tView) {
		stage.setTitle("Rediger beboeroplysninger");
		// stage.initModality(Modality.APPLICATION_MODAL);

		GridPane layout = new GridPane();

		Label l1 = new Label("Værelse:");
		Label l2 = new Label("Navn:");
		Label l3 = new Label("Indflytningsdato:");
		Label l4 = new Label("Telefonnummer:");
		Label l5 = new Label("Uddannelsessted");
		Label l6 = new Label("Uddannelsesretning");
		Label l7 = new Label("Uddannelse påbegyndt");
		Label l8 = new Label("Forventet uddannelsesafslutning");
		Label l9 = new Label("Lejeaftalens udløb:");

		TextField værelse = new TextField();
		TextField navn = new TextField();
		DatePicker indflytningsdato = new DatePicker();
		TextField telefonnummer = new TextField();
		TextField uddannelsessted = new TextField();
		TextField uddannelsesretning = new TextField();
		DatePicker uddStart = new DatePicker();
		DatePicker uddSlut = new DatePicker();
		DatePicker lejeaftalensUdløb = new DatePicker();
		// ComboBox<String> studiekontrolStatus = new ComboBox<String>();
		// studiekontrolStatus.getItems().addAll("Ikke i gang", "Modtaget, ikke
		// godkendt", "Ikke Modtaget", "Sendt til boligselskab", "Godkendt");

		værelse.setText(beboer.getVærelse());
		navn.setText(beboer.getNavn());
		indflytningsdato.setValue(beboer.getIndflytningsdato());
		telefonnummer.setText(beboer.getTelefonnummer());
		uddannelsessted.setText(beboer.getUddannelsessted());
		uddannelsesretning.setText(beboer.getUddannelsesretning());
		uddStart.setValue(beboer.getPåbegyndtDato());
		uddSlut.setValue(beboer.getForventetAfsluttetDato());
		lejeaftalensUdløb.setValue(beboer.getLejeaftalensUdløb());
		// studiekontrolStatus.setValue(ec.konverterEnumTilString((Studiekontrolstatus)
		// beboer.getStudiekontrolstatus()));

		Button gemButton = new Button("Gem ændringer");
		gemButton.setOnAction(e -> {
			// Studiekontrolstatus status = (Studiekontrolstatus)
			// ec.konverterStringTilEnum(studiekontrolStatus.getValue());
			beboer.setNavn(navn.getText());
			beboer.setVærelse(værelse.getText());
			beboer.setIndflytningsdato(indflytningsdato.getValue());
			beboer.setLejeaftalensUdløb(lejeaftalensUdløb.getValue());
			beboer.setTelefonnummer(telefonnummer.getText());
			// status,
			beboer.setUddannelsessted(uddannelsessted.getText());
			beboer.setUddannelsesretning(uddannelsesretning.getText());
			beboer.setPåbegyndtDato(uddStart.getValue());
			beboer.setForventetAfsluttetDato(uddSlut.getValue());

			ec.opretBeboerIExcel(beboer);
			ec.getBeboere().clear();
			ec.hentBeboereFraExcel();
			tView.refresh();
			stage.close();
		});
		Button annullerButton = new Button("Annuller");
		annullerButton.setOnAction(e -> {
			stage.close();
		});

		layout.add(l1, 3, 3);
		layout.add(l2, 3, 6);
		layout.add(l3, 3, 9);
		layout.add(l4, 3, 12);
		layout.add(l5, 3, 15);
		layout.add(l6, 3, 18);
		layout.add(l7, 3, 21);
		layout.add(l8, 3, 24);
		layout.add(l9, 3, 27);

		// Sætter tekstfelter og datepicker på layout
		layout.add(værelse, 5, 3);
		layout.add(navn, 5, 6);
		layout.add(indflytningsdato, 5, 9);
		layout.add(telefonnummer, 5, 12);
		layout.add(uddannelsessted, 5, 15);
		layout.add(uddannelsesretning, 5, 18);
		layout.add(uddStart, 5, 21);
		layout.add(uddSlut, 5, 24);
		layout.add(lejeaftalensUdløb, 5, 27);

		// Sætter buttons på layout
		layout.add(gemButton, 3, 30);
		layout.add(annullerButton, 5, 30);
		layout.setPrefSize(500, 700);

		Scene scene = new Scene(layout);
		stage.setScene(scene);
		stage.showAndWait();
	}

	public void startStudiekontrol() {
		stage.setTitle("Påbegynd studiekontrol");

		GridPane layout = new GridPane();
		layout.setPrefSize(600, 300);

		// Valgmuligheder og labels for at påbegynde studiekontrol oprettes

		Label l1 = new Label("Vælg måned der påbegyndes\nstudiekontrol for:");
		Label l2 = new Label("Påmindelsesdato");
		Label l3 = new Label("Afleveringsfrist/Afslutningsdato");

		DatePicker påmindelsesdato = new DatePicker();
		DatePicker afleveringsfrist = new DatePicker();
		ComboBox<String> udløbsmåned = new ComboBox<String>();
		udløbsmåned.getItems().addAll("Januar", "Februar", "Marts", "April", "Maj", "Juni", "Juli", "August",
				"September", "Oktober", "November", "December");
		udløbsmåned.setPromptText("Vælg Måned");

		Button påbegyndButton = new Button("Påbegynd studiekontrol");
		påbegyndButton.setOnAction(e -> System.out.println("Jeg er ikke implementeret endnu"));
		Button annullerButton = new Button("Annuller");
		annullerButton.setOnAction(e -> stage.close());
		layout.add(l1, 3, 3);
		layout.add(l2, 6, 3);
		layout.add(l3, 9, 3);
		layout.add(udløbsmåned, 3, 5);
		layout.add(påmindelsesdato, 6, 5);
		layout.add(afleveringsfrist, 9, 5);

		layout.add(påbegyndButton, 6, 7);
		layout.add(annullerButton, 9, 7);

		Scene scene = new Scene(layout);
		stage.setScene(scene);
		stage.show();

	}

	/**
	 * popUp menu til at tjekke studiekontrol af.
	 */
	public void modtagetStudiekontrol() {
		stage.setTitle("Studiekontrol for beboer:");

		GridPane layout = new GridPane();

		Label l1 = new Label("Værelse");
		Label l2 = new Label("Navn");
		Label l3 = new Label("Uddannelsessted");
		Label l4 = new Label("Uddannelsesretning");
		Label l5 = new Label("uddannelse påbegyndt:");
		Label l6 = new Label("Forventet studieafslutning:");
		Label l7 = new Label("Status på studiedokumentation");

		TextField værelse = new TextField();
		TextField navn = new TextField();
		TextField uddannelsessted = new TextField();
		TextField uddannelsesretning = new TextField();
		DatePicker uddStart = new DatePicker();
		DatePicker uddSlut = new DatePicker();
		ComboBox<String> kontrolstatus = new ComboBox<String>();
		kontrolstatus.getItems().addAll("Godkendt", "Modtaget. Ikke godkendt", "Ikke modtaget");
		kontrolstatus.setPromptText("Status...");

		Button gemButton = new Button("Gem ændringer");
		Button annulerButton = new Button("Annuller");

		layout.add(l1, 3, 3);
		layout.add(l2, 3, 5);
		layout.add(l3, 3, 7);
		layout.add(l4, 3, 9);
		layout.add(l5, 3, 11);
		layout.add(l6, 3, 13);
		layout.add(l7, 3, 15);

		layout.add(værelse, 5, 3);
		layout.add(navn, 5, 5);
		layout.add(uddannelsessted, 5, 7);
		layout.add(uddannelsesretning, 5, 9);
		layout.add(uddStart, 5, 11);
		layout.add(uddSlut, 5, 13);
		layout.add(kontrolstatus, 5, 15);

		layout.add(gemButton, 4, 17);
		layout.add(annulerButton, 6, 17);

		Scene scene = new Scene(layout);
		stage.setScene(scene);
		stage.show();
	}

	public void afslutStudiekontrol(String måned, ArrayList arrayList) {
		stage.setTitle("Afslut studiekontrol for " + måned);
		GridPane layout = new GridPane();

		Label l1 = new Label("Er du sikker på du vil afslutte studiekontrollen for " + måned
				+ "\nog sende de herunder listede beboeres oplysninger videre til boligselskabet?");
		Button jaButton = new Button("Ja");
		Button nejButton = new Button("Nej");

		TableColumn værelse = new TableColumn();
		TableColumn navn = new TableColumn();

		TableView<Beboer> tView = new TableView();
		tView.getColumns().addAll(værelse, navn);

		layout.add(l1, 3, 3, 5, 1);

		layout.add(tView, 3, 4, 3, 1);
		layout.add(jaButton, 4, 6);
		layout.add(nejButton, 6, 6);

		Scene scene = new Scene(layout);
		stage.setScene(scene);
		stage.show();
	}

	public void værelsesUdlejning(Værelsesudlejning clickedRow, ExcelConnection ec, TableView<Værelsesudlejning> tW1) {
		// TODO Auto-generated method stub

	}

	public void redigerDispensation(Dispensation clickedRow, ExcelConnection ec, TableView<Dispensation> tView,
			TableView<Deadline> tViewHMenu) {
		// TODO Auto-generated method stub

	}

}
