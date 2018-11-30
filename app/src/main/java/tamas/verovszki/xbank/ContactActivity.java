package tamas.verovszki.xbank;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ContactActivity extends AppCompatActivity {

    ImageButton ImageButtonEmail, ImageButtonWeb, ImageButtonPhone, ImageButtonFb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        init();

        // Toolbar + visszanyíl - többek között be kell állítani hozzá a manifestben a szülő Activityt + kell egy Toolbar view az XML-be.
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_5);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        ImageButtonFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(ContactActivity.this, "Open facebook app", Toast.LENGTH_SHORT).show();
                Intent i = newFacebookIntent(getApplicationContext().getPackageManager(),"https://www.facebook.com/groups/343674876060509/");
                //Intent i = newFacebookIntent(getApplicationContext().getPackageManager(),getString(R.string.facebook_address));
                startActivity(i);

            }
        });
        
        ImageButtonPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(ContactActivity.this, "Open phone app", Toast.LENGTH_SHORT).show();
                // Intent i = new Intent(Intent.ACTION_CALL); // - ehhez kell engedély is a manifestbe!
                Intent i = new Intent(Intent.ACTION_DIAL); // - ehhez NEM kell az engedély a manifestbe! Ha ezt fogom használni, a lenti ellenőrzés sem kell!
                String numberToCall = getString(R.string.phone_number);
                i.setData(Uri.parse("tel:" + numberToCall));
                if (ActivityCompat.checkSelfPermission(ContactActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(ContactActivity.this, R.string.text_phone_permission, Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(ContactActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                }
                startActivity(i);

            }
        });

        ImageButtonWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(ContactActivity.this, "Open the web browser", Toast.LENGTH_SHORT).show();
                //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.homepage_address)));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.digitalemotion.space"));
                startActivity(browserIntent);
            }
        });
        
        ImageButtonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Nem működik jól, nem mennek át a paraméterek!
                // Toast.makeText(ContactActivity.this, "Open the email app", Toast.LENGTH_SHORT).show();
                //Intent i = new Intent(Intent.ACTION_SENDTO);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setData(Uri.parse("email"));
                String emailAddress = getString(R.string.email_address);
                i.putExtra(Intent.EXTRA_EMAIL, emailAddress);
                Toast.makeText(ContactActivity.this, emailAddress, Toast.LENGTH_SHORT).show();
                i.putExtra(Intent.EXTRA_SUBJECT, R.string.email_subject);
                // i.putExtra(Intent.EXTRA_SUBJECT, "Email tárgya"); -- ez így átmegy!!
                // i.setType("message/rfc822");
                i.setType("text/plain");
                Intent chooser = Intent.createChooser(i, "Launch Email");
                startActivity(chooser);*/

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + getString(R.string.email_address)));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
                // emailIntent.putExtra(Intent.EXTRA_TEXT, "Email body");
                //emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body); //If you are using HTML in your body text
                startActivity(Intent.createChooser(emailIntent, getString(R.string.email_app_chooser_text)));
            }
        });
        
        
    }

    public void init(){
        ImageButtonEmail = findViewById(R.id.imagebutton_email);
        ImageButtonWeb = findViewById(R.id.imagebutton_web);
        ImageButtonPhone = findViewById(R.id.imagebutton_phone);
        ImageButtonFb = findViewById(R.id.imagebutton_facebook);
    }

    public static Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                // http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }
}