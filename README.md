Fejlesztői dokumentáció - CheckPoint Asztali Alkalmazás (JAR)

Ez a dokumentáció a CheckPoint Java-alapú asztali alkalmazás működését és funkcióit öleli fel, amely futtatható JAR fájl formájában áll rendelkezésre.

1. Indítás

Az alkalmazás elindítása: java -jar CheckPoint_Desktop.jar

Java 17 vagy újabb szükséges

2. Bejelentkezés

A felhasználó a szerepkörének megfelelő adatokkal jelentkezhet be: ADMIN / TEACHER / STUDENT / PORTA

A bejelentkezés JWT token alapú hitelesítést használ

3. Navigáció (Sidebar)

Profil: Felhasználói adatok megjelenítése, módosítási lehetőséggel

Események: Belépési/kilépési és dohányzási napló megtekintése (nem működik, csak a ui)

Keresés (csak Admin): Felhasználók listázása, szűrés

Felhasználók kezelése (csak Admin):

Új felhasználó hozzáadása

Módosítás / törlés

Beállítások: Sötét mód ki/bekapcsolása

Kijelentkezés

4. Portás Funkcionalitások

4.1 Kártyabeolvasás

A portás mezőbe beírt vonalkódot 0.3 mp inaktivitás után dolgozza fel

Az idő leteltével autimatikusan resetel és frissítéskor visszaáll
(Rossz mező miatt hibát dob)(Fejlesztés a jövőben)

Eseményeket naplóz:

Belép / Kilép (külön azonosítóval - 11 karakter, studentCardNumber (diákigazolványszám))

Dohányzás indítás / befejezés (diákigazolványszám - 12 karakter, 0-ra végződik)

Ismeretlen kártyaszám esetén figyelmeztetés a listában: "Ez az azonosító nem szerepel a nyilvántartásban." (Hibát dob, minden bevitt adatra...)(Fejlesztés a jövőben)

5. Profilkezelés

Név, email megtekinthető / szerkeszthető

Új jelszó megadása opció

6. Események megtekintése

Események listázása időrendben

Admin oldalon szűrhető:

Összes esemény

Dohányzó események

Nem dohányzó események
(Fejlesztés a jövőben)
7. Keresés (Admin)

Név, email vagy szerepkör alapján szűrhető felhasználók listája

8. Felhasználók kezelése (Admin)

Új felhasználó felvétele szerepkör alapján

Diák esetén: osztály, szül. dátum, igazolványszám

Felhasználó módosítása / törlése

9. Technikai megjegyzések

Backend REST API: http://localhost:3000

A JWT token átadása minden hívásnál szükséges

argon2 jelszó titkosítás

10. További lehetőségek (Fejlesztés a jövőben)

25 perces dohányzási limit naplázása adatbázisban

Előző napokról is történő ellenőrzés a dohányzási időtartamok alapján
