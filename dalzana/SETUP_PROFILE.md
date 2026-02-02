# Dalzana - Setup Anleitung für beide Entwickler

## Situation
- **Person 1 (mit MySQL)**: Hat MySQL lokal installiert und läuft
- **Person 2 (ohne MySQL)**: Kann das Projekt auch ohne MySQL starten

## Lösung: Spring Boot Profile

Das Projekt nutzt jetzt **Spring Boot Profile** um verschiedene Datenbank-Konfigurationen zu verwenden.

---

## 🚀 Für Person 1 (MySQL Datenbank lokal)

### Schritt 1: Maven Dependencies installieren
```bash
mvnw clean install
```

### Schritt 2: Projekt mit Standard-Profil starten
```bash
mvnw spring-boot:run
```

Das Projekt nutzt automatisch die **MySQL Datenbank** (application.properties).

### Schritt 3: Tests ausführen
Tests laufen mit dem H2-Test-Profil (brauchen keine MySQL):
```bash
mvnw test
```

---

## 🚀 Für Person 2 (ohne MySQL installiert)

### Schritt 1: Maven Dependencies installieren
```bash
mvnw clean install
```

### Schritt 2: Projekt mit Test-Profil starten
```bash
mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=test"
```

Das Projekt nutzt jetzt die **H2 In-Memory Datenbank** (application-test.properties).
Dadurch werden **keine** MySQL-Zugangsdaten benötigt!

### Alternative: In IDE starten
In der IDE (z.B. IntelliJ oder VS Code) können Sie auch direkt das Profil setzen:
- IDE-Umgebungsvariable: `SPRING_PROFILES_ACTIVE=test`
- Oder in application.properties: `spring.profiles.active=test`

### Schritt 3: Tests ausführen
```bash
mvnw test
```

---

## 📋 Welches Profil nutzt welche Datenbank?

| Profil | Datenbank | Konfiguration | Nutzer |
|--------|-----------|---------------|--------|
| `default` (keine Angabe) | MySQL | `application.properties` | Person 1 |
| `test` | H2 (In-Memory) | `application-test.properties` | Person 2 / Unit Tests |

---

## 🧪 Unit Tests ausführen

Die neuen Unit Tests prüfen die **HelloController** Funktionalität ohne Datenbankverbindung:

```bash
# Alle Tests ausführen
mvnw test

# Nur HelloControllerTest ausführen
mvnw test -Dtest=HelloControllerTest
```

---

## 🔧 Troubleshooting

### Problem: "MySQL Connection Refused"
**Lösung:** Das Test-Profil verwenden:
```bash
mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=test"
```

### Problem: "H2 Datenbank nicht gefunden"
**Lösung:** Maven Dependencies neu installieren:
```bash
mvnw clean install
```

### Problem: Port 8080 ist bereits belegt
**Lösung:** Einen anderen Port setzen:
```bash
mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

---

## 📚 Weitere Informationen

- **H2 Console** (nur mit test-Profil): `http://localhost:8080/h2-console`
- **Spring Boot Profiles**: https://docs.spring.io/spring-boot/reference/features/profiles.html
- **H2 Database**: https://www.h2database.com/

---

## 💾 Wichtig für Git

Stellt sicher, dass Ihr die richtigen Konfigurationen committed:
- ✅ `application.properties` (MySQL - Production)
- ✅ `application-test.properties` (H2 - Tests)
- ✅ `TestDatabaseConfig.java` (Test-Konfiguration)
- ✅ `HelloControllerTest.java` (Unit Tests)

Die Person mit MySQL soll die `application.properties` nicht ändern!
