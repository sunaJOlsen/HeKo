package model;

import java.time.LocalDate;

public class Beboer {
private String navn;
private String værelse;
private Uddannelse uddannelse;
private LocalDate indflytningsdato;
private LocalDate lejeaftalensUdløb;
private String telefonnummer;

/**
 * @param navn : Beboerens navn
 * @param værelse : Værelsesnummer
 * @param uddannelse : Uddannelsesinformationer
 * @param indflytningsdato : Dato beboeren har lejemålet fra.
 * @param lejeAftalensUdløb : datoen der kan komme til at danne grundlag for hvornår der påbegynes studiekontrol for beboeren
 */
public Beboer(String navn, String værelse, Uddannelse uddannelse, LocalDate indflytningsdato,
		LocalDate lejeaftalensUdløb, String telefonnummer) {
	this.navn = navn;
	this.værelse = værelse;
	this.uddannelse = uddannelse;
	this.indflytningsdato = indflytningsdato;
	this.lejeaftalensUdløb = lejeaftalensUdløb;
	this.telefonnummer = telefonnummer;
}
public String getNavn() {
	return navn;
}
public void setNavn(String navn) {
	this.navn = navn;
}
public String getVærelse() {
	return værelse;
}
public void setVærelse(String værelse) {
	this.værelse = værelse;
}
public Uddannelse getUddannelse() {
	return uddannelse;
}
public void setUddannelse(Uddannelse uddannelse) {
	this.uddannelse = uddannelse;
}
public LocalDate getIndflytningsdato() {
	return indflytningsdato;
}
public void setIndflytningsdato(LocalDate indflytningsdato) {
	this.indflytningsdato = indflytningsdato;
}
public LocalDate getLejeaftalensUdløbl() {
	return lejeaftalensUdløb;
}
public void setLejeaftalensUdløb(LocalDate sidsteStudiekontrol) {
	this.lejeaftalensUdløb = sidsteStudiekontrol;
}
public String getTelefonnummer() {
	return telefonnummer;
}
public void setTelefonnummer(String telefonnummer) {
	this.telefonnummer = telefonnummer;
}

}
