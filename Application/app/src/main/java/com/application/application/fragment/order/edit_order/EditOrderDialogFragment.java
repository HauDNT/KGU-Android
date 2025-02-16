package com.application.application.fragment.order.edit_order;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.application.application.R;
import com.application.application.Utils;
import com.application.application.database.DatabaseHelper;
import com.application.application.database.enums.OrderStatus;
import com.application.application.fragment.order.detail_order.DetailOrderDialogFragment;
import com.application.application.model.Order;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditOrderDialogFragment extends DialogFragment {

    private DatabaseHelper dbHelper;
    private Order order;
    private DetailOrderDialogFragment.OnOrderUpdatedListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DetailOrderDialogFragment.OnOrderUpdatedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnOrderUpdatedListener");
        }
    }

    public void setOnOrderUpdatedListener(DetailOrderDialogFragment.OnOrderUpdatedListener listener) {
        this.listener = listener;
    }

    public static EditOrderDialogFragment newInstance(Order order) {
        EditOrderDialogFragment fragment = new EditOrderDialogFragment();
        Bundle args = new Bundle();
        args.putInt("orderId", order.getId());
        fragment.setArguments(args);
        fragment.order = order;
        return fragment;
    }

    private int mapStatus(String statusString) {
        if ("Đang xử lý".equals(statusString)) {
            return OrderStatus.PENDING.getStatusValue();
        } else if ("Đã giao".equals(statusString)) {
            return OrderStatus.DELIVERED.getStatusValue();
        } else if ("Đã huỷ".equals(statusString)) {
            return OrderStatus.CANCELLED.getStatusValue();
        } else {
            return OrderStatus.PENDING.getStatusValue();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_order_layout, null);

        MaterialAutoCompleteTextView statusDropdown = view.findViewById(R.id.dialog_edit_order_status);
        String[] statusOptions = {"Đang xử lý", "Đã giao", "Đã huỷ"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, statusOptions);
        statusDropdown.setAdapter(adapter);

        EditText orderNameEditText = view.findViewById(R.id.dialog_edit_order_name);
        EditText orderDescriptionEditText = view.findViewById(R.id.dialog_edit_order_description);
        EditText deliveryAtEditText = view.findViewById(R.id.dialog_edit_order_delivery_at);
        Button btnSave = view.findViewById(R.id.dialog_edit_order_btn_save);
        Button btnClose = view.findViewById(R.id.dialog_edit_order_btn_close);

        dbHelper = new DatabaseHelper(getContext());

        //Điền thông tin hiện có của đơn hàng vào các trường
        orderNameEditText.setText(order.getName());
        orderDescriptionEditText.setText(order.getDescription());

        // deliveryAtEditText.setText(order.getDelivery_at());          // --> Đoạn này gây ra lỗi "Lỗi định dạng ngày"
//        SimpleDateFormat orderDelivery = order.getDelivery_at();
//        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
//        deliveryAtEditText.setText(format.format(orderDelivery));

        deliveryAtEditText.setText(Utils.convertStringDateValid(order.getDelivery_at(), "yyyy-MM-dd HH:mm:ss", "HH:mm:ss - dd/MM/yyyy"));

        //Cho phép click vào trường "Ngày giao hàng" để mở DateTimePicker.
        deliveryAtEditText.setFocusable(false);
        deliveryAtEditText.setClickable(true);
        deliveryAtEditText.setOnClickListener(v -> showDateTimePicker(deliveryAtEditText));

        //Xác định chuỗi trạng thái dựa trên enum OrderStatus
        String currentStatusStr;
        OrderStatus currentStatus = order.getStatus();
        if (currentStatus == OrderStatus.PENDING) {
            currentStatusStr = "Đang xử lý";
        } else if (currentStatus == OrderStatus.DELIVERED) {
            currentStatusStr = "Đã giao";
        } else if (currentStatus == OrderStatus.CANCELLED) {
            currentStatusStr = "Đã huỷ";
        } else {
            currentStatusStr = "Đang xử lý";
        }
        statusDropdown.setText(currentStatusStr, false);

        //Nếu đơn hàng quá khứ (status DELIVERED hoặc CANCELLED) thì không cho phép sửa.
        if (currentStatus == OrderStatus.DELIVERED || currentStatus == OrderStatus.CANCELLED) {
            Toast.makeText(getContext(), "Đơn hàng quá khứ không được sửa", Toast.LENGTH_SHORT).show();
            btnSave.setEnabled(false);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //Xử lý nút Lưu cập nhật đơn hàng
        btnSave.setOnClickListener(v -> {
            String newName = orderNameEditText.getText().toString().trim();
            String newDescription = orderDescriptionEditText.getText().toString().trim();
            String newDeliveryAt = deliveryAtEditText.getText().toString().trim();
            String newStatusStr = statusDropdown.getText().toString().trim();
            int newStatusValue = mapStatus(newStatusStr);

            if (newName.isEmpty() || newDeliveryAt.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            //Kiểm tra trùng tên: Nếu tên mới khác tên cũ, kiểm tra xem đã tồn tại đơn hàng nào có tên đó chưa
            if (!newName.equalsIgnoreCase(order.getName())) {
                boolean checkExistOrder = dbHelper.isExistsOrder("name", newName);
                if (checkExistOrder) {
                    Toast.makeText(getContext(), "Tên đơn hàng đã tồn tại", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss - dd/MM/yyyy", Locale.getDefault());
            try {
                Date newDeliveryDate = sdf.parse(newDeliveryAt);
                Date now = new Date();
                if (newDeliveryDate == null || newDeliveryDate.before(now)) {
                    Toast.makeText(getContext(), "Ngày giao hàng không hợp lệ (không được thuộc quá khứ)", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Lỗi định dạng ngày", Toast.LENGTH_SHORT).show();
                return;
            }

            //Tạo ContentValues chứa thông tin cập nhật
            ContentValues values = new ContentValues();
            values.put("name", newName);
            values.put("description", newDescription);
            values.put("delivery_at", newDeliveryAt);
            values.put("status", newStatusValue);
            values.put("updated_at", sdf.format(new Date()));

            int rowsUpdated = dbHelper.updateOrder(order.getId(), values);
            if (rowsUpdated > 0) {
                Toast.makeText(getContext(), "Cập nhật đơn hàng thành công", Toast.LENGTH_SHORT).show();

                if (listener != null) {
                    listener.onOrderUpdated();
                }

                dismiss();
            } else {
                Toast.makeText(getContext(), "Không thể cập nhật đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> dismiss());
        return dialog;
    }

    private void showDateTimePicker(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        // Mở DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    //Sau khi chọn ngày, mở TimePickerDialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                            (TimePicker view1, int hourOfDay, int minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                //Đặt giây mặc định là 00
                                calendar.set(Calendar.SECOND, 0);
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss - dd/MM/yyyy", Locale.getDefault());
                                editText.setText(sdf.format(calendar.getTime()));
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true);
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
