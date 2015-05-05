# Funktionen der Integritätskomponente

Die Komponente hat drei Hauptfunktionen:
- Erstellen von Integritätsinformationen für eine Kollektion von Dateien
- Überprüfen der Integrität einer Kollektion von Dateien
- Überprüfen der Integrität eines alten Standes bei wachsenden Kollektionen

# Arbeitsweise der Komponente

Die Komponente arbeitet mit SHA512-Hashwerten, die zur Überprüfung in einer Menge von Bäumen (Hashtree) verarbeitet werden. Alle wichtigen Funktionen sind in der Klasse `HashForest` untergebracht. Die Integritätsinformation kann in zwei Modi serialisiert werden: Full und Root (siehe unten).

Die Integritätsinformation betrifft immer die gesamte Kollektion, es kann keine Aussage über einzelne Dateien gemacht werden. Wenn eine einzelne Datei modifiziert oder entfernt wurde, ist somit die Integrität der gesamten Kollektion invalid.

Ein HashForest kann mit verschiedenen Hashtypen arbeiten, die das Interface `HashValue` implementieren. Empfohlen ist die Verwendung von SHA512 Hashes, die Komponente liefert dafür eine Implementierung `SHA512HashValue`. Integritätsinformationen, die verschiedene Hashtypen haben sind inkompatibel zueinander.

Die Integritätsinformationen enthalten einen Zeitstempel in einem ISO8601 Format. Der Zeitstempel kann mit der Methode `HashForest.getFirstSerializedDateTime()` abgefragt werden. Der Zeitstempel eines `HashForest`-Objekts wird erst beim serialisieren mit der Methode `HashForest.writeTo(FileWriter)` erstellt, und nach einer möglichen Erweiterung beim nächsten serialisieren durch den aktuellen Wert ersetzt. Wird ein `HashForest`-Objekt ohne Modifikation mehrfach serialisiert, ändert sich der Zeitstempel nicht. Solange ein `HashForest`-Objekt noch nicht serialisiert wurde, liefert die Methode `HashForest.getFirstSerializedDateTime()` den Wert `null`.

## Full mode

Die serialisierten Integritätsinformationen enhalten eine Liste der Hashwerte der Dateien der abgesicherten Kollektion. Dieser Modus unterscheidet sich kaum von dem herkömmlichen Vorgehen, Hashwerte für eine Sammlung von Dateien zu erstellen (z.B. mit dem Linux-Tool `sha512sum`), mit dem Unterschied dass die Integritätsinformationen keine Dateinamen enthalten, stattdessen aber eine Checksumme. Im Full mode können in der Zukunft hinzukommende Dateien auch in die Integritätsinformationen aufgenommen werden. Der Full mode ist geeignet um eine wachsende Dateikollektion abzusichern.

## Root mode

Die serialisierten Integritätsinformationen enhalten nur eine Liste der Wurzeln der Hashbäume. Die serialisierte Datenmenge ist damit viel geringer als im Full mode, trotzdem können alle Hauptfunktionen damit erfüllt werden (Einschränkung: beim Überprüfen der Integrität eines alten Standes bei wachsenden Kollektionen muss der aktuelle Stand im Full mode vorliegen, bei dem alten Stand reicht der Root mode). Die Integritätsinformationen im Root mode können aber nicht mehr um neu hinzukommende Dateien erweitert werden. Der Root mode ist geeignet um eine statische Dateikollektion abzusichern und um für alte Zustände einer Kollektion zu überprüfen, ob der aktuelle Zustand nur neu hinzugekommene Dateien extra enthält, von den alten Dateien aber keine modifiziert oder entfernt wurden.

Der Root mode wird aktiviert, indem auf einem `HashForest`-Objekt die Methode `pruneForest()` aufgerufen wird. *Achtung: Diese Aktion ist unumkehrbar!* Ein `HashForest`-Objekt im Root mode kann nicht mehr erweitert werden.

## Hinweis zur Reihenfolge

Zu Beachten ist, dass die Reihenfolge der Verarbeitung der Dateien der abzusichernden Kollektion beim Erstellen und beim Überprüfen der Integritätsinformation gleich sein muss. In den serialisierten Integritätsinformationen ist keine Reihenfolgeinformation hinterlegt, eine Anwendung hat also selbst dafür zu sorgen, dass diese Reihenfolge eingehalten wird. Zur Unterstützung gibt es die Hilfsklasse `Ordering` und das Attribut `orderingInformationLocation` in `HashForest`, eine Anwendung kann die Reihenfolgeinformationen aber auch an einer anderen geeigneten Stelle speichern (z.B. in einer Datenbank).

Die Integritätsinformationen ohne Reihenfolgeinformationen zu serialisieren ist eine bewusste Designentscheidung, um die Speicherung der Reihenfolgeinformationen unabhängig von den Integritätsinformationen zu machen. Damit wird es z.B. möglich, die Reihenfolgeinformationen an anderer Stelle zu speichern, statt Dateinamen andere Bezeichner zu verwenden oder implizite Informationen wie Zeitstempel zu verwenden.


# Empfehlungen zum Workflow

## Wachsende Kollektion (Archiv)

### 1. Initialisierung

Erstellen eines neuen HashForest-Objekts.

    HashForest<SHA512HashValue> hf = new HashForest<SHA512HashValue>();

Berechnen der Hashwerte für die Dateien und update des HashForest. Anmerkung: für die Berechnung der Hashwerte ist die Anwendung zuständig. Zur Unterstützung bietet die Komponente die Methode `FileUtil.getHash(String)`, die ein passendes `SHA512HashValue`-Objekt zurückliefert.
  
    for (String fileName : collection) {
        SHA512HashValue hashValue = FileUtil.getHash(fileName);
        hf.update(hashValue);
    }

Beachten Sie den Hinweis zur Reihenfolge. In der gleichen Datei-Reihenfolge wie hier die update-Funktion aufgerufen wird, muss sie auch beim Überprüfen der Integrität aufgerufen werden.

### 2. Serialisierung

Serialisierung der Integritätsinformationen. (Im Beispiel auch die Funktionsweise bei der Abfrage des Zeitstempels.)

    hf.getFirstSerializedDateTime();  // liefert null, noch nicht serialisiert
    hf.writeTo(new FileWriter(integrityFile));
    
    // liefert jetzt den Zeitstempel der Serialisierung, z.B.
    // "2015-05-05T12:34:56.789+02:00"
    hf.getFirstSerializedDateTime();

#### 2.1. Validierung

Überprüfen der Integritätsinformationen: Laden der Informationen für den aktuellen Stand, berechnen des aktuellen Standes und Abgleich der `HashForest`-Objekte durch `HashForest.validate(HashForest)`.

    HashForest<SHA512HashValue> savedState = new HashForest<SHA512HashValue>();
    savedState.readFrom(new FileReader(integrityFile));
    
    HashForest<SHA512HashValue> currentState = new HashForest<SHA512HashValue>();
    // Reihenfolge beachten!
    for (String fileName : collection) {
        SHA512HashValue hashValue = FileUtil.getHash(fileName);
        currentState.update(hashValue);
    }
    
    boolean isValid = currentState.validate(savedState);


### 3. Folgegeneration

Erstellen eines Snapshot, laden eines alten Standes, hinzufügen neuer Dateien und serialisieren des aktualisierten Standes.

    // Hier: Snapshot eines zuvor serialisierten Zustands erstellen.
    // Alternativ kann auch der aktuelle Zustand komplett neu berechnet und
    // der Snapshot daraus erstellt werden.
    HashForest<SHA512HashValue> snapshot = new HashForest<SHA512HashValue>();
    snapshot.readFrom(new FileReader(integrityFile));
    
    // Versetzt den HashForest in den Root mode
    snapshot.pruneForest();
    // Achtung: Die Snapshot-Informationen dürfen nicht in das integrityFile
    // geschrieben werden, sonst ist die Full mode Information verloren
    snapshot.writeTo(new FileWriter(snapShootIntegrityFile));
    
    
    HashForest<SHA512HashValue> hf = new HashForest<SHA512HashValue>();
    // HashForest-Objekt wird aus den gleichen Informationen erstellt wie oben
    hf.readFrom(new FileReader(integrityFile));
    // hinzufügen neuer Werte
    for (String fileName : newFiles) {
        SHA512HashValue hashValue = FileUtil.getHash(fileName);
        hf.update(hashValue);
    }
    // Überschreiben ist jetzt ok. Weil der HashForest erweitert wurde, wird
    // jetzt auch ein neuer Serialisierungszeitstempel generiert.
    hf.writeTo(new FileWriter(integrityFile));
    

### 4. Alten Stand validieren

Laden des alten Standes (z.B. eines Snapshot), laden (oder berechnen) eines aktuellen Standes (muss im Full mode sein), überprüfen ob der alte Stand eine Untermenge des aktuellen Standes ist.

    HashForest<SHA512HashValue> oldState = new HashForest<SHA512HashValue>();
    oldState.readFrom(new FileReader(oldStateIntegrityFile));
    
    // Der aktuelle HashForest muss im Full mode sein!
    HashForest<SHA512HashValue> currentState = new HashForest<SHA512HashValue>();
    currentState.readFrom(new FileReader(currentStateIntegrityFile));
    
    boolean isValidProgression = currentState.contains(oldState);

Erläuterung zur Funktionsweise: Der HashForest ist so konstruiert, dass er nur wachsen kann, indem neue HashWerte angefügt werden, das einfügen neuer Werte zwischen bestehenden ist nicht möglich. In dem HashForest eines aktuellen Zustandes, der ausgehend von einem alten Zustand nur erweitert wurde, muss also die HashForest-Struktur des alten Zustandes auffindbar sein, solange keine Dateien, die zum alten Zustand gehörten, modifiziert oder entfernt wurden. Da jeder Baum durch den HashWert seiner Wurzel repräsentiert wird, reicht es aus, von dem alten Zustand die Wurzeln gespeichert zu haben. Vom aktuellen Zustand muss aber der gesamte Wald vorliegen.

## Statische Kollektion (DIP)

Sinnvoll ist hier, die Reihenfolgeinformation mit in die Kollektion zu legen und über die Integritätsinformation mit abzusichern. Dazu liefert die Komponente die Hilfsklasse `Ordering`, außerdem sind Standarddateinamen für Integritäts- und Reihenfolgedaten hinterlegt (`HashForest.INTEGRITYFILENAME`, `Ordering.ORDERFILENAME`).

### 1. Kollektion erstellen

Alle Dateien der Kollektion müssen vollständig erstellt sein und dürfen nach diesem Schritt nicht mehr modifiziert werden. Im weiteren wird davon ausgegangen, dass die Namen aller Dateien in einer Liste `collection` vorliegen.

### 2. Integritäts- und Reihenfolgeinformationen erstellen und serialisieren

Zuerst Reihenfolgeinformationen erstellen und serialisieren, dann darauf basierend Integritätsinformationen erstellen und serialisieren. Der Dateiname für die Reihenfolgeinformationen wird mit in die Liste der Dateien zur Integritätsberechnung aufgenommen, damit ist diese Datei mit abgesichert.

    Ordering ordering = new Ordering(
        new ChecksumProvider(MessageDigest.getInstance("SHA-512")));
    ordering.add(Ordering.ORDERFILENAME);
    for (String fileName : collection) {
        ordering.add(fileName);
    }
    ordering.writeTo(new FileWriter(Ordering.ORDERFILENAME));
	
    HashForest<SHA512HashValue> hf = new HashForest<SHA512HashValue>();
    for (String fileName : ordering.getIdentifiers()) {
        SHA512HashValue hashValue = FileUtil.getHash(fileName);
        hf.update(hashValue);
    }
    hf.pruneForest();  // Root mode reicht in diesem Szenario

Anmerkung: `setOrderingInformationLocation()` hat keine weitere Funktion, dient aber als Hinweis, wo die Reihenfolgeinformationen zu finden sind.

    hf.setOrderingInformationLocation(Ordering.ORDERFILENAME);
    hf.writeTo(new FileWriter(HashForest.INTEGRITYFILENAME));


### 3. Integrität validieren
	
Integritäts- und Reihenfolgeinformationen einlesen, aktuelle Integritätsinformationen berechnen und die beiden `HashForest`-Objekte vergleichen.

	
    HashForest<SHA512HashValue> savedState = new HashForest<SHA512HashValue>();
    savedState.readFrom(new FileReader(HashForest.INTEGRITYFILENAME));
	
    Ordering ordering = new Ordering(
        new ChecksumProvider(MessageDigest.getInstance("SHA-512")));
    ordering.readFrom(new FileReader(Ordering.ORDERFILENAME));

Anmerkung: Falls nicht die vorgeschlagenen Dateinamen verwendet wurden, hat man die Information, wo die Reihenfolgeinformatioen zu finden sind, wahrscheinlich im `HashForest`-Objekt hinterlegt und kann sie mit `getOrderingInformationLocation()` aufrufen.

    HashForest<SHA512HashValue> currentState = new HashForest<SHA512HashValue>();
    for (String fileName : ordering.getIdentifiers()) {
        SHA512HashValue hashValue = FileUtil.getHash(fileName);
        currentState.update(hashValue);
    }
    
    boolean isValid = currentState.validate(savedState);



