package ca.on.conestogac.clo.pizza_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PizzaOrderDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pizza_orders.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_ORDERS = "orders";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_BASE = "base";
    public static final String COLUMN_TOPPINGS = "toppings";
    public static final String COLUMN_SPICE_LEVEL = "spice_level";
    public static final String COLUMN_DRESSING = "dressing";
    public static final String COLUMN_DELIVERY_METHOD = "delivery_method";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_TOTAL = "total";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    public static final String[] ALL_COLUMNS = {
            COLUMN_ID,
            COLUMN_SIZE,
            COLUMN_BASE,
            COLUMN_TOPPINGS,
            COLUMN_SPICE_LEVEL,
            COLUMN_DRESSING,
            COLUMN_DELIVERY_METHOD,
            COLUMN_NAME,
            COLUMN_ADDRESS,
            COLUMN_PHONE,
            COLUMN_TOTAL,
            COLUMN_TIMESTAMP
    };

    public PizzaOrderDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_ORDERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SIZE + " TEXT, " +
                COLUMN_BASE + " TEXT, " +
                COLUMN_TOPPINGS + " TEXT, " +
                COLUMN_SPICE_LEVEL + " INTEGER, " +
                COLUMN_DRESSING + " TEXT, " +
                COLUMN_DELIVERY_METHOD + " TEXT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_ADDRESS + " TEXT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_TOTAL + " REAL, " +
                COLUMN_TIMESTAMP + " TEXT" +
                ")";
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }
}
