# Render Deployment Guide für Dalzana

## 🚀 Schritt-für-Schritt Setup

### 1. **GitHub Repository vorbereiten**
```bash
git add .
git commit -m "Prepare for Render deployment"
git push origin main
```

### 2. **PostgreSQL Datenbank auf Render erstellen**
- Gehe zu [render.com](https://render.com) und melde dich an
- Erstelle eine neue **PostgreSQL Database**:
  - Wähle kostenlos oder bezahlten Plan
  - Notiere die **Internal Database URL** (sieht aus wie: `postgresql://user:password@host:port/dbname`)

### 3. **Web Service auf Render erstellen**
- Klicke auf "New +" → "Web Service"
- Verbinde dein GitHub Repository
- **Build & Start Settings**:
  - **Build Command**: `./mvnw clean package -DskipTests`
  - **Start Command**: `java -Dserver.port=${PORT} -Dfile.encoding=UTF-8 -jar target/dalzana-0.0.1-SNAPSHOT.jar`
  - **Region**: Choose the one closest to your users

### 4. **Environment Variables konfigurieren**
Füge in Render diese Umgebungsvariablen ein:

| Variable | Wert |
|----------|------|
| `DATABASE_URL` | Deine PostgreSQL Internal URL von Step 2 |
| `DB_USERNAME` | Extrahiert aus DATABASE_URL (user Teil) |
| `DB_PASSWORD` | Extrahiert aus DATABASE_URL (password Teil) |
| `EMAIL_USERNAME` | `dalzana.connect@gmail.com` |
| `EMAIL_PASSWORD` | Dein Gmail App Password |
| `APP_BASE_URL` | `https://your-app-name.onrender.com` |
| `SPRING_PROFILES_ACTIVE` | `render` |

**Beispiel DATABASE_URL:**
```
postgresql://user123:pwd456@dpg-xyz.postgres.render.com:5432/dalzana_db
```

### 5. **Deploy starten**
- Render startet automatisch den Build
- Warte auf "Deploy live" 
- App läuft auf: `https://your-app-name.onrender.com`

### 6. **Probleme beheben**

**Logs anschauen:**
```bash
# In Render Dashboard → Logs Tab
```

**Datenbank Connection:**
- Stelle sicher dass `spring.profiles.active` NICHT gesetzt ist (wird automatisch aus `application-render.properties` geladen)
- Check: `spring.datasource.url=${DATABASE_URL}` in `application.properties`

**Email funktioniert nicht:**
- Gmail braucht [App Password](https://support.google.com/accounts/answer/185833)
- Oder: Mit anderem Email Provider konfigurieren

### 7. **Auto-Deploy aktivieren**
- In Render: Settings → Auto-Deploy → GitHub Push
- Jetzt wird app automatisch aktualisiert bei jedem `git push`

---

## 📋 Was wurde geändert?

✅ **application.properties**: Alle Credentials sind jetzt Umgebungsvariablen  
✅ **pom.xml**: PostgreSQL Driver hinzugefügt  
✅ **application-render.properties**: Neue Profile für Production  
✅ **Procfile**: Deploy Script für Render  
✅ **Email-Sicherheit**: Passwort nicht mehr hardcodiert  

---

## 🔒 Sicherheits-Checkliste

- [ ] Email-Passwort nicht in Git
- [ ] `application-local.properties` ist in `.gitignore`
- [ ] Keine API-Keys in Code
- [ ] DATABASE_URL in Render als Secret eingetragen
- [ ] GitHub Token nicht öffentlich sichtbar

---

## 💡 Tipps

- **Cold Start**: Kostenlos Services schlafen ein nach 15 Min, dauert beim Aufwachen ~30s
- **Logs**: Render zeigt Live-Logs im Dashboard
- **Datenbank Backups**: Regelmäßig manuell exportieren
- **Scaling**: Bei Bedarf später auf bezahlten Plan upgraden

