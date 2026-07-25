# Domarklockan ⚽

En enkel matchklocka för unga fotbollsdomare. Byggd för svensk barn- och ungdomsfotboll med de nationella spelformernas officiella speltider.

## Spelformer och speltider

| Spelform | Ålder | Speltid |
|---|---|---|
| 3 mot 3 | 6–7 år | 4 × 3 min |
| 5 mot 5 | 8–9 år | 3 × 15 min (enskild match) / 3 × 10 min (sammandrag) |
| 7 mot 7 | 10–12 år | 3 × 20 min (enskild match) / 3 × 15 min (sammandrag) |
| 9 mot 9 | 13–14 år | 3 × 25 min |
| 11 mot 11 | 15 år– | 2 × 40 min (15 år) / 2 × 45 min (16 år och äldre) |

Källa: Svenska Fotbollförbundet, [Nationella spelformer](https://aktiva.svenskfotboll.se/tranare/spelformer/)

## Lägga till appen på hemskärmen

### Android (Chrome)

1. Öppna appens webbadress i Chrome
2. Tryck på de tre prickarna **⋮** uppe till höger
3. Välj **"Lägg till på startskärmen"** (på vissa telefoner: "Installera app")
4. Bekräfta med **"Lägg till"**

### iPhone (Safari)

1. Öppna appens webbadress i **Safari** (fungerar inte från Chrome på iPhone)
2. Tryck på dela-knappen (fyrkanten med pil uppåt) längst ner i mitten
3. Skrolla ner och välj **"Lägg till på hemskärmen"**
4. Tryck på **"Lägg till"** uppe till höger

Appen ligger sedan bland de andra apparna med visselpipe-ikonen och öppnas i fullskärm.

## Så används appen

1. **Välj spelform** på startskärmen – tryck på rätt speltid (för 5 mot 5, 7 mot 7 och 11 mot 11 finns två varianter)
2. **Starta perioden** med den gröna knappen – klockan räknar uppåt och skärmen hålls tänd
3. **Mål:** tryck var som helst på lagets ruta. Tryck på **−** för att ångra
4. **Tröjfärg:** tryck på 🎽 under respektive lag och välj färg, så syns det tydligt vilket lag som är vilket
5. **När tiden är slut** blinkar skärmen rött och ett larm ljuder tills det stängs av. Därefter startas nästa period med den gröna knappen
6. Om klockan får gå förbi full tid visas **tilläggstiden i orange** (t.ex. +0:15)

### Låsläge (mot feltryck)

- Tryck på **🔓 Lås skärmen** under klockan – klockan syns och går, men inga tryck registreras
- När skärmen är låst visas en gul **🔒 Skärmen är låst**-markering högst upp
- **Lås upp:** dra det gula reglaget längst ner hela vägen åt höger, som ett vanligt skärmlås
- När larmet går låses skärmen upp automatiskt så att ljudet kan stängas av

## Bra att veta

- **Ljudet fungerar bara när appen är öppen på skärmen.** Låses telefonen med sidoknappen fortsätter klockan att räkna rätt tid, men larmet hörs först när skärmen tänds igen. Använd därför applåset 🔓 istället för att låsa telefonen under perioderna.
- **Före match:** skruva upp medievolymen, stäng av "Stör ej", och testa gärna larmet hemma en gång så du vet hur det låter.
- **Skärmen hålls tänd** under hela matchen, vilket drar en del batteri – bra att ha telefonen laddad före match.
- **Uppdateringar:** appen visar sitt versionsnummer längst upp på startskärmen. Varje gång den öppnas kollar den om en nyare version finns på servern – i så fall visas en gul knapp **"Ny version finns – tryck här för att uppdatera"**. Viktigt när du laddar upp en ny version: höj `APP_VERSION`-numret högst upp i `index.html`:s skriptdel, annars upptäcks inte uppdateringen. (Tänk också på att GitHub Pages kan dröja upp till ca 10 minuter med att servera nya filer.)

## Filer

| Fil | Beskrivning |
|---|---|
| `index.html` | Hela appen i en enda fil |
| `domarklockan-ikon.png` | Hemskärmsikonen (måste ligga i samma mapp) |
| `manifest.json` | Gör att appen öppnas i fullskärm utan adressfält (måste ligga i samma mapp) |
