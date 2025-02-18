package com.application.application.fragment.order;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.application.R;
import com.application.application.Utils;
import com.application.application.database.DatabaseHelper;
import com.application.application.database.enums.OrderStatus;
import com.application.application.fragment.order.detail_order.DetailOrderDialogFragment;
import com.application.application.model.Order;

import java.util.List;

public class OrderFragment extends Fragment implements DetailOrderDialogFragment.OnOrderUpdatedListener {
    private RecyclerView orderRecycleView;
    private OrderFragmentAdapter orderFragmentAdapter;
    private List<Order> orderList;
    private DatabaseHelper dbHelper;
    private Button btnCreateNewOrder;

    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        initElements(view);

        return view;
    }

    private void initElements(View view) {
        dbHelper = new DatabaseHelper(requireContext());
        btnCreateNewOrder = view.findViewById(R.id.btn_create_new_order);
        initOrderRecycleView(view);

        btnCreateNewOrder.setOnClickListener(v -> showCreateDialog());
    }

    private void initOrderRecycleView(View view) {
        orderRecycleView = view.findViewById(R.id.order_list);
        orderRecycleView.setLayoutManager(new LinearLayoutManager(requireContext()));

        orderList = dbHelper.getOrdersList();
        orderFragmentAdapter = new OrderFragmentAdapter(requireContext(), orderList);
        orderRecycleView.setAdapter(orderFragmentAdapter);
    }

    private void showCreateDialog() {
        View createDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_order_layout, null);
        EditText orderName = createDialogView.findViewById(R.id.dialog_create_order_name);
        EditText orderCreatedAt = createDialogView.findViewById(R.id.dialog_create_order_created_at);
        orderCreatedAt.setText(Utils.getCurrentTime());

        EditText orderDescription = createDialogView.findViewById(R.id.dialog_create_order_description);
        Button orderCreateButton = createDialogView.findViewById(R.id.dialog_create_order_btn_create);
        Button closeButton = createDialogView.findViewById(R.id.dialog_create_order_btn_close);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(createDialogView);
        final AlertDialog dialog = builder.create();

        orderCreateButton.setOnClickListener(v -> createNewOrder(orderName, orderDescription, orderCreatedAt));
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void createNewOrder(EditText orderNameField, EditText orderDescriptionField, EditText orderCreatedAtField) {
        String orderName = orderNameField.getText().toString().trim();
        String orderDescription = orderDescriptionField.getText().toString().trim();
        String orderCreatedAt = orderCreatedAtField.getText().toString();
        String regex = getString(R.string.regex_cart_name);
        boolean isValid = Utils.regexVerify(orderName, regex);

        if (isValid) {
            boolean checkExistOrder = dbHelper.isExistsOrder("name", orderName);

            if (!checkExistOrder) {
                ContentValues values = new ContentValues();
                values.put("name", orderName);
                values.put("description", orderDescription);
                values.put("created_at", orderCreatedAt);
                values.put("updated_at", orderCreatedAt);
                values.put("status", String.valueOf(OrderStatus.PENDING.getStatusValue()));

                long result = dbHelper.createOrder(values);

                if (result > 0) {
                    orderNameField.setText("");
                    orderCreatedAtField.setText(Utils.getCurrentTime());
                    orderDescriptionField.setText("");

                    Toast.makeText(requireContext(), "Thêm giỏ hàng thành công!", Toast.LENGTH_LONG).show();
                    orderFragmentAdapter.refreshOrdersList();
                }
            } else {
                Toast.makeText(requireContext(), "Giỏ hàng đã tồn tại", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(requireContext(), "Tên giỏ hàng không hợp lệ", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onOrderUpdated() {
        Toast.makeText(requireContext(), "Check...", Toast.LENGTH_SHORT).show();
        orderFragmentAdapter.refreshOrdersList();
    }
}