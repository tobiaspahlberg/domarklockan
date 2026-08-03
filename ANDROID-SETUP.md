# Domarklockan som Android-app

Guide för att paketera den befintliga webbappen som en riktig Android-app med Capacitor, så att larmet hörs även när skärmen är släckt.

**Grundidén:** samma `index.html` används av både webbappen (iPhone) och Android-appen. Ingen kod dubbleras.

---

## Förutsättningar

| Verktyg | Kommentar |
|---|---|
| Android Studio | Du har det redan installerat |
| Node.js 24 (LTS) | Capacitor kräver minst Node 22. Ladda ner från nodejs.org, kontrollera med `node -v` |
| JDK 21 | Följer med Android Studio – ingen separat installation behövs |

Har du kört Capacitor-kommandon i ett annat projekt tidigare spelar det ingen roll: paketen installeras per projekt.

---

## Steg 1 – Strukturera om repot

Appfilerna flyttas till en mapp `docs/` så att både GitHub Pages och Capacitor kan använda dem.

**Ny struktur:**

```
domarklockan/
├── docs/                  ← webbappen (det som GitHub Pages visar)
│   ├── index.html
│   ├── manifest.json
│   └── domarklockan-ikon.png
├── android/               ← genereras i steg 4
├── capacitor.config.json
├── package.json           ← genereras i steg 3
├── .gitignore
└── .github/workflows/android-build.yml
```

Enklast via GitHubs webbgränssnitt: öppna `index.html`, klicka på pennan, och ändra filnamnet i rutan högst upp till `docs/index.html` – då flyttas filen. Upprepa för `manifest.json` och ikonen.

**Ställ sedan om GitHub Pages:** Settings → Pages → Branch: `main`, mapp: `/docs`. Spara. Webbadressen förblir densamma, så din sons iPhone påverkas inte.

---

## Steg 2 – Hämta repot till datorn

I Android Studio: **File → New → Project from Version Control**, klistra in repots URL, välj en mapp och klona.

Härifrån kan du sköta all git i Android Studio: **Commit** (Ctrl+K) och **Push** (Ctrl+Shift+K). Ingen kommandorad behövs för git.

---

## Steg 3 – Installera Capacitor

Öppna terminalen i Android Studio (**View → Tool Windows → Terminal**) och kör, en rad i taget:

```bash
npm init -y
npm install @capacitor/core @capacitor/cli @capacitor/android @capacitor/local-notifications
```

Lägg sedan in den färdiga `capacitor.config.json` i projektets rot (filen finns bland de levererade filerna).

---

## Steg 4 – Skapa Android-projektet

```bash
npx cap add android
npx cap sync android
```

Nu finns mappen `android/` med ett komplett Android-projekt. Den ska **checkas in i git** – det är den som molnbygget använder.

Lägg också in `.gitignore` i projektets rot (byt namn från `gitignore.txt` till `.gitignore`).

---

## Steg 5 – Kör appen på din telefon

1. Aktivera utvecklarläge på telefonen: Inställningar → Om telefonen → tryck sju gånger på **Byggnummer**
2. Slå på **USB-felsökning** under Inställningar → Utvecklaralternativ
3. Anslut telefonen med kabel
4. I Android Studio: välj telefonen i enhetslistan uppe till höger och tryck på **Run** (▶)

Appen installeras och startar. Ändrar du något i `docs/index.html` räcker det sedan med `npx cap sync android` följt av Run igen.

---

## Steg 6 – Molnbygge med GitHub Actions

Lägg filen `android-build.yml` i mappen `.github/workflows/` i repot. (Via webbgränssnittet: **Add file → Create new file**, skriv `.github/workflows/android-build.yml` som filnamn – mapparna skapas automatiskt när du skriver snedstrecken – och klistra in innehållet.)

**Så hämtar du APK-filen:**

1. Gå till fliken **Actions** i repot
2. Klicka på det senaste bygget (tar 3–5 minuter)
3. Ladda ner **domarklockan-apk** längst ner under "Artifacts"
4. Packa upp zip-filen och öppna APK-filen på telefonen för att installera

Bygget startar automatiskt vid varje push, och kan även startas manuellt via knappen **Run workflow**.

> **Viktigt om signering:** APK:er från molnbygget och från Android Studio signeras med olika nycklar. Android vägrar uppdatera en app om nyckeln skiljer sig ("App not installed"). Välj därför **en** källa för telefonen – eller avinstallera appen innan du byter källa.

---

## Steg 7 – Larm i bakgrunden

När Capacitor är på plats kan appen schemalägga en systemnotis exakt vid periodens slut. Notisen ljuder även med släckt skärm eller om Android stängt av appen, eftersom det är Androids egen larmmotor som sköter den.

Den koden bygger vi in i `index.html` som nästa steg – den aktiveras bara när appen körs som Android-app, så webbappen på iPhone fungerar precis som förut.

---

## Uppdateringsrutin framåt

| Vad du ändrat | Vad du gör |
|---|---|
| Bara `docs/index.html` | Committa och pusha → webbappen uppdateras direkt, APK byggs automatiskt |
| Ny version av appen | Höj `APP_VERSION` och lägg till en changelog-post, precis som förut |
| Nytt Capacitor-plugin | `npm install ...` följt av `npx cap sync android` |

---

## Felsökning

**"SDK location not found"** – öppna mappen `android/` som projekt i Android Studio en gång; den skapar `local.properties` automatiskt.

**Gradle-bygget misslyckas i molnet men fungerar lokalt** – kontrollera att `android/`-mappen och `package-lock.json` verkligen är incheckade i repot.

**Telefonen vägrar installera APK:n** – tillåt installation från okänd källa för filhanteraren, och se signeringsnoten i steg 6.

**Molnbygget klagar på Node-versionen** – `The Capacitor CLI requires NodeJS >=22`. Kontrollera att `node-version` i workflow-filen står på `'22'` eller högre.

**Appen visar en gammal version** – kör `npx cap sync android` innan bygget; webbfilerna kopieras in i Android-projektet vid sync, inte automatiskt.
