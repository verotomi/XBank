package tamas.verovszki.xbank;

/**
 * URL-re figyelni!
 */
public class Constants {

    // ------------------------------------------------------- MYSQL -------------------------------
    public static final int CODE_GET_REQUEST = 1024;
    public static final int CODE_POST_REQUEST = 1025;

    private static final String ROOT_URL = "http://xbank.hu/API/Api.php?apicall="; // Távoli eléréshez - működik AndroidStudio emulátoron is, GenyMotion-on is, S8-on is.
    //private static final String ROOT_URL = "http://digitalemotion.space/PHPAPI/v2/Api.php?apicall="; // Távoli eléréshez - működik AndroidStudio emulátoron is, GenyMotion-on is, S8-on is.
    //private static final String ROOT_URL = "http://10.0.2.2/xbank/PHPAPI/v2/Api.php?apicall="; // Android Studio Emulatorhoz
    //private static final String ROOT_URL = "http://10.0.3.2/xbank/PHPAPI/v1/Api.php?apicall="; // GenyMotion-hoz
    //private static final String ROOT_URL = "http://192.168.0.11/xbank/PHPAPI/v1/Api.php?apicall="; // S8-hoz etherneten keresztül - NEM MŰKÖDIK
    //private static final String ROOT_URL = "http://192.168.0.19/xbank/PHPAPI/v1/Api.php?apicall="; // S8-hoz WiFin keresztül - NEM MŰKÖDIK

    public static final String URL_FOR_DOWNLOAD = "http://xbank.hu/Statements/"; // Távoli eléréshez - működik AndroidStudio emulátoron is, GenyMotion-on is, S8-on is.

    public static final String URL_CREATE_CURRENCY = ROOT_URL + "createcurrency";
    public static final String URL_READ_CURRENCIES = ROOT_URL + "getcurrencies";
    public static final String URL_READ_FOREIGNCURRENCIES = ROOT_URL + "getforeigncurrencies";
    public static final String URL_READ_BOTH_CURRENCIES = ROOT_URL + "getbothcurrencies";
    public static final String URL_UPDATE_CURRENCY = ROOT_URL + "updatecurrency";
    public static final String URL_DELETE_CURRENCY = ROOT_URL + "deletecurrency&id=";
    public static final String URL_TRY_TO_LOGIN = ROOT_URL + "trytologin";
    public static final String URL_TRY_TO_LOGIN2 = ROOT_URL + "trytologin2"; // teszteléshez kellett!
    public static final String URL_UPDATE_LASTLOGINTIME = ROOT_URL + "updatelastlogintime";
    public static final String URL_READ_ACCOUNT_BALANCES = ROOT_URL + "getaccountbalances";
    public static final String URL_READ_CREDIT_CARDS = ROOT_URL + "getcreditcards";
    public static final String URL_UPDATE_CREDIT_CARDS = ROOT_URL + "updatecreditcards";
    public static final String URL_UPDATE_CHANGE_PINCODE = ROOT_URL + "changepincode";
    public static final String URL_INSERT_TRANSFER_ONE_TIME = ROOT_URL + "transferonetime";
    public static final String URL_READ_RECURRING_TRANSFERS = ROOT_URL + "getrecurringtransfers";
    public static final String URL_INSERT_RECURRING_TRANSFER = ROOT_URL + "transferrecurring";
    public static final String URL_DELETE_RECURRING_TRANSFER = ROOT_URL + "deleterecurringtransfer";
    public static final String URL_UPDATE_RECURRING_TRANSFER = ROOT_URL + "updaterecurringtransfer";
    public static final String URL_READ_BANK_ACCOUNT_HISTORY = ROOT_URL + "getbankaccounthistory";
    public static final String URL_READ_BANK_ACCOUNT_STATEMENTS = ROOT_URL + "getbankaccountstatements";
    public static final String URL_READ_SAVINGS = ROOT_URL + "getsavings";
    public static final String URL_UPDATE_SAVING = ROOT_URL + "breakdeposit";
    public static final String URL_INSERT_SAVING = ROOT_URL + "insertsaving";
    public static final String URL_READ_SAVING_TYPES = ROOT_URL + "getsavingtypes";
    public static final String URL_READ_BENEFICIARIES = ROOT_URL + "getbeneficiaries";
    public static final String URL_DELETE_BENEFICIARY = ROOT_URL + "deletebeneficiary";
    public static final String URL_INSERT_BENEFICIARY= ROOT_URL + "insertbeneficiary";
    public static final String URL_UPDATE_BENEFICIARY= ROOT_URL + "updatebeneficiary";

    // ------------------------------------------------------- MYSQL  + SQLite ---------------------
    // currencies
    public static final String TABLE_NAME_CURRENCIES = "currencies";
    public static final String COL_CURRENCIES_ID = "id";
    public static final String COL_CURRENCIES_NAME = "name";
    public static final String COL_CURRENCIES_BUY = "buy";
    public static final String COL_CURRENCIES_SELL = "sell";
    public static final String COL_CURRENCIES_VALIDFROM = "validfrom";

    // foreign currencies
    public static final String TABLE_NAME_FOREIGN_CURRENCIES = "foreigncurrencies";
    public static final String COL_FOREIGNCURRENCIES_ID = "id";
    public static final String COL_FOREIGNCURRENCIES_NAME = "name";
    public static final String COL_FOREIGNCURRENCIES_BUY = "buy";
    public static final String COL_FOREIGNCURRENCIES_SELL = "sell";
    public static final String COL_FOREIGNCURRENCIES_VALIDFROM = "validfrom";

    // ------------------------------------------------------- MYSQL -------------------------------
    // users
    public static final String TABLE_NAME_USERS = "users";    //tábla neve
    public static final String COL_USERS_ID = "id";
    public static final String COL_USERS_FIRSTNAME = "firstname";
    public static final String COL_USERS_LASTNAME = "lastname";
    public static final String COL_USERS_MOBILEBANK_ID = "mobilebank_id";
    public static final String COL_USERS_PINCODE = "pincode";
    public static final String COL_USERS_CREATED_ON = "created_on";
    public static final String COL_USERS_LAST_LOGIN = "last_login";

    // bank accounts
    public static final String TABLE_NAME_BANK_ACCOUNTS = "bank_accounts";    //tábla neve
    public static final String COL_BANK_ACCOUNTS_ID = "id";
    public static final String COL_BANK_ACCOUNTS_ID_USER = "id_user";
    public static final String COL_BANK_ACCOUNTS_NUMBER = "number";
    public static final String COL_BANK_ACCOUNTS_TYPE = "type";
    public static final String COL_BANK_ACCOUNTS_CURRENCY = "currency";
    public static final String COL_BANK_ACCOUNTS_BALANCE = "balance";
    public static final String COL_BANK_ACCOUNTS_STATUS = "status";
    public static final String COL_BANK_ACCOUNTS_CREATED_ON = "created_on";
    public static final String COL_BANK_ACCOUNTS_UPDATED_ON = "updated_on";
    public static final String COL_BANK_ACCOUNTS_USER_FIRSTNAME = "user_firstname"; // származtatott mező
    public static final String COL_BANK_ACCOUNTS_USER_LASTNAME = "user_lastname"; // származtatott mező



    // credit cards
    public static final String TABLE_NAME_CREDITCARDS = "credit_cards";    //tábla neve
    public static final String COL_CREDIT_CARDS_ID = "id";
    public static final String COL_CREDIT_CARDS_ID_USER = "id_user";
    public static final String COL_CREDIT_CARDS_ID_BANK_ACCOUNT = "id_bank_account";
    public static final String COL_CREDIT_CARDS_NUMBER = "number";
    public static final String COL_CREDIT_CARDS_TYPE = "type";
    public static final String COL_CREDIT_CARDS_NAME_ON_CARD = "name_on_card";
    public static final String COL_CREDIT_CARDS_EXPIRE_DATE = "expire_date";
    public static final String COL_CREDIT_CARDS_CVC = "cvc";
    public static final String COL_CREDIT_CARDS_STATUS = "status";
    public static final String COL_CREDIT_CARDS_LIMIT_ATM = "limit_atm";
    public static final String COL_CREDIT_CARDS_LIMIT_POS = "limit_pos";
    public static final String COL_CREDIT_CARDS_LIMIT_ONLINE = "limit_online";

    // transactions
    public static final String TABLE_NAME_TRANSACTIONS = "transactions";    //tábla neve
    public static final String COL_TRANSACTIONS_ID = "id";
    public static final String COL_TRANSACTIONS_ID_USER = "id_user";
    public static final String COL_TRANSACTIONS_ID_BANK_ACCOUNT_NUMBER = "id_bank_account_number";
    public static final String COL_TRANSACTIONS_TYPE = "type";
    public static final String COL_TRANSACTIONS_DIRECTION = "direction";
    public static final String COL_TRANSACTIONS_REFERENCE_NUMBER = "reference_number";
    public static final String COL_TRANSACTIONS_CURRENCY = "currency";
    public static final String COL_TRANSACTIONS_AMOUNT = "amount";
    public static final String COL_TRANSACTIONS_PARTNER_NAME = "partner_name";
    public static final String COL_TRANSACTIONS_PARTNER_ACCOUNT_NUMBER = "partner_account_number";
    public static final String COL_TRANSACTIONS_COMMENT = "comment";
    public static final String COL_TRANSACTIONS_ARRIVED_ON = "arrived_on";
    public static final String COL_TRANSACTIONS_VALUE_DATE = "value_date";
    public static final String COL_TRANSACTIONS_BANK_ACCOUNT_NUMBER = "bank_account_number"; // származtatott (JOIN)

    // recurring transfers
    public static final String TABLE_NAME_RECURRING_TRANSFERS = "recurring_transfers";    //tábla neve
    public static final String COL_RECURRING_TRANSFERS_ID = "id";
    public static final String COL_RECURRING_TRANSFERS_ID_USER = "id_user";
    public static final String COL_RECURRING_TRANSFERS_ID_BANK_ACCOUNT_NUMBER = "id_bank_account_number";
    public static final String COL_RECURRING_TRANSFERS_NAME = "name";
    public static final String COL_RECURRING_TRANSFERS_TYPE = "type";
    public static final String COL_RECURRING_TRANSFERS_DIRECTION = "direction";
    public static final String COL_RECURRING_TRANSFERS_REFERENCE_NUMBER = "reference_number";
    public static final String COL_RECURRING_TRANSFERS_CURRENCY = "currency";
    public static final String COL_RECURRING_TRANSFERS_AMOUNT = "amount";
    public static final String COL_RECURRING_TRANSFERS_PARTNER_NAME = "partner_name";
    public static final String COL_RECURRING_TRANSFERS_PARTNER_ACCOUNT_NUMBER = "partner_account_number";
    public static final String COL_RECURRING_TRANSFERS_COMMENT = "comment";
    public static final String COL_RECURRING_TRANSFERS_ARRIVED_ON = "arrived_on";
    public static final String COL_RECURRING_TRANSFERS_VALUE_DATE = "value_date";
    public static final String COL_RECURRING_TRANSFERS_STATUS = "status";
    public static final String COL_RECURRING_TRANSFERS_LAST_FULFILLED = "last_fulfilled";
    public static final String COL_RECURRING_TRANSFERS_FREQUENCY = "frequency";
    public static final String COL_RECURRING_TRANSFERS_DAYS = "days";

    // statements
    public static final String TABLE_NAME_ACCOUNT_STATEMENTS = "account_statements";    //tábla neve
    public static final String COL_ACCOUNT_STATEMENTS_ID = "id";
    public static final String COL_ACCOUNT_STATEMENTS_ID_USER = "id_user";
    public static final String COL_ACCOUNT_STATEMENTS_ID_BANK_ACCOUNT = "id_bank_account";
    public static final String COL_ACCOUNT_STATEMENTS_NUMBER = "number";
    public static final String COL_ACCOUNT_STATEMENTS_FILENAME = "filename";

    // savings
    public static final String COL_SAVINGS_ID = "id";
    public static final String COL_SAVINGS_ID_USER = "id_user";
    public static final String COL_SAVINGS_ID_BANK_ACCOUNT = "id_bank_account";
    public static final String COL_SAVINGS_ID_TYPE = "id_type";
    public static final String COL_SAVINGS_AMOUNT = "amount";
    public static final String COL_SAVINGS_EXPIRE_DATE = "expire_date";
    public static final String COL_SAVINGS_STATUS = "status";
    public static final String COL_SAVINGS_REFERENCE_NUMBER = "reference_number";
    public static final String COL_SAVINGS_ARRIVED_ON = "arrived_on";
    public static final String COL_SAVINGS_TYPE = "type"; // Származtatott mező
    public static final String COL_SAVINGS_RATE = "rate"; // Származtatott mező
    public static final String COL_SAVINGS_DURATION = "duration"; // Származtatott mező
    public static final String COL_SAVINGS_CURRENCY = "currency"; // Származtatott mező
    public static final String COL_SAVINGS_BANK_ACCOUNT_NUMBER = "bank_account_number"; // Származtatott mező

    // saving types
    public static final String COL_SAVING_TYPES_ID = "id";
    public static final String COL_SAVING_TYPES_TYPE = "type";
    public static final String COL_SAVING_TYPES_RATE = "rate";
    public static final String COL_SAVING_TYPES_DURATION = "duration";
    public static final String COL_SAVING_TYPES_CURRENCY = "currency";

    // beneficiaries
    public static final String COL_BENEFICIARIES_ID = "id";
    public static final String COL_BENEFICIARIES_ID_USER = "id_user";
    public static final String COL_BENEFICIARIES_NAME = "name";
    public static final String COL_BENEFICIARIES_PARTNER_NAME = "partner_name";
    public static final String COL_BENEFICIARIES_PARTNER_ACCOUNT_NUMBER = "partner_account_number";
    public static final String COL_BENEFICIARIES_STATUS = "status";
    public static final String COL_BENEFICIARIES_CREATED_ON = "created_on";

    // ------------------------------------------------------- SQLite ------------------------------
    // xbank adatbázis
    public static final String DATABASE_NAME = "xbank.db";    //adatbázis file név

    public static final String TABLE_NAME_ATMS = "atms";    //tábla neve

    // branches
    public static final String TABLE_NAME_BRANCHES = "branches";
    public static final String COL_BRANCHES_ID = "id";
    public static final String COL_BRANCHES_ZIP = "zip";
    public static final String COL_BRANCHES_city = "city";
    public static final String COL_BRANCHES_ADDRESS = "address";
    public static final String COL_BRANCHES_LATITUDE = "latitude";
    public static final String COL_BRANCHES_LONGITUDE = "longitude";
    public static final String COL_BRANCHES_OPENTIMESUNDAY = "openingtimesunday";
    public static final String COL_BRANCHES_OPENTIMEMONDAY = "openingtimemonday";
    public static final String COL_BRANCHES_OPENTIMETUESDAY = "openingtimetuesday";
    public static final String COL_BRANCHES_OPENTIMEWEDNESDAY = "openingtimewednesday";
    public static final String COL_BRANCHES_OPENTIMETHURSDAY = "openingtimethursday";
    public static final String COL_BRANCHES_OPENTIMEFRIDAY = "openingtimefriday";
    public static final String COL_BRANCHES_OPENTIMESATURDAY = "openingtimesaturday";



    // ---------------------------------------------------------------------------------------------
    // constants

    public static final String RESPONSE_MESSAGE = "message";
    public static final String RESPONSE_ERROR = "error";
    public static final String RESPONSE_ID = "id";
    public static final String RESPONSE_USERS = "users";
    public static final String RESPONSE_SAVINGS = "savings";
    public static final String RESPONSE_SAVING_TYPES = "savingtypes";
    public static final String RESPONSE_ACCOUNT_BALANCES = "accountbalances";
    public static final String RESPONSE_BENEFICIARIES = "beneficiaries";
    public static final String RESPONSE_CREDIT_CARDS = "creditcards";
    public static final String RESPONSE_RECURRING_TRANSFERS = "recurringtransfers";

    public static final String RESPONSE_MESSAGE_ACCOUNTLIST_SUCCESSFUL = "Accountlist query was succesfull!";
    public static final String RESPONSE_MESSAGE_ACCOUNTLIST_UNSUCCESSFUL = "Accountlist query was not succesfull!";
    public static final String RESPONSE_MESSAGE_BANK_ACCOUNT_HISTORY_SUCCESSFUL = "Bank account history list query was succesfull!";
    public static final String RESPONSE_MESSAGE_BANK_ACCOUNT_HISTORY_UNSUCCESSFUL = "Bank account history list query was not succesfull!";
    public static final String RESPONSE_MESSAGE_BENECICIARIES_SUCCESSFUL = "Beneficiaries list query was succesfull!";
    public static final String RESPONSE_MESSAGE_BENECICIARIES_UNSUCCESSFUL = "Beneficiaries list query was not succesfull!";
    public static final String RESPONSE_MESSAGE_NEW_BENECICIARY_SUCCESSFUL = "New beneficiary was succesfully inserted!";
    public static final String RESPONSE_MESSAGE_BENECICIARY_UPDATE_SUCCESSFUL = "Beneficiary updated successfully";
    public static final String RESPONSE_MESSAGE_RECURRING_TRANSFER_UPDATE_SUCCESSFUL = "Recurring transfer updated successfully";
    public static final String RESPONSE_MESSAGE_RECURRING_TRANSFER_UPDATE_UNSUCCESSFUL = "Recurring transfer was not updated successfully";
    public static final String RESPONSE_MESSAGE_SAVINGS_LIST_SUCCESSFUL = "Savings list query was succesfull!";
    public static final String RESPONSE_MESSAGE_SAVINGS_LIST_UNSUCCESSFUL = "Savings list query was not succesfull!";
    public static final String RESPONSE_MESSAGE_CREDITCARDS_SUCCESSFUL = "Creditcards queried succesfully!";
    public static final String RESPONSE_MESSAGE_CREDITCARDS_UNSUCCESFUL = "Creditcards query was not succesfull!";
    public static final String RESPONSE_MESSAGE_CREDITCARDS_UPDATES_SUCCESSFUL = "Creditcards updated succesfully!";
    public static final String RESPONSE_MESSAGE_RECURRRING_TRANSFER_LIST_SUCCESSFUL = "Recurring transfer list query was succesfull!";
    public static final String RESPONSE_MESSAGE_EARLY_WITHDRAWAL_SUCCESSFUL = "Early withdrawal was successfull!";
    public static final String RESPONSE_MESSAGE_EARLY_WITHDRAWAL_UNSUCCESFUL = "Early withdrawal was not successfull!";
    public static final String RESPONSE_MESSAGE_NEW_SAVING_SUCCESSFUL = "New saving order was succesfull!";
    public static final String RESPONSE_MESSAGE_NEW_SAVING_UNSUCCESSFUL = "New saving order was not succesfull!";
    public static final String RESPONSE_MESSAGE_NOT_ENOUGH_MONEY = "Not enough money!";
    public static final String RESPONSE_MESSAGE_TRANSFER_SUCCESSFUL = "Transfer was succesfull!";
    public static final String RESPONSE_MESSAGE_TRANSFER_UNSUCCESSFUL = "Transfer was not succesfull!";
    public static final String RESPONSE_MESSAGE_SAVING_TYPES_SUCCESSFUL = "Saving types list query was succesfull!";

    public static final String FROM = "from";
    public static final String TO = "to";
    public static final String FILTER = "filter";
    public static final String ACTIVE = "Active";
    public static final String INACTIVE = "Inactive";
    public static final String EVERY_DAY = "Every day";
    public static final String EVERY_WEEK = "Every week";
    public static final String EVERY_MONTH = "Every month";

    public static final String SHAREDPREFERENCES_FILE_NAME = "mentett_adatok";
    public static final String SHAREDPREFERENCES_USER_FIRSTNAME = "user_firstname";
    public static final String SHAREDPREFERENCES_USER_ID = "user_id";
    public static final String SHAREDPREFERENCES_USER_LASTNAME = "user_lastname";
    public static final String SHAREDPREFERENCES_USER_LAST_LOGIN_TIME = "user_last_login_time";
    public static final String SHAREDPREFERENCES_ACCOUNT_NUMBER_POSITION = "account_number_position";
    public static final String SHAREDPREFERENCES_BANK_ACCOUNT = "bank_account";
    public static final String SHAREDPREFERENCES_TRANSACTION = "transaction";
    public static final String SHAREDPREFERENCES_BENEFICIARY = "beneficiary";
    public static final String SHAREDPREFERENCES_LOGGED_IN = "bejelentkezve";
    public static final String SHAREDPREFERENCES_LATITUDE_CURRENT = "aktualis_latitude";
    public static final String SHAREDPREFERENCES_LONGITUDE_CURRENT = "aktualis_longitude";
    public static final String SHAREDPREFERENCES_LATITUDE_PREVIOUS = "elozo_latitude";
    public static final String SHAREDPREFERENCES_LONGITUDE_PREVIOUS = "elozo_longitude";
    public static final String SHAREDPREFERENCES_RECURRING_TRANSFER = "recurringtransfer";
    public static final String SHAREDPREFERENCES_BANNED_REMAINING_TIME = "remaining_time_banned";
    public static final String SHAREDPREFERENCES_DISTANCE_REFRESH = "tavolsagfrissiteshez_szukseges_ujrainditas_egyszer_mar_lefutott";
    public static final String SHAREDPREFERENCES_EXCHANGE_RATE_REFRESH = "arfolyamfrissiteshez_szukseges_ujrainditas_egyszer_mar_lefutott";
    public static final String SHAREDPREFERENCES_BRANCHES_AND_ATMS_ORDER_TYPE = "bankfiokok_es_atmek_rendezesi_mod";
    public static final String SHAREDPREFERENCES_ID_ENTERED = "entered_id";
    public static final String SHAREDPREFERENCES_PINCODE_USER = "user_pincode";
    public static final String SHAREDPREFERENCES_LOCATION_STARTED = "helymeghatarozas_elinditva";
    public static final String SHAREDPREFERENCES_SAVING = "saving";
    public static final String SHAREDPREFERENCES_LANGUAGE = "valasztott_nyelv";

    public static final String SUNDAY = "Sunday";
    public static final String MONDAY = "Monday";
    public static final String TUESDAY = "Tuesday";
    public static final String WEDNESDAY = "Wednesday";
    public static final String THURSDAY = "Thursday";
    public static final String FRIDAY = "Friday";
    public static final String SATURDAY = "Saturday";

    public static final String VASARNAP = "vasárnap";
    public static final String HETFO = "hétfő";
    public static final String KEDD = "kedd";
    public static final String SZERDA = "szerda";
    public static final String CSUTORTOK = "csütörtök";
    public static final String PENTEK = "péntek";
    public static final String SZOMBAT = "szombat";

    public static final String SONNTAG = "Sonntag";
    public static final String MONTAG = "Montag";
    public static final String DIENSTAG = "Dienstag";
    public static final String MITTWOCH = "Mittwoch";
    public static final String DONNERSTAG = "Donnerstag";
    public static final String FREITAG = "Freitag";
    public static final String SAMSTAG = "Samstag";

    // ezeket szinkronizálandóak az adatbázisban található szövegekkel!!!
    // elegánsabb lenne, hogy először beolvasom a különböző típusokat adatbázisból, és az alapján hozom létre a konstansokat, de akkor a különböző nyelvekhez tartozó string-eket nem tudom, hogyan generálnám le?
    public static final String RETAIL_BANK_ACCOUNT = "Retail bank account";
    public static final String SAVING_ACCOUNT = "Saving account";
    public static final String FOREIGN_CURRENCY_ACCOUNT = "Foreign currency account";
    public static final String BREAKED = "Breaked";
    public static final String EXPIRED = "Expired";
    public static final String YEARLY_SAVING = "Yearly saving";
    public static final String MONTHLY_SAVING = "Monthly saving";
    public static final String WEEKLY_SAVING = "Weekly saving";
    public static final String QUARTERLY_SAVING = "Quarterly saving";

    public static final String TYPE_ACCOUNT_OPEN = "account open";
    public static final String TYPE_DEPOSIT = "deposit";
    public static final String TYPE_WITHDRAWAL = "withdrawal";
    public static final String TYPE_TRANSFER_FEE = "transfer fee";
    public static final String TYPE_SAVING = "saving";
    public static final String TYPE_EARLY_WITHDRAWAL = "early withdrawal";
    public static final String TYPE_FUND_WITHDRAWAL = "fund withdrawal";
    public static final String TYPE_INTEREST_WITHDRAWAL = "interest withdrawal";
    public static final String TYPE_RECURRING_TRANSFER = "recurring transfer";
    public static final String TYPE_OUTGOING_TRANSFER = "outgoing transfer";
    public static final String TYPE_INCOMING_TRANSFER = "incoming transfer";
    public static final String TYPE_RECURRING_TRANSFER_FEE = "recurring transfer fee";
    public static final String TYPE_PURCHASE_WITH_CREDIT_CARD = "purchase with credit card";
    public static final String TYPE_MONTHLY_FEE = "monthly fee";
    public static final String IN = "in";
    public static final String OUT = "out";
    public static final String NULL = "null";

    public static final String COMMENT_MONTHLY_FEE = "Monthly fee";
    public static final String COMMENT_RANDOM_COMMENT = "Random comment";
    public static final String COMMENT_ACCOUNT_OPENING = "Account opening";
    public static final String COMMENT_FUND_WITHDRAWAL = "fund withdrawal";
    public static final String COMMENT_INTEREST_WITHDRAWAL = "interest withdrawal";
    public static final String COMMENT_SAVING = "Saving";
    public static final String COMMENT_EARLY_WITHDRAWAL = "Early withdrawal";


}
