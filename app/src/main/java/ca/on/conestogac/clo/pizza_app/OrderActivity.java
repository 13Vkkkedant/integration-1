package ca.on.conestogac.clo.pizza_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderActivity extends AppCompatActivity {

    private Spinner spinnerSize;
    private Spinner spinnerBase;
    private EditText etQuantity;
    private SeekBar seekBarSpice;
    private RadioGroup radioGroupDressing;
    private RadioGroup radioGroupDelivery;
    private EditText etName;
    private EditText etAddress;
    private EditText etPhone;
    private Button btnPlaceOrder;
    private Button btnResetOrder;
    private CheckBox chkMushroom;
    private CheckBox chkPepperoni;
    private TextView tvOrderTotal;

    private static final double SMALL_PRICE = 10.0;
    private static final double MEDIUM_PRICE = 12.0;
    private static final double LARGE_PRICE = 14.0;
    private static final double TOPPING_PRICE = 0.5;
    private static final double CRUST_EXTRA_CHARGE = 1.0;
    private static final double DELIVERY_CHARGE = 1.99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);


        spinnerSize = findViewById(R.id.spinner_size);
        spinnerBase = findViewById(R.id.spinner_base);
        etQuantity = findViewById(R.id.et_quantity);
        seekBarSpice = findViewById(R.id.seekbar_spice);
        chkMushroom = findViewById(R.id.chk_mushroom);
        chkPepperoni = findViewById(R.id.chk_pepperoni);
        radioGroupDressing = findViewById(R.id.radio_group_dressing);
        radioGroupDelivery = findViewById(R.id.radio_group_delivery);
        etName = findViewById(R.id.et_name);
        etAddress = findViewById(R.id.et_address);
        etPhone = findViewById(R.id.et_phone);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
        btnResetOrder = findViewById(R.id.btn_reset_order);
        tvOrderTotal = findViewById(R.id.tv_order_total);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double orderTotal = calculateOrderTotal();
                tvOrderTotal.setText(getString(R.string.order_total, orderTotal));
                String selectedSize = spinnerSize.getSelectedItem().toString();
                String selectedBase = spinnerBase.getSelectedItem().toString();
                String quantityString = etQuantity.getText().toString().trim();
                String name = etName.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();

                if (selectedSize.isEmpty()) {
                    showToast("Please select a pizza size.");
                    return;
                }
                if (selectedBase.isEmpty()) {
                    showToast("Please select a pizza base.");
                    return;
                }
                if (quantityString.isEmpty()) {
                    showToast("Please enter the quantity of pizzas.");
                    return;
                }
                int quantity = Integer.parseInt(quantityString);
                if (quantity <= 0) {
                    showToast("Please enter a valid quantity (greater than zero).");
                    return;
                }
                if (name.isEmpty()) {
                    showToast("Please enter your name.");
                    return;
                }
                if (address.isEmpty()) {
                    showToast("Please enter your address.");
                    return;
                }
                if (!isValidPhoneNumber(phone)) {
                    showToast("Please enter a valid phone number.");
                    return;
                }

                String toppings = getSelectedToppings();
                int spiceLevel = seekBarSpice.getProgress();
                String dressing = getSelectedDressing();
                String deliveryMethod = getDeliveryMethod();

                Intent intent = new Intent(OrderActivity.this, SummaryActivity.class);

                intent.putExtra("size", selectedSize);
                intent.putExtra("base", selectedBase);
                intent.putExtra("quantity", quantity);
                intent.putExtra("toppings", toppings);
                intent.putExtra("spice_level", spiceLevel);
                intent.putExtra("dressing", dressing);
                intent.putExtra("delivery_method", deliveryMethod);
                intent.putExtra("name", name);
                intent.putExtra("address", address);
                intent.putExtra("phone", phone);
                intent.putExtra("total", orderTotal);

                PizzaOrder order = new PizzaOrder(selectedSize, selectedBase, toppings, spiceLevel, dressing, deliveryMethod, name, address, phone, orderTotal);
                saveOrderToDatabase(order);

                startActivity(intent);
            }
        });

        btnResetOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerSize.setSelection(0);
                spinnerBase.setSelection(0);
                etQuantity.setText("");
                seekBarSpice.setProgress(0);
                radioGroupDressing.clearCheck();
                radioGroupDelivery.clearCheck();
                etName.setText("");
                etAddress.setText("");
                etPhone.setText("");
            }
        });

        if (getIntent().getBooleanExtra("editMode", false)) {
            populateOrderFromDatabase();
        }
    }

    private double calculateOrderTotal() {
        double total = 0.0;
        String selectedSize = spinnerSize.getSelectedItem().toString();
        String selectedBase = spinnerBase.getSelectedItem().toString();
        String quantityString = etQuantity.getText().toString().trim();

        int quantity = 1;
        if (!quantityString.isEmpty()) {
            quantity = Integer.parseInt(quantityString);
            if (quantity <= 0) {
                showToast("Please enter a valid quantity (greater than zero).");
                return 0.0;
            }
        }

        if (selectedSize.equals(getString(R.string.size_small))) {
            total += SMALL_PRICE;
        } else if (selectedSize.equals(getString(R.string.size_medium))) {
            total += MEDIUM_PRICE;
        } else if (selectedSize.equals(getString(R.string.size_large))) {
            total += LARGE_PRICE;
        }

        total += getToppingCharge();

        if (selectedBase.equals(getString(R.string.base_thin_crust)) || selectedBase.equals(getString(R.string.base_deep_dish))) {
            total += CRUST_EXTRA_CHARGE;
        }

        if (radioGroupDelivery.getCheckedRadioButtonId() == R.id.radio_delivery) {
            total += DELIVERY_CHARGE;
        }

        total *= quantity;

        return total;
    }

    private double getToppingCharge() {
        double toppingCharge = 0.0;
        if (chkPepperoni.isChecked()) {
            toppingCharge += TOPPING_PRICE;
        }
        if (chkMushroom.isChecked()) {
            toppingCharge += TOPPING_PRICE;
        }
        return toppingCharge;
    }

    private void saveOrderToDatabase(PizzaOrder order) {
        PizzaOrderDBHelper dbHelper = new PizzaOrderDBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PizzaOrderDBHelper.COLUMN_SIZE, order.getSize());
        values.put(PizzaOrderDBHelper.COLUMN_BASE, order.getBase());
        values.put(PizzaOrderDBHelper.COLUMN_TOPPINGS, order.getToppings());
        values.put(PizzaOrderDBHelper.COLUMN_SPICE_LEVEL, order.getSpiceLevel());
        values.put(PizzaOrderDBHelper.COLUMN_DRESSING, order.getDressing());
        values.put(PizzaOrderDBHelper.COLUMN_DELIVERY_METHOD, order.getDeliveryMethod());
        values.put(PizzaOrderDBHelper.COLUMN_NAME, order.getName());
        values.put(PizzaOrderDBHelper.COLUMN_ADDRESS, order.getAddress());
        values.put(PizzaOrderDBHelper.COLUMN_PHONE, order.getPhone());
        values.put(PizzaOrderDBHelper.COLUMN_TOTAL, order.getTotal());
        values.put(PizzaOrderDBHelper.COLUMN_TIMESTAMP, order.getOrderTimestamp().toString());

        long newRowId = db.insert(PizzaOrderDBHelper.TABLE_ORDERS, null, values);
        db.close(); // Close the database connection

        if (newRowId == -1) {
            showToast("Error saving order");
        } else {
            showToast("Order saved successfully"); // optional
        }
    }

    private void populateOrderFromDatabase() {
        PizzaOrderDBHelper dbHelper = new PizzaOrderDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + PizzaOrderDBHelper.TABLE_ORDERS + " ORDER BY " + PizzaOrderDBHelper.COLUMN_TIMESTAMP + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            for (String columnName : PizzaOrderDBHelper.ALL_COLUMNS) {
                int columnIndex = cursor.getColumnIndex(columnName);
                if (columnIndex >= 0) {
                    if (columnName.equals(PizzaOrderDBHelper.COLUMN_SIZE)) {
                        String size = cursor.getString(columnIndex);
                        setSpinnerSelection(spinnerSize, size);
                    } else if (columnName.equals(PizzaOrderDBHelper.COLUMN_BASE)) {
                        String base = cursor.getString(columnIndex);
                        setSpinnerSelection(spinnerBase, base);
                    } else if (columnName.equals(PizzaOrderDBHelper.COLUMN_TOPPINGS)) {
                        String toppings = cursor.getString(columnIndex);
                        if (toppings != null && !toppings.isEmpty()) {
                            String[] selectedToppings = toppings.split(",");
                            for (String topping : selectedToppings) {
                                if (topping.trim().equalsIgnoreCase("pepperoni")) {
                                    chkPepperoni.setChecked(true);
                                } else if (topping.trim().equalsIgnoreCase("mushroom")) {
                                    chkMushroom.setChecked(true);
                                }
                            }
                        }
                    } else if (columnName.equals(PizzaOrderDBHelper.COLUMN_SPICE_LEVEL)) {
                        int spiceLevel = cursor.getInt(columnIndex);
                        seekBarSpice.setProgress(spiceLevel);
                    } else if (columnName.equals(PizzaOrderDBHelper.COLUMN_DRESSING)) {
                        String dressing = cursor.getString(columnIndex);
                        setRadioButton(radioGroupDressing, dressing);
                    } else if (columnName.equals(PizzaOrderDBHelper.COLUMN_DELIVERY_METHOD)) {
                        String deliveryMethod = cursor.getString(columnIndex);
                        setRadioButton(radioGroupDelivery, deliveryMethod);
                    } else if (columnName.equals(PizzaOrderDBHelper.COLUMN_NAME)) {
                        String name = cursor.getString(columnIndex);
                        etName.setText(name);
                    } else if (columnName.equals(PizzaOrderDBHelper.COLUMN_ADDRESS)) {
                        String address = cursor.getString(columnIndex);
                        etAddress.setText(address);
                    } else if (columnName.equals(PizzaOrderDBHelper.COLUMN_PHONE)) {
                        String phone = cursor.getString(columnIndex);
                        etPhone.setText(phone);
                    } else if (columnName.equals(PizzaOrderDBHelper.COLUMN_TOTAL)) {
                        double total = cursor.getDouble(columnIndex);
                        // ... Set total (If you need to display it) ...
                    }
                } else {
                    Log.e("OrderActivity", "Column not found: " + columnName);
                }
            }
        } else {
            showToast("No recent orders found.");
        }

        cursor.close();
        db.close();
    }


    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void setRadioButton(RadioGroup radioGroup, String value) {
        int count = radioGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View o = radioGroup.getChildAt(i);
            if (o instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) o;
                if (radioButton.getText().toString().equals(value)) {
                    radioButton.setChecked(true);
                    break;
                }
            }
        }
    }


    private void showToast(String message){
        Toast.makeText(OrderActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean isValidPhoneNumber(String phone) {
        String phoneRegex = "^[0-9]{10}$";`
        Pattern pattern = Pattern.compile(phoneRegex);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private String getSelectedToppings() {
        StringBuilder toppings = new StringBuilder();
        if (chkPepperoni.isChecked()) {
            toppings.append(getString(R.string.topping_pepperoni)).append(", ");
        }
        if (chkMushroom.isChecked()) {
            toppings.append(getString(R.string.topping_mushroom)).append(", ");
        }
        return toppings.toString();
    }

    private String getSelectedDressing() {
        int selectedId = radioGroupDressing.getCheckedRadioButtonId();
        if (selectedId == R.id.radio_none) {
            return getString(R.string.dressing_none);
        } else if (selectedId == R.id.radio_ranch) {
            return getString(R.string.dressing_ranch);
        }
        return "";
    }

    private String getDeliveryMethod() {
        int selectedId = radioGroupDelivery.getCheckedRadioButtonId();
        if (selectedId == R.id.radio_delivery) {
            return getString(R.string.delivery);
        } else if (selectedId == R.id.radio_pickup) {
            return getString(R.string.pickup);
        }
        return "";
    }
}
