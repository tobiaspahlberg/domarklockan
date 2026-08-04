# Domarklockan

Matchklocka för ungdomsfotbollsdomare. Distribueras på två sätt från samma kod:

- **Webbapp (PWA)** via GitHub Pages från `docs/` – används på iPhone
- **Android-app** byggd med Capacitor – har bakgrundslarm som webbappen inte kan ge

## Struktur

```
docs/index.html      Hela appen – HTML, CSS och JS i EN fil. All appkod bor här.
docs/hamta.html      Nedladdningssida med länk till senaste APK:n
docs/manifest.json   PWA-manifest (fullskärm på Android)
assets/              Källbilder för app-ikonen (genereras om vid varje bygge)
android/             Capacitor-projektet, incheckat i git
  app/src/main/java/se/domarklockan/app/
    MainActivity.java        Registrerar pluginet, visar appen över låsskärmen
    AlarmChannelPlugin.java  Larmkanal, schemaläggning, ljudlägeskoll
    AlarmReceiver.java       Väcker skärmen och visar fullskärmslarmet
.github/workflows/   Molnbygge som publicerar APK som GitHub Release
```

## Viktiga regler

**Appen ska förbli en enda HTML-fil.** Inget byggsteg, inga ramverk, inga
npm-paket i frontend. Enkelheten är ett medvetet val – filen ska gå att läsa
och redigera rakt av.

**Vid varje ändring i `docs/index.html`:**

1. Höj `APP_VERSION` (finns i skriptet, strax efter changelog-blocket)
2. Lägg till en post överst i changelog-blocket
   (`<script type="application/json" id="changelog">`) med samma versionsnummer

Detta är inte valfritt: versionsnumret styr både uppdateringsprompten i appen
och vilken tagg releasen får i molnbygget.

**Semantisk versionering:** buggfix → patch (2.4.1), ny funktion → minor
(2.5.0), större omtag → major.

## Kodkonventioner

- Svenska i all UI-text, kommentarer och changelog
- Klockan räknar **uppåt**, tilläggstid visas i orange efter full tid
- Speltider kommer från Svenska Fotbollförbundets nationella spelformer
  (https://aktiva.svenskfotboll.se/tranare/spelformer/) – ändra inte på känsla
- Färger ligger som CSS-variabler högst upp; `--run` gul, `--over` orange,
  `--go` grön, `--stop` röd

## Fallgropar

**Tid beräknas alltid från tidsstämplar**, aldrig genom att räkna ner en
variabel. Appen kan dödas av Android när som helst; `elapsed` rekonstrueras
från `savedAt` när matchen återupptas.

**Matcher sparas i localStorage vid varje händelse** plus var tredje sekund
under spel och när appen går i bakgrunden. En match får ALDRIG raderas
automatiskt – bara via papperskorgen med bekräftelse.

**Android-kanaler går inte att ändra i efterhand.** Ändras kanalens ljud eller
vibration måste kanal-id:t bytas, annars behåller telefonen de gamla
inställningarna tills appen avinstalleras.

**`npx cap sync android` kopierar bara webbfilerna** – ikoner genereras av
`npx @capacitor/assets generate --android`. Molnbygget kör båda.

**Native-kod körs bara i appen.** Allt som rör Capacitor ligger bakom
`if (NATIVE)` eller `if (AC)` så att webbversionen på iPhone fungerar oförändrat.

## Vanliga kommandon

```bash
npx cap sync android                      # inför lokalt bygge i Android Studio
npx @capacitor/assets generate --android  # efter ändrad ikon
cd android && ./gradlew assembleDebug     # bygg APK lokalt
```

Molnbygget sköter allt detta automatiskt vid push till `main` och publicerar
APK:n som en release med taggen `v<APP_VERSION>`.

## Testning

Ingen automatisk testsvit. Testa manuellt med en 3 mot 3-match (4 × 3 min med
byte vid 1.30) – den går snabbt att köra igenom och rör alla funktioner:
bytespaus, periodbyte, slutsignal, låsskärm och bakgrundslarm.
