/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Patric Hollenstein
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.lustenauer.thema;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.lustenauer.thema.data.Entry;
import net.lustenauer.thema.data.Thema;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private final Random random = new Random();

    private TextView antwortText1, antwortText2, antwortText3, frageText;
    private ImageButton antwortButton1, antwortButton2, antwortButton3, frageButton2;
    private Button frageButton1;

    private MediaPlayer mediaPlayer;

    private ArrayList<Thema> themaList;
    private Spinner themaSpinner, entrySpinner;
    private Thema currentThema;
    private Entry currentEntry;
    private ArrayAdapter<Entry> entryAdapter;
    private ArrayAdapter<Thema> themaAdapter;
    private boolean selectLastEntry;
    private boolean shuffleEntry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLists();

        antwortText1 = (TextView) findViewById(R.id.antwortText1);
        antwortText2 = (TextView) findViewById(R.id.antwortText2);
        antwortText3 = (TextView) findViewById(R.id.antwortText3);
        frageText = (TextView) findViewById(R.id.frageText);

        antwortButton1 = (ImageButton) findViewById(R.id.antwortButton1);
        antwortButton2 = (ImageButton) findViewById(R.id.antwortButton2);
        antwortButton3 = (ImageButton) findViewById(R.id.antwortButton3);
        frageButton1 = (Button) findViewById(R.id.frageButton1);
        frageButton2 = (ImageButton) findViewById(R.id.frageButton2);


        // THEMA SPINNER
        // =============
        themaAdapter = new ArrayAdapter<>(this, R.layout.spinner);
        themaAdapter.addAll(themaList);

        themaSpinner = (Spinner) findViewById(R.id.themaSpinner);
        themaSpinner.setAdapter(themaAdapter);

        themaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Thema newThema = (Thema) themaSpinner.getSelectedItem();
                if (!newThema.getThema().equals(currentThema.getThema())) {
                    currentThema = newThema;
                    hideAnswers();

                    entryAdapter.clear();
                    entryAdapter.addAll(currentThema.getEntryList());
                    entrySpinner.setAdapter(entryAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        if (currentThema == null)
            currentThema = (Thema) themaSpinner.getSelectedItem();


        // STICHWORT SPINNER
        // =================
        entryAdapter = new ArrayAdapter<>(this, R.layout.spinner);
        entryAdapter.addAll(currentThema.getEntryList());

        entrySpinner = (Spinner) findViewById(R.id.entrySpinner);
        entrySpinner.setAdapter(entryAdapter);
        entrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                currentEntry = (Entry) entrySpinner.getSelectedItem();
                hideAnswers();

                // select last entry on entry spinner when requested
                if (selectLastEntry) {
                    selectLastEntry = false;
                    entrySpinner.setSelection(currentThema.getEntryList().size() - 1);
                }

                // set a random entry
                if (shuffleEntry) {
                    shuffleEntry = false;
                    entrySpinner.setSelection(random.nextInt(currentThema.getEntryList().size()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });


        // Set the hardware buttons to control the music
        //this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onAntwortButton(View view) {

        antwortText1.setText(currentEntry.getAntwort1());
        antwortButton1.setVisibility(View.VISIBLE);

        if (!currentEntry.getAntwort2().isEmpty()) {
            antwortText2.setText(currentEntry.getAntwort2());
            antwortButton2.setVisibility(View.VISIBLE);
        }

        if (!currentEntry.getAntwort3().isEmpty()) {
            antwortText3.setText(currentEntry.getAntwort3());
            antwortButton3.setVisibility(View.VISIBLE);
        }
    }


    public void onAntwortButton1(View view) {
        playMedia(currentEntry.getAntwort1Path());
    }

    public void onAntwortButton2(View view) {
        playMedia(currentEntry.getAntwort2Path());
    }

    public void onAntwortButton3(View view) {
        playMedia(currentEntry.getAntwort3Path());
    }


    public void onStichwortButton(View view) {
        playMedia(currentEntry.getStichwortPath());
    }

    public void onThemaButton(View view) {
        playMedia(currentThema.getThemaResPaht());
    }

    public void onFrageButton2(View view) {
        playMedia(currentEntry.getFragePath());
    }

    public void onFrageButton(View view) {
        frageButton2.setVisibility(View.VISIBLE);
        frageText.setText(currentEntry.getFrage());
    }


    public void onShuffleButton(View view) {
        shuffleEntry = true;
        themaSpinner.setSelection(random.nextInt(themaList.size()));
    }

    public void onLeftButton(View view) {
        int entryIndex = currentThema.getEntryList().indexOf(currentEntry);
        if (entryIndex <= 0) {
            selectLastEntry = true;
            int themaIndex = themaList.indexOf(currentThema);
            if (themaIndex <= 0) {
                themaSpinner.setSelection(themaList.size() - 1);
            } else {
                themaSpinner.setSelection(themaIndex - 1);
            }
        } else {
            entrySpinner.setSelection(entryIndex - 1);
        }
    }

    public void onRightButton(View view) {
        int entryIndex = currentThema.getEntryList().indexOf(currentEntry);
        if (entryIndex >= currentThema.getEntryList().size() - 1) {
            int themaIndex = themaList.indexOf(currentThema);
            if (themaIndex >= themaList.size() - 1) {
                themaSpinner.setSelection(0);
            } else {
                themaSpinner.setSelection(themaIndex + 1);
            }
        } else {
            entrySpinner.setSelection(entryIndex + 1);
        }
    }


    private void playMedia(String resource) {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
            } else {
                mediaPlayer = new MediaPlayer();
            }

            AssetFileDescriptor descriptor = getAssets().openFd(resource);
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mediaPlayer.prepare();
            mediaPlayer.setVolume(1f, 1f);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();


        } catch (Exception e) {
            Log.e("MainActivity", "error playing madia", e);
        }

    }

    private void hideAnswers() {
        antwortText1.setText("");
        antwortText2.setText("");
        antwortText3.setText("");
        frageText.setText("");

        antwortButton1.setVisibility(View.GONE);
        antwortButton2.setVisibility(View.GONE);
        antwortButton3.setVisibility(View.GONE);
        frageButton2.setVisibility(View.GONE);
    }

    private void initLists() {
        themaList = new ArrayList<>();

        initBerufUndArbeiten();
        initEinkaufen();
        initEinkaufen();
        initEssenUndTrinken();
        initFamilie();
        initFreizeit();
        initFreunde();
        initKleidung();
        initKoerperGesundheit();
        initMoebel();
        initReisen();
        initSport();
        initSprachenLernen();
        initTagesablaufAlltag();
        initUrlaub();
        initVerkeht();
        initWochenende();
        initWohnen();

    }

    @SuppressWarnings("SpellCheckingInspection")
    private void initBerufUndArbeiten() {
        Thema thema = new Thema("Beruf und Arbeit", "audio/themen/BerufUndArbeit.ogg");
        themaList.add(thema);
        ArrayList<Entry> el = thema.getEntryList();


        el.add(new Entry(
                "Arbeiten", "audio/stichworte/arbeiten.ogg",
                "Wo Arbeiten Sie?", "audio/fragen/WoArbeitenSie.ogg",
                "Ich Arbeite nicht, ich bin Hausfrau", "audio/antworten/IchArbeiteNichtIchBinHausfrau.ogg",
                "Ich Arbeite nicht, ich bin Schülerin", "audio/antworten/IchArbeiteNichtIchBinSchuelerin.ogg"));


        el.add(new Entry(
                "Arbeitskleidung", "audio/stichworte/arbeitskleidung.ogg",
                "Haben Sie Arbeitskleidung?", "audio/fragen/HabenSieArbeitskleidung.ogg",
                "Nein ich habe keine Arbeitskleidung", "audio/antworten/NeinIchHabeKeineArbeitskleidung.ogg"));

        el.add(new Entry(
                "Arbeitsplatz", "audio/stichworte/arbeitsplatz.ogg",
                "Wo ist ihr Arbeitsplatz?", "audio/fragen/WoIstIhrArbeitsplatz.ogg",
                "In Chonburi", "audio/antworten/InConburi.ogg"));

        el.add(new Entry(
                "Arbeitszeit", "audio/stichworte/arbeitszeit.ogg",
                "Wie sind ihre Arbeitszeiten?", "audio/fragen/WieSindIhreArbeitszeiten.ogg",
                "Ich Arbeit von 8:00 bis 17:00 Uhr", "audio/antworten/IchArbeiteVon8Bis17Uhr.ogg"));

        el.add(new Entry(
                "Aufgaben", "audio/stichworte/aufgaben.ogg",
                "Was sind Ihre Aufgaben im Beruf", "audio/fragen/WasSindIhreAufgabenImBeruf.ogg",
                "Ich muss Telefonieren und verkaufen", "audio/antworten/IchMussTelefonierenUndVerkaufen.ogg",
                "Ich habe keinen Beruf, ich gehe noch zur Schule", "audio/antworten/IchHabeKeinenBerufIchGeheNochZurSchule.ogg"));

        el.add(new Entry(
                "Auto", "audio/stichworte/auto.ogg",
                "Fahren Sie mit dem Auto zur Arbeit?", "audio/fragen/FahrenSieMitDemAutoZurArbeit.ogg",
                "Nein ich fahre mit dem Bus", "audio/antworten/NeinIchFahreMitDemBus.ogg"));

        el.add(new Entry(
                "Beruf", "audio/stichworte/beruf.ogg",
                "Was sind sie von Beruf?", "audio/fragen/WasSindSieVonBeruf.ogg",
                "Ich bin Hausfrau von Beruf", "audio/antworten/IchBinHausfrauVonBeruf.ogg",
                "Ich habe keinen Beruf, ich gehe noch zur Schule", "audio/antworten/IchHabeKeinenBerufIchGeheNochZurSchule.ogg"));

        el.add(new Entry(
                "Computer", "audio/stichworte/computer.ogg",
                "Arbeiten Sie mit dem Computer?", "audio/fragen/ArbeitenSieMitDemComputer.ogg",
                "Ja, ich Arbeite mit dem Computer", "audio/antworten/JaIchArbeiteMitDemComputer.ogg"));

        el.add(new Entry(
                "Firma", "audio/stichworte/firma.ogg",
                "Wo ist ihre Firma?", "audio/fragen/WoIstIhreFirma.ogg",
                "In Chonburi", "audio/antworten/InConburi.ogg"));

        el.add(new Entry(
                "Geld", "audio/stichworte/geld.ogg",
                "Wie viel Geld haben Sie im Monat", "audio/fragen/WievieGeldHabenSieImMonat.ogg",
                "Ich habe 2000 Bath im Monat", "audio/antworten/IchHabe2000BathImMonat.ogg"));

        el.add(new Entry(
                "Kollegen", "audio/stichworte/kollegen.ogg",
                "Sind ihre Kollegen nett?", "audio/fragen/SindIhreKollegenNett.ogg",
                "Ja, sie sind sehr nett", "audio/antworten/JaSieSindSehrNett.ogg"));

        el.add(new Entry(
                "Mittagspause", "audio/stichworte/mittagessen.ogg",
                "Wann haben Sie Mittagspause?", "audio/fragen/WannHabenSieMittagspause.ogg",
                "Immer um 12 Uhr", "audio/antworten/ImmerUm12Uhr.ogg"));

        el.add(new Entry(
                "Pause", "audio/stichworte/pause.ogg",
                "Wann haben Sie Pause?", "audio/fragen/WannHabenSiePause.ogg",
                "Immer um 12 Uhr", "audio/antworten/ImmerUm12Uhr.ogg"));

        el.add(new Entry(
                "Spaß", "audio/stichworte/spass.ogg",
                "Mach ihnen ihr Beruf spaß?", "audio/fragen/MachtIhnenIhrBerufSpass.ogg",
                "Ja, mein Beruf macht mir sehr viel Spaß", "audio/antworten/JaMeinBerufMachMirSehrVielSpass.ogg"));

        el.add(new Entry(
                "Traumberuf", "audio/stichworte/traumberuf.ogg",
                "Was ist ihr Traumberuf", "audio/fragen/WasIstIhrTraumberuf.ogg",
                "Mein Traumberuf ist Ärztin", "audio/antworten/MeinTraumberufIstAertztin.ogg"));

        el.add(new Entry(
                "Urlaub", "audio/stichworte/urlaub.ogg",
                "Wann haben Sie Urlaub?", "audio/fragen/WannHabenSieUrlaub.ogg",
                "Ich habe im Juni Urlaub", "audio/antworten/IchHabeImJuniUrlaub.ogg"));

    }

    @SuppressWarnings("SpellCheckingInspection")
    private void initEinkaufen() {
        Thema thema = new Thema("Einkaufen", "audio/themen/Einkaufen.ogg");
        themaList.add(thema);
        ArrayList<Entry> el = thema.getEntryList();

        el.add(new Entry(
                "Bäckerei", "audio/stichworte/baeckerei.ogg",
                "Wie oft gehen Sie zur Bäckerei?", "audio/fragen/WieOftGehenSieZurBaeckerei.ogg",
                "Jeden morgen", "audio/antworten/JedenMorgen.ogg"));

        el.add(new Entry(
                "Brille", "audio/stichworte/brille.ogg",
                "Wo kann ich eine Brille kaufen?", "audio/fragen/WoKannIchEineBrilleKaufen.ogg",
                "Im Brillengeschäft", "audio/antworten/ImBrillengeschaeft.ogg"));

        el.add(new Entry(
                "Buch", "audio/stichworte/buch.ogg",
                "Wo kann ich ein Buch kaufen?", "audio/fragen/WoKannIchEinBuchKaufen.ogg",
                "Im Buchladen", "audio/antworten/ImBuchladen.ogg"));

        el.add(new Entry(
                "Buchladen", "audio/stichworte/buchladen.ogg",
                "Wo ist ein Buchladen", "audio/fragen/WoIstEinBuchladen.ogg",
                "Da vorne", "audio/antworten/DaVorne.ogg",
                "Da hinten", "audio/antworten/DaHinten.ogg"));

        el.add(new Entry(
                "Computer", "audio/stichworte/computer.ogg",
                "Wo kann ich einen Computer kaufen?", "audio/fragen/WoKannIchEinenComputerKaufen.ogg",
                "Im Big C", "audio/antworten/ImBigC.ogg",
                "Im Computerladen", "audio/antworten/ImComputerladen.ogg"));

        el.add(new Entry(
                "Feierabend", "audio/stichworte/feierabend.ogg",
                "Gehen sie einkaufen nach ihrem Feierabend?", "audio/fragen/GehenSieEinkaufenNachIhremFeierabend.ogg",
                "Ja, im Kaufhaus", "audio/antworten/JaImKaufhaus.ogg"));

        el.add(new Entry(
                "Gemüse", "audio/stichworte/gemuese.ogg",
                "Wo kann ich Gemüse kaufen?", "audio/fragen/WoKannIchGemueseKaufen.ogg",
                "Im Supermarkt", "audio/antworten/ImSuppermarkt.ogg"));

        el.add(new Entry(
                "geöffnet", "audio/stichworte/geoeffnet.ogg",
                "Wann hat der Supermarkt geöffnet?", "audio/fragen/WannHatDerSuppermarktGeoeffnet.ogg",
                "Von zehn(10) bis achtzehn(18) Uhr", "audio/antworten/Von10bis18Uhr.ogg"));

        el.add(new Entry(
                "Geschäft", "audio/stichworte/geschaeft.ogg",
                "In welchem Geschäft kaufen sie gerne ein?", "audio/fragen/InWelchemGeschaeftKaufenSieGerneEin.ogg",
                "Im Big C", "audio/antworten/ImBigC.ogg"));

        el.add(new Entry(
                "Handy", "audio/stichworte/handy.ogg",
                "Können sie mir ein Handy kaufen?", "audio/fragen/KoennenSieMirEinHandyKaufen.ogg",
                "Nein, das kann ich nicht", "audio/antworten/NeinDasKannIchNicht.ogg"));

        el.add(new Entry(
                "Hose", "audio/stichworte/hose.ogg",
                "Wo kann ich eine Hose einkaufen", "audio/fragen/WoKannIchEineHoseKaufen.ogg",
                "Im Kaufhaus", "audio/antworten/ImKaufhaus.ogg"));

        el.add(new Entry(
                "Kasse", "audio/stichworte/kasse.ogg",
                "Wo ist hier die Kasse?", "audio/fragen/WoIstHierDieKasse.ogg",
                "Da vorne", "audio/antworten/DaVorne.ogg",
                "Da hinten", "audio/antworten/DaHinten.ogg"));

        el.add(new Entry(
                "Kiosk", "audio/stichworte/kiosk.ogg",
                "Wo ist der Kiosk?", "audio/fragen/WoIstDerKisok.ogg",
                "Da vorne", "audio/antworten/DaVorne.ogg",
                "Da hinten", "audio/antworten/DaHinten.ogg"));

        el.add(new Entry(
                "Kleidung", "audio/stichworte/kleidung.ogg",
                "Wo kann man billige Kleidung kaufen?", "audio/fragen/WoKannManBilligeKleidungKaufen.ogg",
                "Im Supermarkt", "audio/antworten/ImSuppermarkt.ogg",
                "Im Lotus", "audio/antworten/ImLotus.ogg"));

        el.add(new Entry(
                "Kreditkarte", "audio/stichworte/kreditkarte.ogg",
                "Wo kann man mit Kreditkarte bezahlen?", "audio/fragen/WoKannManMitKredikarteBezahlen.ogg",
                "An der Kasse", "audio/antworten/AnDerKasse.ogg"));

        el.add(new Entry(
                "Obst", "audio/stichworte/obst.ogg",
                "Können sie mir bitte Obst kaufen?", "audio/fragen/KoennenSieMirBitteObsKaufen.ogg",
                "Ja, natürlich", "audio/antworten/JaNatuerlich.ogg"));

        el.add(new Entry(
                "Post", "audio/stichworte/post.ogg",
                "Kann ich auf der Post Briefmarken kaufen?", "audio/fragen/KannIchAufDerPostBriefmarkenKaufen.ogg",
                "Ja, natürlich", "audio/antworten/JaNatuerlich.ogg"));

        el.add(new Entry(
                "Schuhe", "audio/stichworte/schuhe.ogg",
                "Wo kaufen sie ihre Schuhe?", "audio/fragen/WoKaufenSieIhreSchuhe.ogg",
                "Im Schuhladen", "audio/antworten/ImSchuhladen.ogg",
                "Im Big C", "audio/antworten/ImBigC.ogg"));

        el.add(new Entry(
                "Stadtplan", "audio/stichworte/stadtplan.ogg",
                "Wo kann ich einen Stadtplan kaufen?", "audio/fragen/WoKannIchEinenStadtplanKaufen.ogg",
                "Im Buchladen", "audio/antworten/ImBuchladen.ogg"));

        el.add(new Entry(
                "Ticket", "audio/stichworte/ticket.ogg",
                "Wo kann ich ein Ticket kaufen?", "audio/fragen/WoKannIchEinTicketKaufen.ogg",
                "Am Schalter", "audio/antworten/AmSchalter.ogg",
                "Am Automaten", "audio/antworten/AmAutomaten.ogg"));

        el.add(new Entry(
                "Zeitung", "audio/stichworte/zeitung.ogg",
                "Wo kann ich eine Zeitung kaufen?", "audio/fragen/WoKannIchEineZeitungKaufen.ogg",
                "Im Kisok", "audio/antworten/ImKiosk.ogg",
                "Im Buchladen", "audio/antworten/ImBuchladen.ogg"));

    }


    @SuppressWarnings("SpellCheckingInspection")
    private void initEssenUndTrinken() {
        Thema thema = new Thema("Essen und Trinken", "audio/themen/EssenUndTrinken.ogg");
        themaList.add(thema);
        ArrayList<Entry> el = thema.getEntryList();

        el.add(new Entry(
                "Abendessen", "audio/stichworte/abendessen.ogg",
                "Was essen Sie zu Abend?", "audio/fragen/WasEssenSieZuAbend.ogg",
                "Ich esse Spaghetti zu Abend", "audio/antworten/IchEsseSpaghettiZuAbend.ogg"));

        el.add(new Entry(
                "Frühstück", "audio/stichworte/fruestueck.ogg",
                "Um wie viel Uhr Frühstücken Sie?", "audio/fragen/UmWievielUhrFruestueckenSie.ogg",
                "Immer um 7 Uhr", "audio/antworten/ImmerUm7Uhr.ogg"));

        el.add(new Entry(
                "Lieblingsessen", "audio/stichworte/lieblingsessen.ogg",
                "Was ist ihr Lieblingsessen?", "audio/fragen/WasIstIhrLieblingsessen.ogg",
                "Mein Lieblingsessen ist Pizza", "audio/antworten/MeinLieblingsessenIstPizza.ogg"));

        el.add(new Entry(
                "Lieblingsgetränk", "audio/stichworte/lieblingsgetraenk.ogg",
                "Was ist ihr Lieblingsgetränk?", "audio/fragen/WasIstIhrLieblingsgetraenk.ogg",
                "Mein Lieblingsgetränk ist Cola", "audio/antworten/MeinLieblingsgetraenkIstCola.ogg"));

        el.add(new Entry(
                "Mittagessen", "audio/stichworte/mittagessen.ogg",
                "Was essen sie zu Mittag?", "audio/fragen/WasEssenSieZuMittag.ogg",
                "Ich esse Spaghetti zu Mittag", "audio/antworten/IchEsseSpaghettiZuMittag.ogg"));

        el.add(new Entry(
                "Restaurant", "audio/stichworte/restaurant.ogg",
                "Gehen Sie oft ins Restaurant?", "audio/fragen/GehenSieOftInsReataurant.ogg",
                "Nein, nicht so oft", "audio/antworten/NeinNichtSoOft.ogg"));

        el.add(new Entry(
                "Wasser", "audio/stichworte/wasser.ogg",
                "Wollen Sie ein Glas Wasser trinken?", "audio/fragen/WollenSieEinGlasWasserTrinken.ogg",
                "Nein, danke, ich trinke lieber Cola", "audio/antworten/NeinDankeIchTrinkeLieberCola.ogg"));

    }

    @SuppressWarnings("SpellCheckingInspection")
    private void initFamilie() {
        Thema thema = new Thema("Familie", "audio/themen/Familie.ogg");
        themaList.add(thema);
        ArrayList<Entry> el = thema.getEntryList();

        el.add(new Entry(
                "Einkaufen", "audio/stichworte/einkaufen.ogg",
                "Wo kaufen sie mit Ihrer Familie ein?", "audio/fragen/WoKaufenSieMitIhrerFamileEin.ogg",
                "Wir kaufen im Supermarkt ein", "audio/antworten/WirKaufenImSupermarktEin.ogg",
                "Wir kaufen im Lotus ein", "audio/antworten/WirKaufenImLotusEin.ogg"));

        el.add(new Entry(
                "Eltern", "audio/stichworte/eltern.ogg",
                "Wo wohnen ihre Eltern?", "audio/fragen/WoWohenIhreEltern.ogg",
                "Sie Wohnen in Ubonratchathani", "audio/antworten/SieWohnenInUbonRatchathani.ogg"));

        el.add(new Entry(
                "Geburtstag", "audio/stichworte/geburtstag.ogg",
                "Was machen sie mit ihrer Familie am Geburtstag?", "audio/fragen/WasMachenSieMitIhrerFamilieAmGeburtstag.ogg",
                "Wir machen eine Party", "audio/antworten/WirMachenEineParty.ogg"));

        el.add(new Entry(
                "Geschenk", "audio/stichworte/geschenk.ogg",
                "Wo kann ich ein Geschenk für meine Mutter kaufen?", "audio/fragen/WoKannIchEinGeschenkFuerMeineMutterKaufen.ogg",
                "Im Kaufhaus", "audio/antworten/ImKaufhaus.ogg"));

        el.add(new Entry(
                "Großeltern", "audio/stichworte/grosseltern.ogg",
                "Haben Sie noch Großeltern?", "audio/fragen/HabenSieNochGrosseltern.ogg",
                "Ja, eine Großmutter", "audio/antworten/JaIchHabeEineGrossmutter.ogg",
                "Ja, ich habe noch vier Großeltern", "audio/antworten/JaIchHabeNoch4Grosseltern.ogg"));

        el.add(new Entry(
                "Großmutter", "audio/stichworte/grossmutter.ogg",
                "Wo wohnt Ihre Großmutter?", "audio/fragen/WoWohntIhreGrossmutter.ogg",
                "Sie wohnt in Ubonratchathani", "audio/antworten/SieWohnenInUbonRatchathani.ogg"));

        el.add(new Entry(
                "Hobby", "audio/stichworte/hobby.ogg",
                "Welches Hobby machen sie mit ihrer Familie?", "audio/fragen/WelchesHobbyMachenSieMitIHrerFamilie.ogg",
                "Wir gehen Fahrradfahren", "audio/antworten/WirGehenFahrradfahren.ogg",
                "Wir Spielen Tennis", "audio/antworten/WirSpielenTennis.ogg"));

        el.add(new Entry(
                "Kinder", "audio/stichworte/kinder.ogg",
                "Haben sie Kinder?", "audio/fragen/HabenSieKinder.ogg",
                "Ja, ich habe 2 Kinder", "audio/antworten/JaIchHabeZweiKinder.ogg",
                "Nein, ich habe noch keine Kinder", "audio/antworten/NeinIchHabeNochKeineKinder.ogg"));

        el.add(new Entry(
                "Schule", "audio/stichworte/schuhe.ogg",
                "Wo ist die Schule ihrer Kinder?", "audio/fragen/WoIstDieSchuleIhrerKinder.ogg",
                "In Sattahip", "audio/antworten/InSattahip.ogg"));

        el.add(new Entry(
                "Verheiratet", "audio/stichworte/verheiratet.ogg",
                "Sind Sie schon verheiratet?", "audio/fragen/SindSieSchonVerheiratet.ogg",
                "Ja, ich bin verheiratet", "audio/antworten/JaIchBinVerheiratet.ogg",
                "Nein ich bin noch ledig", "audio/antworten/NeinIchBinNochLedig.ogg"));

        el.add(new Entry(
                "Wochenende", "audio/stichworte/wochenende.ogg",
                "Was macht ihre Familie am Wochenende?", "audio/fragen/WasMachtIhreFamilieAmWochenende.ogg",
                "Wir gehen Fahrradfahren", "audio/antworten/WirGehenFahrradfahren.ogg",
                "Wir gehen einkaufen", "audio/antworten/WirGehenEinkaufen.ogg"));


    }

    @SuppressWarnings("SpellCheckingInspection")
    private void initFreizeit() {
        Thema thema = new Thema("Freizeit", "audio/themen/Freizeit.ogg");
        themaList.add(thema);
        ArrayList<Entry> el = thema.getEntryList();

        el.add(new Entry(
                "Abend", "audio/stichworte/abend.ogg",
                "Was machen sie am Abend?", "audio/fragen/WasMachenSieAmAbend.ogg",
                "Ich sehe Fern", "audio/antworten/IchSeheFern.ogg",
                "Ich esse am Abend", "audio/antworten/IchEsseAmAbend.ogg",
                "Ich koche", "audio/antworten/IchKoche.ogg"));

        el.add(new Entry(
                "Bücher", "audio/stichworte/buecher.ogg",
                "Lesen sie viele Bücher in ihrer Freizeit?", "audio/fragen/LesenSieVielBuecherInIhrerFreizeit.ogg",
                "Ja, viele", "audio/antworten/JaViele.ogg",
                "Nein, nicht so viele", "audio/antworten/NeinNichtSoViele.ogg"));

        el.add(new Entry(
                "Einkaufen", "audio/stichworte/einkaufen.ogg",
                "Gehen Sie gerne einkaufen in ihrer Freizeit?", "audio/fragen/GehenSieGerneEinkaufenInIhrerFreizeit.ogg",
                "Ja, sehr gerne", "audio/antworten/JaSehrGerne.ogg"));

        el.add(new Entry(
                "Feierabend", "audio/stichworte/feierabend.ogg",
                "Was machen Sie nach ihrem Feierabend?", "audio/fragen/WasMachenSieNachIhremFeierabend.ogg",
                "Ich gehe nach Hause", "audio/antworten/IchGeheNachHause.ogg"));

        el.add(new Entry(
                "Fernsehen", "audio/stichworte/fernsehen.ogg",
                "Sehen Sie in Ihrer Freizeit Fern?", "audio/fragen/SehenSieInIhrerFreizeitFern.ogg",
                "Ja, ich sehe gerne Fern", "audio/antworten/JaIchSeheGerneFern.ogg"));

        el.add(new Entry(
                "Freunde", "audio/stichworte/freunde.ogg",
                "Was machen Sie in Ihrer Freizeit mit ihren Freunden?", "audio/fragen/WasMachenSieInIhrerFreizeitMitIhrenFreunden.ogg",
                "Wir gehen ins Kino", "audio/antworten/WirGehenInsKino.ogg",
                "Wir gehen Fahrradfahren", "audio/antworten/WirGehenFahrradfahren.ogg",
                "Wir gehen Einkaufen", "audio/antworten/WirGehenEinkaufen.ogg"));

        el.add(new Entry(
                "gern", "audio/stichworte/gern.ogg",
                "Was machen Sie gern in ihrer Freizeit", "audio/fragen/WasMachenSieGerneInIhrerFreizeit.ogg",
                "Ich Schwimme gerne", "audio/antworten/IchSchwimmeGerne.ogg"));

        el.add(new Entry(
                "Hobby", "audio/stichworte/hobby.ogg",
                "Was ist ihr Hobby", "audio/fragen/WasIstIhrHobby.ogg",
                "Mein Hobby ist Fahrradfahren", "audio/antworten/MeinHobbyIstFahradfahren.ogg",
                "Mein Hobby ist Schwimmen", "audio/antworten/MeinHobbyIstSchwimmen.ogg"));

        el.add(new Entry(
                "Kino", "audio/stichworte/kino.ogg",
                "Gehen sie gerne in ihrer Freizeit ins Kino?", "audio/fragen/GehenSieGerneInIhrerFreizeitInsKino.ogg",
                "Ja, sehr gerne", "audio/antworten/JaSehrGerne.ogg"));

        el.add(new Entry(
                "Lieblingssport", "audio/stichworte/lieblingssport.ogg",
                "Was ist ihr Lieblingssport", "audio/fragen/WasIstIhrLieblingssport.ogg",
                "Mein Lieblingssport ist Fahrradfahren", "audio/antworten/MeinLieblingssportIstFahradfahren.ogg"));

        el.add(new Entry(
                "oft", "audio/stichworte/oft.ogg",
                "Gehen sie oft in Ihrer Freizeit ins Kino?", "audio/fragen/GehenSieOftInIhrerFreizeitInsKino.ogg",
                "Nicht so oft", "audio/antworten/NichtSoOft.ogg"));

        el.add(new Entry(
                "Sport", "audio/stichworte/sport.ogg",
                "Machen Sie Sport in ihrer Freizeit", "audio/fragen/MachenSieSportInIhrerFreizeit.ogg",
                "Ja, aber nicht viel", "audio/antworten/JaAberNichtViel.ogg",
                "Nein, ich bin zu Faul", "audio/antworten/NeinIchBinZuFaul.ogg"));

        el.add(new Entry(
                "Wochenende", "audio/stichworte/wochenende.ogg",
                "Was machen Sie am Wochenende", "audio/fragen/WasMachenSieAmWochenende.ogg",
                "Ich gehe Fahrradfahren", "audio/antworten/IchGeheFahrradfahren.ogg"));

    }

    @SuppressWarnings("SpellCheckingInspection")
    private void initFreunde() {
        Thema thema = new Thema("Freunde", "audio/themen/Freunde");
        themaList.add(thema);
        ArrayList<Entry> el = thema.getEntryList();

        el.add(new Entry(
                "Ausflug", "audio/stichworte/ausflug.ogg",
                "Machen Sie oft Ausflug mit ihren Freunden?", "audio/fragen/MachenSieOftAusflugMitIhrenFreunden.ogg",
                "Ja, sehr oft", "audio/antworten/JaSehrOft.ogg"));

        el.add(new Entry(
                "Geburtstag", "audio/stichworte/geburtstag.ogg",
                "Was machen Sie am Geburtstag mit Ihren Freunden?", "audio/fragen/WasMachenSieAmGeburtstagMitIhrenFreunden.ogg",
                "Wie machen eine Party", "audio/antworten/WirMachenEineParty.ogg"));

        el.add(new Entry(
                "Reise", "audio/stichworte/reise.ogg",
                "Reisen Sie gerne mit ihren Freunden?", "audio/fragen/ReisenSieGerneMitIhrenFreunden.ogg",
                "Nein, nicht so gern", "audio/antworten/NeinNichtSoGern.ogg"));

        el.add(new Entry(
                "Restaurant", "audio/stichworte/restaurant.ogg",
                "Wann gehen Sie mit ihren Freunden ins Restaurant?", "audio/fragen/WannGehenSieMitIhrenFreundenInsRestaurant.ogg",
                "Nach der Arbeit", "audio/antworten/NachDerArbeit.ogg"));

        el.add(new Entry(
                "Sprachkurs", "audio/stichworte/sprachkurs.ogg",
                "Haben Sie viele Freunde im Sprachkurs", "audio/fragen/HabenSieVielFreundeImSprachkurs.ogg",
                "Ja, ich habe viele Freunde", "audio/antworten/IchHabeVieleFreunde.ogg",
                "Nein, nicht so viele", "audio/antworten/NeinNichtSoViele.ogg"));

        el.add(new Entry(
                "Wochenende", "audio/stichworte/wochenende.ogg",
                "Sind Sie am Wochenende bei ihren Freunden?", "audio/fragen/SindSieAmWochenendeBeiIhrenFreunden.ogg",
                "Ja, ich bin am Wochenende bei meinen Freunden", "audio/antworten/JaIchBinAmWochenEndeBeiMeinenFreunden.ogg"));

    }

    @SuppressWarnings("SpellCheckingInspection")
    private void initKleidung() {
        Thema thema = new Thema("Kleidung", "audio/themen/Kleidung.ogg");
        themaList.add(thema);
        ArrayList<Entry> el = thema.getEntryList();

        el.add(new Entry(
                "Farbe", "audio/stichworte/farbe.ogg",
                "Welche Farbe mögen sie am liebsten bei Kleidung?", "audio/fragen/WelcheFarbeMoechtenSieAmLiebstenBeiKleidung.ogg",
                "Rot", "audio/antworten/Rot.ogg"));

        el.add(new Entry(
                "Geschäft", "audio/stichworte/geschaeft.ogg",
                "Kaufen Sie Kleidung im Geschäft ein?", "audio/fragen/KaufenSieKleidungImGeschaftEin.ogg",
                "Ja", "audio/antworten/Ja.ogg"));

        el.add(new Entry(
                "Lieblingspullover", "audio/stichworte/lieblingspulover.ogg",
                "Haben Sie einen Lieblingspulover", "audio/fragen/HabenSieEinenLieblingspulover.ogg",
                "Ja der rote", "audio/antworten/JaDerRote.ogg"));

        el.add(new Entry(
                "Party", "audio/stichworte/party.ogg",
                "Welche Kleidung möchten sie für eine Party?", "audio/fragen/WelcheKleidungMoechtenSieFuerEineParty.ogg",
                "Ich brauche schöne Kleidung", "audio/antworten/IchBrauchSchoeneKleidung.ogg"));

        el.add(new Entry(
                "Reise", "audio/stichworte/reise.ogg",
                "Wie viel Kleidung brauchen sie für die Reise?", "audio/fragen/WievielKleidungBrauchenSiefuerDieReise.ogg",
                "Sehr viel", "audio/antworten/SehrViel.ogg"));

        el.add(new Entry(
                "Schuhe", "audio/stichworte/schuhe.ogg",
                "Wie viele Schuhe haben Sie?", "audio/fragen/WievieleSchuheHabenSie.ogg",
                "Ich habe 5 paar Schuhe", "audio/antworten/IchHabe5PaarSchuhe.ogg"));

    }


    @SuppressWarnings("SpellCheckingInspection")
    private void initKoerperGesundheit() {
        Thema thema = new Thema("Körper / Gesundheit", "audio/themen/KoerperGesundheit.ogg");
        themaList.add(thema);
        ArrayList<Entry> el = thema.getEntryList();

        el.add(new Entry(
                "Arzt", "audio/stichworte/arzt.ogg",
                "Gehen sie oft zum Arzt?", "audio/fragen/GehenSieOftZumArzt.ogg",
                "Nein, nicht so oft", "audio/antworten/NeinNichtSoOft.ogg"));

        el.add(new Entry(
                "gesund", "audio/stichworte/gesund.ogg",
                "Sind Sie heute gesund?", "audio/fragen/SindSieHeuteGesund.ogg",
                "Ja, ich bin gesund", "audio/antworten/JaIchBinGesund.ogg"));

        el.add(new Entry(
                "Grippe", "audio/stichworte/grippe.ogg",
                "Haben Sie die Grippe?", "audio/fragen/HabenSieGrippe.ogg",
                "Nein", "audio/antworten/Nein.ogg"));

        el.add(new Entry(
                "Halsschmerzen", "audio/stichworte/halsschmerzen.ogg",
                "Haben sie Halsschmerzen?", "audio/fragen/HabenSieHalsschmerzen.ogg",
                "Nein", "audio/antworten/Nein.ogg"));

        el.add(new Entry(
                "Kopf", "audio/stichworte/Kopf",
                "Tut ihr Kopf noch weh?", "audio/fragen/TutIhrKopfNochWeh.ogg",
                "Ja, immer noch", "audio/antworten/JaImmerNoch.ogg",
                "Nein ich habe keine Kopfschmerzen", "audio/antworten/NeinIchHabeKeineKopfschmerzen.ogg"));

        el.add(new Entry(
                "Termin", "audio/stichworte/termin.ogg",
                "Wann haben Sie einen Termin beim Arzt?", "audio/fragen/WannHabenSieEinenTerminBeimArzt.ogg",
                "Am Montag", "audio/antworten/AmMontag.ogg"));


    }

    @SuppressWarnings("SpellCheckingInspection")
    private void initMoebel() {
        Thema thema = new Thema("Möbel", "audio/themen/Moebel.ogg");
        themaList.add(thema);
        ArrayList<Entry> el = thema.getEntryList();

        el.add(new Entry(
                "Bett", "audio/stichworte/bett.ogg",
                "Haben Sie ein großes Bett?", "audio/fragen/HabenSieEinGrossesBett.ogg",
                "Ja, mein Bett ist sehr groß", "audio/antworten/JaMeinBettIstGross.ogg"));

        el.add(new Entry(
                "Herd", "audio/stichworte/herd.ogg",
                "Wo steht Ihr Herd?", "audio/fragen/WoStehtIhrHerd.ogg",
                "In der Küche", "audio/antworten/InDerKueche.ogg"));

        el.add(new Entry(
                "Kühlschrank", "audio/stichworte/kuehlschrank.ogg",
                "Haben Sie einen Kühlschrank?", "audio/fragen/HabenSieEinenKuehlschrank.ogg",
                "Ja, natürlich", "audio/antworten/JaNatuerlich.ogg"));

        el.add(new Entry(
                "Sofa", "audio/stichworte/sofa.ogg",
                "Wo haben Sie Ihr Sofa gekauft?", "audio/fragen/WoHabenSieIhrSofaGekauft.ogg",
                "Im Index", "audio/antworten/ImIndex.ogg"));

        el.add(new Entry(
                "Tisch", "audio/stichworte/tisch.ogg",
                "Haben Sie einen Tisch zu Hause?", "audio/fragen/HabenSieEinenTischZuHause.ogg",
                "Ja, ich habe zwei Tische", "audio/antworten/JaIchHabe2Tische.ogg"));

    }

    @SuppressWarnings("SpellCheckingInspection")
    private void initReisen() {
        Thema thema = new Thema("Reisen", "audio/themen/Reisen.ogg");
        themaList.add(thema);
        ArrayList<Entry> el = thema.getEntryList();

        el.add(new Entry(
                "Ausland", "audio/stichworte/ausland.ogg",
                "Reisen sie gerne ins Ausland?", "audio/fragen/ReisenSieGerneInsAusland.ogg",
                "Ja, ich reise gern ins Ausland", "audio/antworten/JaIchReiseGernInsAusland.ogg"));

        el.add(new Entry(
                "Auto", "audio/stichworte/auto.ogg",
                "Reisen sie gerne mit dem Auto?", "audio/fragen/ReisenSieGerneMitDemAuto.ogg",
                "Ja, natürlich", "audio/antworten/JaNatuerlich.ogg"));

        el.add(new Entry(
                "Bahnhof", "audio/stichworte/bahnhof.ogg",
                "Wie komme ich zum Bahnhof?", "audio/fragen/WieKommeIchZumBahnhof.ogg",
                "Fahren sie am besten mit dem Taxi", "audio/antworten/FahrenSieAmBestenMitDemTaxi.ogg"));

        el.add(new Entry(
                "Fahrkarte", "audio/stichworte/fahrkarte.ogg",
                "Wo kann ich eine Fahrkarte kaufen?", "audio/fragen/WoKannIchEineFahrkarteKaufen.ogg",
                "Am Bahnhof", "audio/antworten/AmBahnhof.ogg",
                "Am Schalter", "audio/antworten/AmSchalter.ogg",
                "Am Automaten", "audio/antworten/AmAutomaten.ogg.ogg"));

        el.add(new Entry(
                "Familie", "audio/stichworte/familie.ogg",
                "Reisen sie gerne mit ihrer Familie", "audio/fragen/ReisenSieGerneMitIhrerFamilie.ogg",
                "Ja, sehr gerne", "audio/antworten/JaSehrGerne.ogg"));

        el.add(new Entry(
                "Flughafen", "audio/stichworte/flughafen.ogg",
                "Wie komme ich zum Flughafen?", "audio/fragen/WieKommeIchZumFlughafen.ogg.ogg",
                "Fahren sie am besten mit dem Taxi", "audio/antworten/FahrenSieAmBestenMitDemTaxi.ogg"));

        el.add(new Entry(
                "Koffer", "audio/stichworte/koffer.ogg",
                "Mit wie vielen Koffern reisen sie?", "audio/fragen/MitWievielenKoffernReisenSie.ogg",
                "Mit 2 Koffer", "audio/antworten/MitZweiKoffern.ogg"));

        el.add(new Entry(
                "Schalter", "audio/stichworte/schalter.ogg",
                "Wo ist der Schalter von Thai Airways?", "audio/fragen/WoIstDerSchalterDerThaiArways.ogg",
                "Da hinten!", "audio/antworten/DaHinten.ogg",
                "Da vorne!", "audio/antworten/DaVorne.ogg"));

    }

    @SuppressWarnings("SpellCheckingInspection")
    private void initSport() {
        Thema thema = new Thema("Sport", "audio/themen/Sport.ogg");
        themaList.add(thema);
        ArrayList<Entry> el = thema.getEntryList();

        el.add(new Entry(
                "Abend", "audio/stichworte/abend.ogg",
                "Machen sie am Abend Sport?", "audio/fragen/MachenSieAmAbendSport.ogg",
                "Nein, am Abend mache ich keinen Sport", "audio/antworten/NeinAmAbendMachIchKeinenSport.ogg"));

        el.add(new Entry(
                "Ball", "audio/stichworte/ball.ogg",
                "Machen sie gerne Ballsport?", "audio/fragen/MachenSieGerneBallsport.ogg",
                "Ja, sehr gerne", "audio/antwo  rten/JaSehrGerne.ogg"));

        el.add(new Entry(
                "Fahrrad", "audio/stichworte/fahrrad.ogg",
                "Fahren sie auch Fahrrad?", "audio/fragen/FahrenSieAuchFahrrad.ogg",
                "Ja, ich fahre Fahrrad", "audio/antworten/JaIchFahreFahrrad.ogg"));

        el.add(new Entry(
                "Wochenende", "audio/stichworte/wochenende.ogg",
                "Machen sie am Wochenende Sport?", "audio/fragen/MachenSieAmWochenendeSport.ogg",
                "Ja, ich gehe Fahrradfahren", "audio/antworten/JaIchGeheFahrradfahren.ogg",
                "Ja, Ich gehe schwimmen", "audio/antworten/JaIchGeheSchwimmen.ogg",
                "Nein ich bin zu faul", "audio/antworten/NeinIchBinZuFaul.ogg"));


    }

    @SuppressWarnings("SpellCheckingInspection")
    private void initSprachenLernen() {
        Thema thema = new Thema("Sprachen Lernen", "audio/themen/SprachenLernen.ogg");
        themaList.add(thema);
        ArrayList<Entry> el = thema.getEntryList();

        el.add(new Entry(
                "Hausaufgaben", "audio/stichworte/hausaufgaben.ogg",
                "Müssen Sie Hausaufgaben machen?", "audio/fragen/MuessenSieHausaufgabenMachen.ogg",
                "Ja, natürlich", "audio/antworten/JaNatuerlich.ogg"));

        el.add(new Entry(
                "Klasse", "audio/stichworte/klasse.ogg",
                "Wie viele Schüler sind in ihrer Klasse?", "audio/fragen/WieVieleSchuelerSindInIhrerKlasse.ogg",
                "10 (zehn) Schüler", "audio/antworten/10Schueler.ogg"));

        el.add(new Entry(
                "Kurs", "audio/stichworte/kurs.ogg",
                "Welchen Kurs besuchen Sie?", "audio/fragen/WelchenKursBesuchenSie.ogg",
                "Ich besuche den A1 Kurs", "audio/antworten/IchBesucheDenA1Kurs.ogg"));

        el.add(new Entry(
                "Lehrerin", "audio/stichworte/lehrerin.ogg",
                "Wie heißt ihre Lehrerin", "audio/fragen/WieHeistIhreLehrerin.ogg",
                "Sie heißt Maria", "audio/antworten/SieHeisstMaria.ogg"));

        el.add(new Entry(
                "Unterricht", "audio/stichworte/unterricht.ogg",
                "Ist der Unterricht interessant?", "audio/fragen/IstDerUnterrichtInteressant.ogg",
                "Ja, er ist sehr interessant", "audio/antworten/JaIstSehrInteressant.ogg"));

        el.add(new Entry(
                "Unterrichtsstunde", "audio/stichworte/unterrichtsstunde.ogg",
                "Wann fängt der Unterricht an?", "audio/fragen/WannFaengtDerUnterrichtAn.ogg",
                "Um 9 Uhr", "audio/antworten/Um9Uhr.ogg"));


    }

    @SuppressWarnings("SpellCheckingInspection")
    private void initTagesablaufAlltag() {
        Thema thema = new Thema("Tagesablauf / Alltag", "audio/themen/TagesbalufAlltag.ogg");
        themaList.add(thema);
        ArrayList<Entry> el = thema.getEntryList();

        el.add(new Entry(
                "Abend", "audio/stichworte/abend.ogg",
                "Was machen sie am Abend?", "audio/fragen/WasMachenSieAmAbend.ogg",
                "Ich sehe Fern", "audio/antworten/IchSeheFern.ogg",
                "Ich esse am Abend", "audio/antworten/IchEsseAmAbend.ogg",
                "Ich koche", "audio/antworten/IchKoche.ogg"));

        el.add(new Entry(
                "Abendessen", "audio/stichworte/Abendessen",
                "Wann essen sie zu Abend?", "audio/fragen/WannEssenSieZuAbend.ogg",
                "Um 18 Uhr", "audio/antworten/Um18Uhr.ogg"));

        el.add(new Entry(
                "Bett", "audio/stichworte/bett.ogg",
                "Wann gehen sie ins Bett?", "audio/fragen/WannGehenSieInsBett.ogg",
                "Um 21 Uhr", "audio/antworten/Um21Uhr.ogg"));

        el.add(new Entry(
                "Fernsehen", "audio/stichworte/fernsehen.ogg",
                "Sehen sie Abends gerne Fern?", "audio/fragen/SehenSieAbendsGerneFern.ogg",
                "Ja, sehr gerne", "audio/antworten/JaSehrGerne.ogg"));

        el.add(new Entry(
                "Frühstück", "audio/stichworte/fruestueck.ogg",
                "Wann Frühstücken sie?", "audio/fragen/WannFruehstueckenSie.ogg",
                "Um 7 Uhr", "audio/antworten/Um7Uhr.ogg"));

        el.add(new Entry(
                "Mittagessen", "audio/stichworte/mittagessen.ogg",
                "Was essen sie zu Mittag?", "audio/fragen/WasEssenSieZuMittag.ogg",
                "Ich esse Spaghetti zu Mittag", "audio/antworten/IchEsseSpaghettiZuMittag.ogg"));

        el.add(new Entry(
                "Mittagspause", "audio/stichworte/mittagspause.ogg",
                "Wann haben Sie Mittagspause?", "audio/fragen/WannHabenSieMittagspause.ogg",
                "Immer um 12 Uhr", "audio/antworten/ImmerUm12Uhr.ogg"));

        el.add(new Entry(
                "Sprachkurs", "audio/stichworte/sprachkurs.ogg",
                "Wann beginnt Ihr Sprachkurs?", "audio/fragen/WannBegintIhrSprachkurs.ogg",
                "Nächsten Monat", "audio/antworten/NaechstenMonat.ogg",
                "Um 9 Uhr", "audio/antworten/Um9Uhr.ogg"));
    }

    @SuppressWarnings("SpellCheckingInspection")
    private void initUrlaub() {
        Thema thema = new Thema("Urlaub", "audio/themen/Urlaub.ogg");
        themaList.add(thema);
        ArrayList<Entry> el = thema.getEntryList();

        el.add(new Entry(
                "Ausland", "audio/stichworte/ausland.ogg",
                "Machen Sie gerne Urlaub im Ausland?", "audio/fragen/MachenSieGerneUrlaubImAusland.ogg",
                "Ja, sehr gerne", "audio/antworten/JaSehrGerne.ogg"));

        el.add(new Entry(
                "Auto", "audio/stichworte/auto.ogg",
                "Fahren Sie mit dem Auto in den Urlaub?", "audio/fragen/FahrenSieMitDemAutoInDenUrlaub.ogg",
                "Ja, natürlich", "audio/antworten/JaNatuerlich.ogg",
                "Nein, wir fliegen mit dem Flugzeug", "audio/antworten/NeinWirFliegenMitDemFlugzeug.ogg",
                "Nein, wir fahren mit dem Zug", "audio/antworten/NeinWirFahtenMitDemZug.ogg"));

        el.add(new Entry(
                "Bus", "audio/stichworte/bus.ogg",
                "Fahren Sie mit dem Bus in den Urlaub?", "audio/fragen/FahrenSieMitDemBusInDenUrlaub.ogg",
                "Nein ich fahre mit dem Auto", "audio/antworten/NeinIchFahreMitDemAuto.ogg",
                "Nein, wir fliegen mit dem Flugzeug", "audio/antworten/NeinWirFliegenMitDemFlugzeug.ogg",
                "Nein, wir fahren mit dem Zug", "audio/antworten/NeinWirFahtenMitDemZug.ogg"));

        el.add(new Entry(
                "Familie", "audio/stichworte/familie.ogg",
                "Wo macht Ihre Familie Urlaub?", "audio/fragen/WoMachtIhreFamilieUrlaub.ogg",
                "In Deutschland", "audio/antworten/InDeutschland.ogg"));

        el.add(new Entry(
                "Freunde", "audio/stichworte/freunde.ogg",
                "Fahren Sie oft mit Freunden in den Urlaub?", "audio/fragen/FahrenSieOftMitFreundenInDenUrlaub.ogg",
                "Nein, nicht so oft", "audio/antworten/NeinNichtSoOft.ogg"));

        el.add(new Entry(
                "Hotel", "audio/stichworte/hotel.ogg",
                "Wohnen sie in einem Hotel im Urlaub?", "audio/fragen/WohenSieInEinemHotelImUrlaub.ogg",
                "Ja, immer", "audio/antworten/JaImmer.ogg"));

        el.add(new Entry(
                "Meer", "audio/stichworte/meer.ogg",
                "Machen Sie Urlaub am Meer?", "audio/fragen/MachenSieUrlaubAmMeer.ogg",
                "Ja Immer", "audio/antworten/JaImmer.ogg",
                "Im August", "audio/antworten/ImAugust.ogg"));

        el.add(new Entry(
                "Reisebüro", "audio/stichworte/reisebuero.ogg",
                "Buchen Sie Ihren Urlaub im Reisebüro?", "audio/fragen/BuchenSieIhrenUrlaubImReisebuero.ogg",
                "Ja, natürlich", "audio/antworten/JaNatuerlich.ogg",
                "Nein, im Internet", "audio/antworten/NeinImInternet.ogg"));

        el.add(new Entry(
                "schwimmen", "audio/stichworte/schwimmen.ogg",
                "Schwimmen sie viel in ihrem Urlaub?", "audio/fragen/SchwimmenSieVielInIhremUrlaub.ogg",
                "Ja, natürlich", "audio/antworten/JaNatuerlich.ogg"));

        el.add(new Entry(
                "Strand", "audio/stichworte/strand.ogg",
                "Machen Sie gerne Urlaub am Strand?", "audio/fragen/MachenSieGerneUrlaubAmStrand.ogg",
                "Ja, sehr gerne", "audio/antworten/JaSehrGerne.ogg"));

        el.add(new Entry(
                "übernachten", "audio/stichworte/uebernachten.ogg",
                "Möchten sie in Bangkok übernachten?", "audio/fragen/MoechtenSieInBangkokUebernachten.ogg",
                "Ja, sehr gerne", "audio/antworten/JaSehrGerne.ogg"));

        el.add(new Entry(
                "Urlaub", "audio/stichworte/urlaub.ogg",
                "Wann machen Sie Urlaub?", "audio/fragen/WannMachenSieUrlaub.ogg",
                "Im August", "audio/antworten/ImAugust.ogg",
                "Im Juni", "audio/antworten/ImJuni.ogg",
                "Im Habe im Juni Urlaub", "audio/antworten/IchHabeImJuniUrlaub.ogg"));


    }

    @SuppressWarnings("SpellCheckingInspection")
    private void initVerkeht() {
        Thema thema = new Thema("Verkehr", "audio/themen/Verkehr.ogg");
        themaList.add(thema);
        ArrayList<Entry> el = thema.getEntryList();

        el.add(new Entry(
                "Arbeit", "audio/stichworte/arbeit.ogg",
                "Wie fahren Sie zur Arbeit?", "audio/fragen/WieFahrenSieZurArbeit.ogg",
                "Ich fahre mit dem Taxi", "audio/antworten/IchFahreMitDemTaxi.ogg",
                "Ich fahre mit dem Bus", "audio/antworten/IchFahreMitDemBus.ogg",
                "Ich fahre mit dem Fahrrad", "audio/antworten/IchFahreMitDemFahrrad.ogg"));

        el.add(new Entry(
                "Fahrkarte", "audio/stichworte/fahrkarte.ogg",
                "Was kostet eine Fahrkarte?", "audio/fragen/WasKostetEineFahrkarte.ogg",
                "30 Bath", "audio/antworten/30Bath.ogg"));

        el.add(new Entry(
                "Fahrkarte", "audio/stichworte/fahrkarte.ogg",
                "Wo kann ich eine Fahrkarte kaufen?", "audio/fragen/WoKannIchEineFahrkarteKaufen.ogg",
                "Am Schalter", "audio/antworten/AmSchalter.ogg",
                "Am Bahnhof", "audio/antworten/AmBahnhof.ogg",
                "Am Automaten", "audio/antworten/AmAutomaten.ogg"));

        el.add(new Entry(
                "Fahrrad", "audio/stichworte/fahrrad.ogg",
                "Fahren Sie gerne mit dem Fahrrad?", "audio/fragen/FahrenSieGerneMitDemFahrrad.ogg",
                "Ja, sehr gerne", "audio/antworten/JaSehrGerne.ogg",
                "Nein, nicht so gern", "audio/antworten/NeinNichtSoGern.ogg"));

        el.add(new Entry(
                "U-Bahn", "audio/stichworte/ubahn.ogg",
                "Fahren Sie oft mit der U-Bahn", "audio/fragen/FahrenSieOftMitDerUBahn.ogg",
                "Nein, nicht so oft", "audio/antworten/NeinNichtSoOft.ogg"));

        el.add(new Entry(
                "Urlaub", "audio/stichworte/urlaub.ogg",
                "Fahren Sie mit dem Auto in den Urlaub?", "audio/fragen/FahrenSieMitDemAutoInDenUrlaub.ogg",
                "Ja", "audio/antworten/Ja.ogg",
                "Nein, wir fliegen mit dem Flugzeug", "audio/antworten/NeinWirFliegenMitDemFlugzeug.ogg",
                "Nein, wir fahren mit dem Zug", "audio/antworten/NeinWirFahtenMitDemZug.ogg"));

        el.add(new Entry(
                "Zug", "audio/stichworte/zug.ogg",
                "Fahren Sie gerne mit dem Zug?", "audio/fragen/FahrenSieGerneMitDemZug.ogg",
                "Ja, sehr gerne", "audio/antworten/JaSehrGerne.ogg",
                "Nein, nicht so gern", "audio/antworten/NeinNichtSoGern.ogg"));

    }


    @SuppressWarnings("SpellCheckingInspection")
    private void initWochenende() {
        Thema thema = new Thema("Wochenende", "audio/themen/Wochenende.ogg");
        themaList.add(thema);
        ArrayList<Entry> el = thema.getEntryList();

        el.add(new Entry(
                "Ausflug", "audio/stichworte/ausflug.ogg",
                "Haben sie am Wochenende einen Ausflug gemacht?", "audio/fragen/HabenSieAmWochenendeEinenAusflugGemacht.ogg",
                "Ja, ich war in Pattaya", "audio/antworten/JaIchWarInPatthaya.ogg"));

        el.add(new Entry(
                "Bücher", "audio/stichworte/buecher.ogg",
                "Lesen sie Bücher am Wochenende?", "audio/fragen/LesenSieBuecherAmWochenende.ogg",
                "Ja, sehr gerne", "audio/antworten/JaSehrGerne.ogg"));

        el.add(new Entry(
                "Familie", "audio/stichworte/familie.ogg",
                "Was machen Sie am Wochenende mit ihrer Familie", "audio/fragen/WasMachenSieAmWochenendeMitIhrerFamilie.ogg",
                "Wir gehen Fahrradfahren", "audio/antworten/WirGehenFahrradfahren.ogg",
                "Wir gehen schwimmen", "audio/antworten/WirGehenSchwimmen.ogg"));

        el.add(new Entry(
                "Frühstück", "audio/stichworte/fruestueck.ogg",
                "Was essen Sie am Wochenende zum Frühstück?", "audio/fragen/WasEssenSieAmWochenendeZumFruehstueck.ogg",
                "Ich esse Brot mit Marmelade", "audio/antworten/IchEsseBrotMitMarmelade.ogg"));

        el.add(new Entry(
                "Sonntag", "audio/stichworte/sonntag.ogg",
                "Was machen Sie am Sonntag?", "audio/fragen/WasMachenSieAmSonntag.ogg",
                "Ich gehe Schwimmen", "audio/antworten/IchGeheSchwimmen.ogg",
                "Ich gehe Fahrradfahren", "audio/antworten/IchGeheFahrradfahren.ogg",
                "Wir gehen ins Kino", "audio/antworten/WirGehenInsKino.ogg"));

        el.add(new Entry(
                "Sport", "audio/stichworte/sport.ogg",
                "Machen sie am Wochenende Sport?", "audio/fragen/MachenSieAmWochenendeSport.ogg",
                "Ja, ich gehe Fahrradfahren", "audio/antworten/JaIchFahreFahrrad.ogg",
                "Ja, Ich gehe schwimmen", "audio/antworten/JaIchGeheSchwimmen.ogg",
                "Nein ich bin zu faul", "audio/antworten/NeinIchBinZuFaul.ogg"));
    }


    @SuppressWarnings("SpellCheckingInspection")
    private void initWohnen() {
        Thema thema = new Thema("Wohnen", "audio/themen/Wohnen.ogg");
        themaList.add(thema);
        ArrayList<Entry> el = thema.getEntryList();

        el.add(new Entry(
                "Balkon", "audio/stichworte/balkon.ogg",
                "Heben sie einen Balkon zu Hause", "audio/fragen/HabenSieEinenBalkonZuHause.ogg",
                "Ja", "audio/antworten/Ja.ogg",
                "Nein", "audio/antworten/Nein.ogg"));

        el.add(new Entry(
                "Bilder", "audio/stichworte/bilder.ogg",
                "Haben sie Bilder zu Hause?", "audio/fragen/HabenSieBilderZuHause.ogg",
                "Ja, ich habe viele Bilder", "audio/antworten/JaIchHabeVieleBilder.ogg",
                "Nein ich habe keine Bilder", "audio/antworten/NeinIchHabeKeineBilder.ogg"));

        el.add(new Entry(
                "Fernseher", "audio/stichworte/fernsehen.ogg",
                "Haben sie einen Fernseher in ihrer Wohnung?", "audio/fragen/HabenSieEinenFernseherInIhrerWohnng.ogg",
                "Ja, wir haben zwei Fernseher", "audio/antworten/JaWirHabenZweiFernseher.ogg",
                "Nein, das brauchen wir nicht", "audio/antworten/NeinDasBrauchenWirNicht.ogg"));

        el.add(new Entry(
                "Garten", "audio/stichworte/garten.ogg",
                "Haben sie einen Garten", "audio/fragen/HabenSieEinenGarten.ogg/",
                "Nein, ich hab keinen Garten", "audio/antworten/NeinIchHabeKeinenGarten.ogg",
                "Ich habe einen Graten", "audio/antworten/IchHabeEinenGarten.ogg"));

        el.add(new Entry(
                "Haus", "audio/stichworte/haus.ogg",
                "Ist Ihr Haus groß?", "audio/fragen/IstIhrHausGross.ogg",
                "Ja, es ist groß", "audio/antworten/JaEsIstGross.ogg"));

        el.add(new Entry(
                "Küche", "audio/stichworte/kueche.ogg",
                "Haben sie eine große Küche in ihrer Wohnung?", "audio/fragen/HabenSieEineGrosseKuecheInIhrerWohnung.ogg",
                "Ja, die Küche ist groß", "audio/antworten/JaDieKuecheIstGross.ogg",
                "Nein die Küche ist klein", "audio/antworten/NeinDieKuecheIstKlein.ogg"));

        el.add(new Entry(
                "Miete", "audio/stichworte/miete.ogg",
                "Wie viel Miete zahlen Sie?", "audio/fragen/WievielMieteZahlenSie.ogg",
                "Ich muss 5000 Baht im Monat bezahlen", "audio/antworten/IchMuss5000BathMieteImMonatBezahlen.ogg"));

        el.add(new Entry(
                "Schrank", "audio/stichworte/schrank.ogg",
                "Haben Sie viele Schränke zu Hause?", "audio/fragen/HabenSieVieleSchraenkeZuHause.ogg",
                "Nein, nicht so viele", "audio/antworten/NeinNichtSoViele.ogg",
                "Ja sehr viele", "audio/antworten/JaSehrViele.ogg"));

        el.add(new Entry(
                "Straße", "audio/stichworte/strasse.ogg",
                "In welcher Straße wohnen sie?", "audio/fragen/InWelcherStrasseWohnenSie.ogg",
                "In der Thepprasitstraße", "audio/antworten/InDerThrepasitStrasse.ogg"));

        el.add(new Entry(
                "Wohnen", "audio/stichworte/wohnen.ogg",
                "Wohnen sie in der Thepprasitstraße?", "audio/fragen/WohnenSieInDerThrepasitStrasse.ogg",
                "Ja", "audio/antworten/Ja.ogg",
                "Nein", "audio/antworten/Nein.ogg"));

        el.add(new Entry(
                "Wohnung", "audio/stichworte/wohnung.ogg",
                "Möchten sie in Bangkok eine Wohnung suchen?", "audio/fragen/MoechtenSieInBangkokEineWohnungSuchen.ogg",
                "Ja", "audio/antworten/Ja.ogg",
                "Nein", "audio/antworten/Nein.ogg"));

        el.add(new Entry(
                "Wohnung", "audio/stichworte/wohnung.ogg",
                "Haben Sie eine Wohnung?", "audio/fragen/HabenSieEineWohnung.ogg",
                "Nein, wir Wohnen in einem Haus", "audio/antworten/NeinWirWohnenInEinemHaus.ogg"));

        el.add(new Entry(
                "Zimmer", "audio/stichworte/zimmer.ogg",
                "Wie viele Zimmer hat ihre Wohnung?", "audio/fragen/WievieleZimmerHatIhreWohnung.ogg",
                "Sie hat drei Zimmer", "audio/antworten/SieHatDreiZimmer.ogg"));


    }

}
