# Recurio — by GiuClab
Alle Zahlungen an einem Ort. Kostenlose Open-Source-Vorschau für Android 8+, GPL-3.0-or-later.

## Funktionen
Zahlungen hinzufügen, bearbeiten, pausieren und löschen. Einmalige, wöchentliche, monatliche, vierteljährliche und jährliche Termine. Monatssumme und Vorschau für 12 Monate, getrennt nach Währung. Browser-Links teilen, 145 Katalogeinträge anhand ihrer Domain erkennen und Links im externen Browser öffnen.

Optionale Erinnerungen mit Betrag, Währung und einem lokal verfügbaren Dienstsymbol oder dem Anfangsbuchstaben der Zahlung. 0–365 Tage Vorlauf und gewählte Uhrzeit. Beim Empfang einer Erinnerung werden keine Website-Anfragen gestartet. Android kann sie verzögern; diese Vorschau fordert keine Berechtigung für exakte Alarme an. Benachrichtigungen erlauben und auf dem Gerät testen. Nach erzwungenem Stopp muss die App wieder geöffnet werden.

## Erster Start und Sicherung
Bei unterstützter Telefonsprache (en/it/de/fr) startet die App direkt in dieser Sprache. Sonst erscheint eine englische Sprachauswahl. Eine sechsteilige Anleitung und eine optionale Erklärung der Benachrichtigungsberechtigung folgen. Bestehende Alpha-Nutzer behalten ihre Sprache. Die Anleitung ist in den Einstellungen verfügbar.

Unter Einstellungen → Sicherung und Wiederherstellung werden Zahlungen und deren Erinnerungen in eine passwortverschlüsselte .recurio-Datei exportiert. Das Passwort muss mindestens 5 Zeichen haben und kann nicht wiederhergestellt werden. Beim Import auf einem neuen Handy wird die Anzahl angezeigt und das Ersetzen aller vorhandenen Zahlungen muss bestätigt werden. Kein Zusammenführen. Symbole, App-Einstellungen und Android-Berechtigungen sind nicht enthalten. Keine Freigabe oder Synchronisierung.

## Datenschutz
Kein Konto, keine Werbung, kein Tracking, keine Bankanbindung und kein Recurio-Server. Daten im privaten App-Speicher, ohne zusätzliche Datenbankverschlüsselung. Automatische Android-Sicherung deaktiviert.

Website-Symbole werden nur optional geladen; standardmäßig an, mit Hinweis vor ersten Anfragen und Abschaltmöglichkeit. Direkte HTTPS-Anfrage an die öffentliche Website, ohne Browser-Cookies, persönliche Link-Pfade oder Zahlungsdaten. Die Website sieht die IP-Adresse. Bilder bleiben im lokalen Cache. Fehlende, nicht unterstützte oder auf fremden Domains gespeicherte Symbole werden durch einen Anfangsbuchstaben ersetzt. GitHub- und GitHub-Copilot-Symbole aus Octicons (MIT) sind enthalten; andere Website-Symbole werden nur optional geladen.

## Einschränkungen und Build
Vorschauen sind auf [GitHub](https://github.com/GiuClab/Recurio/releases) verfügbar; noch kein F-Droid-Eintrag. Debug-APK nur zum Testen, noch nicht auf einem physischen Gerät getestet. Verschlüsselter Export und manuelle Wiederherstellung sind verfügbar. Zahlungskategorien, eigene Wiederholungsintervalle, Synchronisierung und automatische Erkennung persönlicher Verlängerungstermine fehlen. Deinstallation oder Datenlöschung entfernt alle Einträge. Das freigegebene Recurio-Symbol ist integriert.

Mit OpenJDK 17, Android SDK 35 und Build Tools 35.0.0: `./gradlew testDebugUnitTest lintDebug assembleDebug`. Der Workflow erzeugt ein Debug-Artefakt, keine Veröffentlichung. Beiträge mit passenden Tests, Übersetzungen und frei verteilbaren Ressourcen sind willkommen. Dienstnamen und Symbole gehören ihren jeweiligen Inhabern.

[Technische Details](../README.md) · [Lizenz](../LICENSE) · [Projekt unterstützen](https://ko-fi.com/giuclab/tip)

Made with ❤️ by GiuClab

[GitHub](https://github.com/GiuClab/Recurio) · [Feedback / suggerimenti](https://github.com/GiuClab/Recurio/issues/new/choose)
