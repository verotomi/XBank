package tamas.verovszki.xbank;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class InformationActivity extends AppCompatActivity {

    TextView Text_View_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        init();

        // Toolbar + visszanyíl - többek között be kell állítani hozzá a manifestben a szülő Activityt + kell egy Toolbar view az XML-be.
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_4);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Text_View_1.setMovementMethod(new ScrollingMovementMethod());

        Text_View_1.setText(
                "\n" +
                "X Bank - netbank applikáció\n" +
                "Verzió: 0.1.9\n" +
                "© 2018 - Verovszki Tamás\n" +
                "\n" +
                "1. Rövid leírás:\n Fiktív banki alkalmazás lakossági folyószámla kezeléséhez, Android rendszerű telefonokra\n" +
                "\n" +
                "2. Témaválasztás indoklása:\n A bankok sokféle szolgáltatással és kedvezményekkel csábítják magukhoz az ügyfeleket, azonban ami az egyik banknál megtalálható, az nem biztos, hogy a másiknál is. Léteznek testre szabható lakossági banki szolgáltatások is, de azok is meglehetősen korlátozottak. Ezek miatt életem során számtalan banknál volt folyószámlám, sokszor kettő vagy három banknál is egyszerre. Igyekeztem kihasználni egyszerre több bank számomra előnyös szolgáltatását. Mivel manapság szinte mindegyik bank nyújt mobiltelefonos felületet is a számlatulajdonosok számára, így sikerült sokféle netbank applikációval megismerkednem. Aktív bankszámlahasználatom hamar oda vezetett, hogy egy átlag felhasználónál jóval mélyebben megismertem az egyes bankok androidos felületű szolgáltatásait. Felfedeztem hiányosságokat és hibákat, valamint születtek olyan ötleteim, melyek könnyebbé tehetnék a bankszámla tulajdonosok, a bank, vagy akár mindkét oldal életét. Azonban hiába jeleztem ezeket a bankok felé, azok óriási mérete és összetettsége miatt az ilyen „apróságok” az esetek döntő többségében nem kerülnek kijavításra vagy megvalósításra. Fentiek alapján jött az ötlet, hogy létrehozzak egy olyan alkalmazást, ami az alapvető és általános szolgáltatások mellett olyan funkciókkal is rendelkezik, melyek nem, vagy csak elvétve találhatóak meg más bankoknál. Más szavakkal élve egy olyan netbank szolgáltatást szerettem volna alkotni, amely a bankok szolgáltatásiból összegyűjti a hasznos szolgáltatásokat és emellett kiküszöböli az esetleges hiányosságokat vagy hibákat. Analitikus gondolkodásmódom és az új dolgok iránti kíváncsiságom sokat segít abban, hogy mind szolgáltatói, mind pedig felhasználói szemmel is alaposan átnézek, átlássak egy rendszert. Mivel gyerekkorom óta vonzódom a matematikához és a számokhoz, így kézenfekvő volt, hogy olyan témát válasszak a szakdolgozatomnak, amiben a számok kiemelt szerepet játszanak. És hol máshol található ez meg, ha nem a bankoknál, ahol csakis akkor kerek valami, ha az nemcsak forintra, de fillérre is stimmel?\n");
    }

    public void init(){
        Text_View_1 = findViewById(R.id.Text_View_1);
    }
}
