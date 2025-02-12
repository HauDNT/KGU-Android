package com.application.application.activity.order.choose_order;

import static android.widget.Toast.LENGTH_LONG;

import android.app.Dialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.application.R;
import com.application.application.Utils;
import com.application.application.database.DatabaseHelper;
import com.application.application.database.enums.OrderStatus;
import com.application.application.model.Order;

import java.util.ArrayList;
import java.util.List;

public class ChooseOrderDialogFragment extends DialogFragment implements ChooseOrderDialogAdapter.OnOrderSelectedListener {
    private DatabaseHelper dbHelper;
    private RecyclerView orderRecycleView;
    private ChooseOrderDialogAdapter chooseOrderDialogAdapter;
    private List<Order> orderList;
    private List<Integer> ordersSelected = new ArrayList<>();
    private TextView alertText, orderQuantity;
    private EditText orderName, orderCreatedAt;
    private Button createOrderBtn, addOrderBtn, upQuantityBtn, downQuantityBtn;
    private int foodId = -1;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.dialog_choose_order_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        dbHelper = new DatabaseHelper(requireActivity());

        getFoodId();
        initElements(dialog);
        handleCreateOrder();
        addFoodToOrder();
        setUpQuantityBtn();
        setDownQuantityBtn();

        return dialog;
    }

    // Hàm lấy food id của sản phẩm cần thêm vào giỏ hàng (được truyền qua từ FoodActivity với Intent - Bundle)
    private void getFoodId() {
        Bundle args = getArguments();
        String getFoodId = args != null ? args.getString("food_id") : "";

        if (!getFoodId.equals("")) foodId = Integer.parseInt(getFoodId);
    }

    public void initElements(Dialog dialog) {
        alertText = dialog.findViewById(R.id.dialog_choose_order_alert);
        orderName = dialog.findViewById(R.id.dialog_choose_order_name);
        orderCreatedAt = dialog.findViewById(R.id.dialog_choose_order_created_at);
        orderCreatedAt.setText(Utils.getCurrentTime());
        createOrderBtn = dialog.findViewById(R.id.dialog_choose_order_btn_create);

        addOrderBtn = dialog.findViewById(R.id.dialog_choose_order_btn_add);
        orderQuantity = dialog.findViewById(R.id.dialog_choose_cart_text_quantity);
        upQuantityBtn = dialog.findViewById(R.id.dialog_choose_cart_btn_up_quantity);
        downQuantityBtn = dialog.findViewById(R.id.dialog_choose_cart_btn_down_quantity);

        initOrderRecycleview(dialog);
    }

    public void initOrderRecycleview(Dialog dialog) {
        orderRecycleView = dialog.findViewById(R.id.order_item_list);
        orderRecycleView.setLayoutManager(new LinearLayoutManager(requireContext()));

        orderList = dbHelper.getOrdersList();

        chooseOrderDialogAdapter = new ChooseOrderDialogAdapter(requireActivity(), orderList, (ChooseOrderDialogAdapter.OnOrderSelectedListener) this);
        orderRecycleView.setAdapter(chooseOrderDialogAdapter);
    }

    // Hàm xử lý sự kiện bấm nút tăng số lượng
    public void setUpQuantityBtn() {
        upQuantityBtn.setOnClickListener(v -> {
            int quantity = Integer.parseInt(orderQuantity.getText().toString());

            quantity = quantity + 1;

            orderQuantity.setText(Integer.toString(quantity));
        });
    }

    // Hàm xử lý sự kiện bấm nút giảm số lượng
    public void setDownQuantityBtn() {
        downQuantityBtn.setOnClickListener(v -> {
            int quantity = Integer.parseInt(orderQuantity.getText().toString());

            if (quantity > 1) {
                quantity = quantity - 1;
                orderQuantity.setText(String.valueOf(quantity));
            }
        });
    }

    // Hàm tạo một giỏ hàng mới nếu người dùng không muốn chọn giỏ hàng có sẵn
    public void handleCreateOrder() {
        createOrderBtn.setOnClickListener(v -> {
            String name = orderName.getText().toString();
            String regex = getString(R.string.regex_cart_name);
            boolean isValidName = Utils.regexVerify(name, regex);

            if (isValidName) {
                alertText.setVisibility(View.GONE);

                ContentValues values = new ContentValues();
                values.put("name", name);
                values.put("created_at", orderCreatedAt.getText().toString());
                values.put("updated_at", orderCreatedAt.getText().toString());
                values.put("status", String.valueOf(OrderStatus.PENDING.getStatusValue()));

                long result = dbHelper.createOrder(values);

                if (result > 0) {
                    orderName.setText("");
                    orderName.clearFocus();
                    chooseOrderDialogAdapter.updateOrderListUI(dbHelper.getOrdersList());
                    Toast.makeText(requireActivity(), "Tạo giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                }
            } else {
                alertText.setVisibility(View.VISIBLE);
                alertText.setText("Tên giỏ hàng không hợp lệ");
            }
        });
    }

    // Hàm Override lại onOrderSelected của ChooseOrderDialogAdapter để lấy id của giỏ hàng được chọn (có thể lấy được nhiều giỏ hàng)
    @Override
    public void onOrderSelected(int orderId, boolean isChecked) {
        if (isChecked) {
            ordersSelected.add(orderId);
        } else {
            ordersSelected.remove(Integer.valueOf(orderId));
        }
    }

    // Hàm thêm food vào giỏ hàng
    public void addFoodToOrder() {
        addOrderBtn.setOnClickListener(v -> {
            if (ordersSelected.size() < 0 || foodId == -1) {
                Toast.makeText(requireActivity(), "Dữ liệu giỏ hàng - sản phẩm không hợp lệ", LENGTH_LONG).show();
                return;
            }

            long result = dbHelper.addOrUpdateFoodToOrders(
                    foodId,
                    ordersSelected,
                    Integer.parseInt(orderQuantity.getText().toString())
            );

            if (result > 0) {
                Toast.makeText(requireActivity(), "Thêm vào giỏ hàng thành công", LENGTH_LONG).show();
            } else {
                Toast.makeText(requireActivity(), "Thêm vào giỏ hàng thất bại", LENGTH_LONG).show();
            }
        });
    }
}
