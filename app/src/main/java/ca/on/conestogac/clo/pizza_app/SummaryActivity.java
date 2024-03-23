package ca.on.conestogac.clo.pizza_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        TextView tvOrderSummary = findViewById(R.id.tv_order_summary);
        Button btnConfirmOrder = findViewById(R.id.btn_confirm_order);
        Button btnEditOrder = findViewById(R.id.btn_edit_order);

        Intent intent = getIntent();
        String selectedSize = intent.getStringExtra("size");
        String selectedBase = intent.getStringExtra("base");
        int quantity = intent.getIntExtra("quantity", 0);
        String toppings = intent.getStringExtra("toppings");
        int spiceLevel = intent.getIntExtra("spice_level", 0);
        String dressing = intent.getStringExtra("dressing");
        String deliveryMethod = intent.getStringExtra("delivery_method");
        String name = intent.getStringExtra("name");
        String address = intent.getStringExtra("address");
        String phone = intent.getStringExtra("phone");
        double total = intent.getDoubleExtra("total", 0);

        String summaryText = "Size: " + selectedSize + "\nBase: " + selectedBase + "\nQuantity: " + quantity
                + "\nToppings: " + toppings + "\nSpice Level: " + spiceLevel + "\nDressing: " + dressing
                + "\nDelivery Method: " + deliveryMethod + "\nName: " + name + "\nAddress: " + address
                + "\nPhone: " + phone + "\nTotal: $" + total;
        tvOrderSummary.setText(summaryText);


        btnConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btnEditOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long orderId = fetchOrderIdFromDatabase();

                Intent intent = new Intent(SummaryActivity.this, OrderActivity.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("editMode", true);
                startActivity(intent);
                finish();
            }
        });
    }

    private void populateOrderFromDatabase(long orderId) {
        PizzaOrderDBHelper dbHelper = new PizzaOrderDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + PizzaOrderDBHelper.TABLE_ORDERS + " WHERE " + PizzaOrderDBHelper.COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(orderId) });

        if (cursor.moveToFirst()) {
            LinearLayout orderDetailsContainer = findViewById(R.id.order_details_container);
            orderDetailsContainer.removeAllViews();

            // Extract values from the cursor
            String size = cursor.getString(cursor.getColumnIndex(PizzaOrderDBHelper.COLUMN_SIZE));
            String base = cursor.getString(cursor.getColumnIndex(PizzaOrderDBHelper.COLUMN_BASE));
            String toppings = cursor.getString(cursor.getColumnIndex(PizzaOrderDBHelper.COLUMN_TOPPINGS));
            int spiceLevel = cursor.getInt(cursor.getColumnIndex(PizzaOrderDBHelper.COLUMN_SPICE_LEVEL));
            String dressing = cursor.getString(cursor.getColumnIndex(PizzaOrderDBHelper.COLUMN_DRESSING));
            String deliveryMethod = cursor.getString(cursor.getColumnIndex(PizzaOrderDBHelper.COLUMN_DELIVERY_METHOD));
            String name = cursor.getString(cursor.getColumnIndex(PizzaOrderDBHelper.COLUMN_NAME));
            String address = cursor.getString(cursor.getColumnIndex(PizzaOrderDBHelper.COLUMN_ADDRESS));
            String phone = cursor.getString(cursor.getColumnIndex(PizzaOrderDBHelper.COLUMN_PHONE));
            double total = cursor.getDouble(cursor.getColumnIndex(PizzaOrderDBHelper.COLUMN_TOTAL));

            // Create TextViews dynamically for each detail
            createAndAddTextView(orderDetailsContainer, "Size:", size);
            createAndAddTextView(orderDetailsContainer, "Base:", base);
            createAndAddTextView(orderDetailsContainer, "Toppings:", toppings);
            createAndAddTextView(orderDetailsContainer, "Spice Level:", String.valueOf(spiceLevel));
            createAndAddTextView(orderDetailsContainer, "Dressing:", dressing);
            createAndAddTextView(orderDetailsContainer, "Delivery Method:", deliveryMethod);
            createAndAddTextView(orderDetailsContainer, "Name:", name);
            createAndAddTextView(orderDetailsContainer, "Address:", address);
            createAndAddTextView(orderDetailsContainer, "Phone:", phone);
            createAndAddTextView(orderDetailsContainer, "Total:", String.format("$%.2f", total));
        } else {
            showToast("Order not found.");
        }

        cursor.close();
        db.close();
    }


    private void createAndAddTextView(LinearLayout layout, String label, String value) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        itemLayout.setPadding(0, 8, 0, 8);

        TextView labelTextView = new TextView(this);
        labelTextView.setText(label);
        labelTextView.setTextColor(getResources().getColor(R.color.black));

        TextView valueTextView = new TextView(this);
        valueTextView.setText(value);
        valueTextView.setTextColor(getResources().getColor(R.color.black));
        valueTextView.setTypeface(null, Typeface.BOLD);
        valueTextView.setGravity(Gravity.END);

        itemLayout.addView(labelTextView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        itemLayout.addView(valueTextView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        layout.addView(itemLayout);
    }

    private long fetchOrderIdFromDatabase() {
        SQLiteDatabase db = new PizzaOrderDBHelper(this).getReadableDatabase();
        Cursor cursor = db.query(PizzaOrderDBHelper.TABLE_ORDERS, new String[]{"_id"}, null, null, null, null, PizzaOrderDBHelper.COLUMN_TIMESTAMP + " DESC", "1");

        long orderId = -1;
        if (cursor.moveToFirst()) {
            orderId = cursor.getLong(0);
        }
        cursor.close();
        db.close();
        return orderId;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
