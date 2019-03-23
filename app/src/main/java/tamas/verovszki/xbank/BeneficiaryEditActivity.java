package tamas.verovszki.xbank;

    import android.content.Intent;
    import android.content.pm.ActivityInfo;
    import android.content.res.ColorStateList;
    import android.os.Bundle;
    import android.os.Handler;
    import android.support.v7.widget.Toolbar;
    import android.text.Editable;
    import android.text.TextWatcher;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.Toast;
    import android.content.Context;
    import android.content.SharedPreferences;
    import android.support.design.widget.NavigationView;
    import android.support.v4.view.GravityCompat;
    import android.support.v4.widget.DrawerLayout;
    import android.support.v7.app.ActionBar;

    import org.json.JSONException;
    import org.json.JSONObject;


    import java.util.HashMap;


public class BeneficiaryEditActivity extends BaseActivity implements PerformNetworkRequest.TaskDelegate{

    private Toolbar toolbar;
    MenuItem menuitem_logout_3;
    MenuItem menuitem_toolbar_name;
    EditText EditTextName;
    EditText EditTextPartnerName;
    EditText EditTextPartnerAccountNumber;
    Button ButtonSubmit;

    boolean loginState = false;
    Boolean valasz = false;
    String userFirstName = "none";
    int userId;

    //MySQL adatbázishoz

    PerformNetworkRequest request;

    boolean _ignore = false; // indicates if the change was made by the TextWatcher itself.
    private String _before; // Unchanged sequence which is placed before the updated sequence.
    private String _old; /// updated sequence before the update.
    private String _new; // Updated sequence after the update.
    private String _after; // Unchanged sequence which is placed after the updated sequence.

    DataModelBeneficiaries beneficiary;

    DrawerLayout drawerLayout;
    String userLastLoginTime;
    String userLastName;
    boolean ignore = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // képernyő-forgatás tiltása
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.TitleEditBeneficiary)); // Fejléc átállítása a kiválasztott nyelver. Ez még a setContentView() elé kell
        setContentView(R.layout.activity_beneficiary_edit);

        getBundle(); // átadott változók beolvasása
        init();
        navDrawer();

        EditTextName.setText(beneficiary.getName());
        EditTextPartnerName.setText(beneficiary.getPartner_name());
        EditTextPartnerAccountNumber.setText(beneficiary.getPartner_account_number());

        setSupportActionBar(toolbar); // Toolbar megjelenítése

        ButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean iserror = false;
                if (EditTextName.getText().toString().equals("")) {
                    Toast.makeText(BeneficiaryEditActivity.this, getString(R.string.template_name_needed), Toast.LENGTH_SHORT).show();
                    iserror = true;
                }
                else if (EditTextPartnerName.getText().toString().equals("")) {
                    Toast.makeText(BeneficiaryEditActivity.this, getString(R.string.beneficiary_name_needed), Toast.LENGTH_SHORT).show();
                    iserror = true;
                }
                else if (!((EditTextPartnerAccountNumber.getText().toString().length() == 17) || (EditTextPartnerAccountNumber.getText().length() == 26))){
                    //if (true){
                    Toast.makeText(BeneficiaryEditActivity.this, getString(R.string.rule_account_number), Toast.LENGTH_SHORT).show();
                    iserror = true;
                }
                if (iserror) {
                } else {
                    updateBeneficiary(userId);

                    Intent intent = new Intent(BeneficiaryEditActivity.this, DoneAnimation.class);
                    startActivity(intent);

                    finish();
                }
            }
        });

        EditTextPartnerAccountNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                _before = charSequence.subSequence(0,start).toString();
                _old = charSequence.subSequence(start, start+count).toString();
                _after = charSequence.subSequence(start+count, charSequence.length()).toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                _new = charSequence.subSequence(start, start+count).toString();

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (_ignore)
                    return;
                _ignore = true; // prevent infinite loop
                // Change your text here.
                // myTextView.setText(myNewText);

                //Ezt beállítottam xml-ben
                /*if (editable.toString().length() >26){
                    //EditTextTargetAccount.setText(_before);
                    EditTextTargetAccount.setText(editable.toString().substring(0,26));
                }*/

                //Ez előre hozzáadja a kötőjelet Egyszerre csak az egyik lehet aktív!
                if (editable.toString().length() == 8){
                    if (_before.length() == 7){
                        EditTextPartnerAccountNumber.setText(editable.toString()+ "-");
                    }
                    else if  (_before.length() > editable.length()) {
                        EditTextPartnerAccountNumber.setText(editable.toString().substring(0,8));
                    }
                }

                // ez utólag adja hozzá. Egyszerre csak az egyik lehet aktív!
                /*if (editable.toString().length() == 9) {
                    if (_before.length() == 8) {
                        //Toast.makeText(TransferMoneyOneTimeActivity.this, "1: " + EditTextTargetAccount.getText().toString().substring(0, 8) + "2:" + EditTextTargetAccount.getText().toString().substring(8, 9), Toast.LENGTH_SHORT).show();
                        EditTextTargetAccount.setText(editable.toString().substring(0, 8) + "-" + editable.toString().substring(8, 9));
                    } else if (_before.length() > editable.length()) {
                        EditTextTargetAccount.setText(editable.toString().substring(0, 8));
                    }
                }*/

                // ez utólag adja hozzá. VEgyszerre csak az egyik lehet aktív!
                if (editable.toString().length() == 18){
                    if (_before.length() == 17){
                        EditTextPartnerAccountNumber.setText(editable.toString() + "-");
                        EditTextPartnerAccountNumber.setText(editable.toString().substring(0, 17) + "-" + editable.toString().substring(17, 18));
                    }
                    else if  (_before.length() > editable.length()) {
                        EditTextPartnerAccountNumber.setText(editable.toString().substring(0,17));
                    }
                }

                //Ez előre hozzáadja a kötőjelet Egyszerre csak az egyik lehet aktív!
                /*if (editable.toString().length() == 17){
                    if (_before.length() == 16){
                        EditTextTargetAccount.setText(editable.toString() + "-");
                    }
                    else if  (_before.length() > editable.length()) {
                        EditTextTargetAccount.setText(editable.toString().substring(0,17));
                    }
                }*/

                _ignore = false; // release, so the TextWatcher start to listen again.
                EditTextPartnerAccountNumber.setSelection(EditTextPartnerAccountNumber.getText().length());  // kurzor pozicíonálása a legvégére
            }
        });

        EditTextPartnerAccountNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasfocus) {
                if (!EditTextPartnerAccountNumber.getText().toString().equals("")){
                    if (EditTextPartnerAccountNumber.getText().toString().substring(EditTextPartnerAccountNumber.length()-1, EditTextPartnerAccountNumber.length()).equals("-")){
                        EditTextPartnerAccountNumber.setText(EditTextPartnerAccountNumber.getText().toString().substring(0, EditTextPartnerAccountNumber.length()-1 ));
                    }
                }

            }
        });

    }

    /**
     * @param result
     */
    @Override
    public void taskCompletionResult(String result) {
        String returnedMessage = "";
        try {
            JSONObject object2 = new JSONObject(result);
            returnedMessage = object2.getString(Constants.RESPONSE_MESSAGE);
        } catch (JSONException e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }
        switch (returnedMessage) {
            case Constants.RESPONSE_MESSAGE_BENECICIARY_UPDATE_SUCCESSFUL:
                Toast.makeText(this, getString(R.string.record_successful), Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    private void updateBeneficiary(int user_id) {
        if (Utility.IsNetworkAvailable(this)){
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.COL_BENEFICIARIES_ID, String.valueOf(beneficiary.getId()));
            params.put(Constants.COL_BENEFICIARIES_ID_USER, String.valueOf(user_id));
            params.put(Constants.COL_BENEFICIARIES_NAME, EditTextName.getText().toString());
            params.put(Constants.COL_BENEFICIARIES_PARTNER_NAME, EditTextPartnerName.getText().toString());
            params.put(Constants.COL_BENEFICIARIES_PARTNER_ACCOUNT_NUMBER, EditTextPartnerAccountNumber.getText().toString());
            request = new PerformNetworkRequest(Constants.URL_UPDATE_BENEFICIARY, params, Constants.CODE_POST_REQUEST, this);
            request.execute();
            //Toast.makeText(this, request.getResult(), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mobilbankmainactivity_without_name, menu); // elemek hozzáadása a toolbarhoz
        menuitem_logout_3 = menu.findItem(R.id.logout_toolbar_icon_3);
        //menuitem_toolbar_name = menu.findItem(R.id.logout_toolbar_name);
        loginState = Utility.getLoginState(this);

        if (loginState) {
            menuitem_logout_3.setVisible(true);
            menuitem_logout_3.setEnabled(true);
            //menuitem_toolbar_name.setTitle(userFirstName);
            //menuitem_toolbar_name.setVisible(true);
        } else {
            menuitem_logout_3.setEnabled(false);
            menuitem_logout_3.setVisible(false);
            //menuitem_toolbar_name.setTitle("");
            //menuitem_toolbar_name.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // itt kell kezelni a toolbar-gombok kattintásait. A visszanyíl kattintást a rendszer kezeli!
        int id = item.getItemId();
        if (id == R.id.logout_toolbar_icon_3) {
            CustomDialogClass cdd = new CustomDialogClass(this);
            cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                @Override
                public void onYes(String title) {
                    menuitem_logout_3.setEnabled(false);
                    menuitem_logout_3.setVisible(false);
                    //menuitem_toolbar_name.setTitle("");
                    //menuitem_toolbar_name.setVisible(false);
                    loginState = false;
                    Utility.saveLoginState(BeneficiaryEditActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                    MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                    Intent intent = new Intent(BeneficiaryEditActivity.this, LogoutAnimation.class);
                    startActivity(intent);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                            BeneficiaryEditActivity.this.finish();
                            //finish();
                        }
                    }, 3000);
                }

                @Override
                public void onNo(String title) {

                }
            });
            cdd.show();
            return true;
        }
        if (id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void init() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        EditTextName = findViewById(R.id.EditTextName);
        EditTextPartnerName = findViewById(R.id.EditTextPartnerName);
        EditTextPartnerAccountNumber = findViewById(R.id.EditTextPartnerAccountNumber);
        ButtonSubmit = findViewById(R.id.ButtonSubmit);
        drawerLayout = findViewById(R.id.drawer_layout);

    }

    public void getBundle(){
        try{
        /*Intent i = getIntent();
        userFirstName = i.getStringExtra("user_firstname");
        userId = Integer.parseInt(i.getStringExtra("user_id"));*/

            Bundle b = getIntent().getExtras();
            beneficiary = (DataModelBeneficiaries) b.getSerializable(Constants.SHAREDPREFERENCES_BENEFICIARY); // cast-olni kellett!! (Alt + Enter)
            userId = Integer.parseInt(b.getString(Constants.SHAREDPREFERENCES_USER_ID));
            userFirstName = b.getString(Constants.SHAREDPREFERENCES_USER_FIRSTNAME);
            userLastName = b.getString(Constants.SHAREDPREFERENCES_USER_LASTNAME);
            userLastLoginTime = b.getString(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME);
        }
        catch (Exception e){ // ez a blokk azért került ide, mert nem akart működni a bundle-s paraméter átadás. Már működik, így ez a blokk csak biztonsági szempontból maradt itt.
            SharedPreferences sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
            userId = Integer.parseInt(sp.getString(Constants.SHAREDPREFERENCES_USER_ID, ""));
            userFirstName = sp.getString(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, "");
        }
    }

    public void navDrawer(){
        final NavigationView navigationView = findViewById(R.id.nav_view);
        //View hView =  navigationView.getHeaderView(0);
        View hView =  findViewById(R.id.Navigation_Header_Layout);

        TextView nav_name = (TextView)hView.findViewById(R.id.TextViewName);
        TextView nav_lastlogin = (TextView)hView.findViewById(R.id.TextViewLastLogin);
        nav_name.setText(userLastName + " " + userFirstName);
        nav_lastlogin.setText(userLastLoginTime);

        Menu navMenu = navigationView.getMenu();
        MenuItem navMenuAccountHistory = navMenu.findItem(R.id.Beneficiaries);
        navMenuAccountHistory.setEnabled(false);

        ColorStateList cs1 = Utility.navigationDrawerColors();
        navigationView.setItemTextColor(cs1);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        // menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        Intent intent;
                        int id = menuItem.getItemId();
                        switch (id){
                            case R.id.Overviewe:
                                intent = new Intent(BeneficiaryEditActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;
                                finish();
                                break;
                            case R.id.Balance_query:
                                intent = new Intent(BeneficiaryEditActivity.this, BankAccountBalancesActivity.class);
                                break;
                            case R.id.One_time_transfer:
                                intent = new Intent(BeneficiaryEditActivity.this, TransferMoneyOneTimeActivity.class);
                                break;
                            case R.id.Recurring_transfers:
                                intent = new Intent(BeneficiaryEditActivity.this, RecurringTransfersActivity.class);
                                break;
                            case R.id.Savings:
                                intent = new Intent(BeneficiaryEditActivity.this, SavingsActivity.class);
                                break;
                            case R.id.Account_history:
                                intent = new Intent(BeneficiaryEditActivity.this, BankAccountHistoryActivity.class);
                                break;
                            case R.id.Account_statement_download:
                                intent = new Intent(BeneficiaryEditActivity.this, BankAccountStatementsActivity.class);
                                break;
                            case R.id.Beneficiaries:
                                intent = new Intent(BeneficiaryEditActivity.this, BeneficiariesActivity.class);
                                break;
                            case R.id.Manage_credit_cards:
                                intent = new Intent(BeneficiaryEditActivity.this, CreditCardsActivity.class);
                                break;
                            case R.id.Change_PIN_code:
                                intent = new Intent(BeneficiaryEditActivity.this, PinCodeChangeActivity.class);
                                break;
                            case R.id.Logout:
                                //intent=null;

                                // az előzőleg kiválasztott elem úgy maradt, ezért törölni kell a kijelölést
                                // a -2 oka: navmenu utolsó eleme egy ures sor, az azelőtti meg a kilépés. Ezeknek nem kell törölni a kijelölését.
                                int size = navigationView.getMenu().size();
                                //for (int i = 0; i < size-2; i++) {
                                for (int i = 0; i < size; i++) {
                                    navigationView.getMenu().getItem(i).setChecked(false);
                                }

                                intent = new Intent(BeneficiaryEditActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;

                                CustomDialogClass cdd = new CustomDialogClass(BeneficiaryEditActivity.this);
                                cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                                    @Override
                                    public void onYes(String title) {
                                        menuitem_logout_3.setEnabled(false);
                                        menuitem_logout_3.setVisible(false);
                                        //menuitem_toolbar_name.setTitle("");
                                        //menuitem_toolbar_name.setVisible(false);
                                        loginState = false;
                                        Utility.saveLoginState(BeneficiaryEditActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                                        MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                                        Intent intent = new Intent(BeneficiaryEditActivity.this, LogoutAnimation.class);
                                        startActivity(intent);
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                                                BeneficiaryEditActivity.this.finish();
                                                //finish();
                                            }
                                        }, 3000);
                                    }

                                    @Override
                                    public void onNo(String title) {

                                    }
                                });
                                cdd.show();
                                break;

                            default: // van egy üres sor a navigation drawer legalján, amiatt, hogy oda is kerüljön elválasztóvonal. Ennek az üres sornak a lekezelése miatt kell ide az ignore, hogy ha arra kattintunk, ne történjen semmi
                                ignore = true;
                                intent = new Intent(BeneficiaryEditActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                break;
                        }
                        if (!ignore){
                            intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
                            intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                            intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                            intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között


                            finish();
                        }
                        ignore = false;
                        return true;
                    }
                });
        setSupportActionBar(toolbar); // Toolbar megjelenítése
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }
}
