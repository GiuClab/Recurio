# Recurio — by GiuClab
Tous vos paiements au même endroit. Aperçu gratuit et open source pour Android 8+, GPL-3.0-or-later.

Télécharger [Recurio-v1.0.apk](https://github.com/GiuClab/Recurio/releases/download/v1.0/Recurio-v1.0.apk). Les notifications affichent icône, montant et devise. La version interne est 1.0. La fenêtre À propos utilise des espacements plus compacts et regroupe logo et version. La signature de débogage diffère de celle de l’ancien APK. Si Android refuse la mise à jour, sauvegardez les données avant de désinstaller l’ancienne application. [Détails](../distribution/DOWNLOAD_NOTES.md).


## Fonctions
Ajout, modification, suspension et suppression des paiements. Fréquences ponctuelle, hebdomadaire, mensuelle, trimestrielle et annuelle. Total du mois et prévision sur 12 mois, séparés par devise. Partage de liens du navigateur, reconnaissance d’145 entrées du catalogue par domaine et ouverture dans le navigateur externe.

Rappels facultatifs avec montant, devise et icône du service déjà disponible sur le téléphone, ou initiale du paiement. Délai de 0 à 365 jours et heure choisie. La réception d’un rappel ne déclenche aucune requête vers les sites. Android peut les retarder ; cet aperçu ne demande pas l’autorisation d’alarmes exactes. Autorisez les notifications et testez sur votre téléphone. Après un arrêt forcé, rouvrez l’application.

## Premier lancement et sauvegarde
Si la langue du téléphone est prise en charge (en/it/de/fr), Recurio l’utilise directement. Sinon un sélecteur en anglais apparaît. Un guide de six étapes et une explication facultative des notifications suivent. Les utilisateurs des anciennes alpha gardent leur choix. Le guide reste disponible dans les paramètres.

Paramètres → Sauvegarde et restauration exporte paiements et rappels dans un fichier .recurio chiffré, avec un mot de passe d’au moins 5 caractères. Pour le nouveau téléphone, choisissez le fichier et saisissez le mot de passe. Le nombre de paiements est affiché avant confirmation du remplacement de tous les paiements actuels, sans fusion. Le mot de passe est irrécupérable. Icônes, préférences et autorisations Android non incluses. Aucun partage ni synchronisation.

## Confidentialité
Aucun compte, publicité, suivi, accès bancaire ou serveur Recurio. Données dans le stockage privé de l’application, sans chiffrement supplémentaire de la base. Sauvegarde automatique Android désactivée.

Récupération facultative des icônes, activée par défaut, avec information avant les premières requêtes et option de désactivation. Requête HTTPS directe vers le site public, sans cookies du navigateur, chemin personnel du lien ni données de paiement. Le site voit l’adresse IP. Images conservées dans le cache local. Les icônes absentes, incompatibles ou hébergées sur un autre domaine sont remplacées par une initiale. Les icônes GitHub et GitHub Copilot proviennent d’Octicons (MIT) ; les autres icônes restent facultatives et téléchargées depuis les sites.

## Limites et compilation
Les aperçus sont sur [GitHub](https://github.com/GiuClab/Recurio/releases) ; aucune publication F-Droid pour le moment. APK de débogage destiné aux essais, pas encore testé sur appareil physique. Exportation chiffrée et restauration manuelle disponibles. Catégories de paiement, intervalles personnalisés, synchronisation et détection automatique des échéances personnelles non disponibles. Désinstaller ou effacer les données supprime les paiements. Icône Recurio approuvée et intégrée.

OpenJDK 17, Android SDK 35 et Build Tools 35.0.0 : `./gradlew testDebugUnitTest lintDebug assembleDebug`. Le workflow produit un artefact de débogage, sans publier de version. Contributions avec tests adaptés, traductions et ressources redistribuables bienvenues. Les noms et icônes des services appartiennent à leurs titulaires respectifs.

[Détails techniques](../README.md) · [Licence](../LICENSE) · [Soutenir le projet](https://ko-fi.com/giuclab/tip)

Made with ❤️ by GiuClab

[GitHub](https://github.com/GiuClab/Recurio) · [Feedback / suggerimenti](https://github.com/GiuClab/Recurio/issues/new/choose)
