package com.application.application.common.pdf;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.application.application.R;
import com.application.application.Utils;
import com.application.application.model.OrderItem;
import com.application.application.model.OrderWithItems;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutputStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class InvoicePDFGenerator {
    public static void createInvoicePDF(Context context, OrderWithItems orderInfo, String userFullname, String totalPriceOfOrder) throws IOException {
        // Tạo đường dẫn lưu file PDF
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, orderInfo.getOrder().getName() + "_" + System.currentTimeMillis() + ".pdf");

        // Tạo các đối tượng iText7 để thiết kế giao diện PDF
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        document.setMargins(0, 0, 0, 0);

        // ------------------------------------------------------------------- PHẦN ẢNH NỀN & COVER -------------------------------------------------------------------
        // Phần nền của file PDF
        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable drawable_1 = context.getDrawable(R.drawable.pdf_background);
        Bitmap bitmap_1 = ((BitmapDrawable) drawable_1).getBitmap();
        ByteArrayOutputStream stream_1 = new ByteArrayOutputStream();
        bitmap_1.compress(Bitmap.CompressFormat.JPEG, 100, stream_1);
        byte[] bitmapData_1 = stream_1.toByteArray();

        ImageData imageData_1 = ImageDataFactory.create(bitmapData_1);
        pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, new BackgroundImagePDFHandler(imageData_1));
        Image image_1 = new Image(imageData_1);

        // Phần heading của file PDF
        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable drawable_2 = context.getDrawable(R.drawable.pdf_cover);
        Bitmap bitmap_2 = ((BitmapDrawable) drawable_2).getBitmap();
        ByteArrayOutputStream stream_2 = new ByteArrayOutputStream();
        bitmap_2.compress(Bitmap.CompressFormat.JPEG, 100, stream_2);
        byte[] bitmapData_2 = stream_2.toByteArray();

        ImageData imageData_2 = ImageDataFactory.create(bitmapData_2);
        Image image_2 = new Image(imageData_2);


        // ------------------------------------------------------------------- PHẦN THÔNG TIN -------------------------------------------------------------------
        // Lấy font Times New Roman để set vào font chữ
        String fontPath = "assets/fonts/OpenSans/OpenSans-Medium.ttf";
        FontProgram fontProgram = FontProgramFactory.createFont(fontPath);
        PdfFont pdfFont = PdfFontFactory.createFont(fontProgram, PdfEncodings.IDENTITY_H);

        // Bảng thông tin cơ bản của hoá đơn
        float columnWidth_1[] = {150, 300};
        Table table_1 = new Table(columnWidth_1);

        table_1.setMargins(-100, 20, 0, 20);

        table_1.addCell(new Cell().add(new Paragraph("Họ và tên:").setFontSize(14).setFont(pdfFont).setFontColor(ColorConstants.WHITE)).setBorder(Border.NO_BORDER));
        table_1.addCell(new Cell().add(new Paragraph(userFullname).setFontSize(14).setFont(pdfFont).setFontColor(ColorConstants.WHITE)).setBorder(Border.NO_BORDER));
        table_1.addCell(new Cell().add(new Paragraph("Mã đơn:").setFontSize(14).setFont(pdfFont).setFontColor(ColorConstants.WHITE)).setBorder(Border.NO_BORDER));
        table_1.addCell(new Cell().add(new Paragraph(String.valueOf(orderInfo.getOrder().getId())).setFontSize(14).setFont(pdfFont).setFontColor(ColorConstants.WHITE)).setBorder(Border.NO_BORDER));

        table_1.addCell(new Cell().add(new Paragraph("Thành tiền:").setFont(pdfFont).setFontSize(14).setFontColor(ColorConstants.WHITE)).setBorder(Border.NO_BORDER));
        table_1.addCell(new Cell().add(new Paragraph(Utils.formatCurrency(Integer.parseInt(totalPriceOfOrder)) + " VND").setFont(pdfFont).setFontSize(14).setFontColor(ColorConstants.WHITE)).setBorder(Border.NO_BORDER));
        table_1.addCell(new Cell().add(new Paragraph("Ngày thanh toán:").setFont(pdfFont).setFontSize(14).setFontColor(ColorConstants.WHITE)).setBorder(Border.NO_BORDER));
        table_1.addCell(new Cell().add(new Paragraph(orderInfo.getOrder().getDelivery_at()).setFont(pdfFont).setFontSize(14).setFontColor(ColorConstants.WHITE)).setBorder(Border.NO_BORDER));

        // Bảng thông tin các sản phẩm trong hoá đơn
        DeviceRgb table2_border_color = new DeviceRgb(255, 204, 0);
        float columnWidth_2[] = {180, 120, 120, 140};
        Table table_2 = new Table(columnWidth_2);
        table_2.setMargins(20, 20, 0, 20);

        table_2.addCell(new Cell().add(new Paragraph("Sản phẩm").setFontSize(15).setFont(pdfFont).setFontColor(ColorConstants.WHITE)).setBorder(new SolidBorder(table2_border_color, 1)).setBackgroundColor(table2_border_color));
        table_2.addCell(new Cell().add(new Paragraph("Số lượng").setFontSize(15).setFont(pdfFont).setFontColor(ColorConstants.WHITE)).setBorder(new SolidBorder(table2_border_color, 1)).setBackgroundColor(table2_border_color));
        table_2.addCell(new Cell().add(new Paragraph("Đơn giá").setFontSize(15).setFont(pdfFont).setFontColor(ColorConstants.WHITE)).setBorder(new SolidBorder(table2_border_color, 1)).setBackgroundColor(table2_border_color));
        table_2.addCell(new Cell().add(new Paragraph("Thành tiền").setFontSize(15).setFont(pdfFont).setFontColor(ColorConstants.WHITE)).setBorder(new SolidBorder(table2_border_color, 1)).setBackgroundColor(table2_border_color));

        for (OrderItem item : orderInfo.getOrderItemList()) {
            table_2.addCell(new Cell().add(new Paragraph(String.valueOf(item.getFood_name())).setFontSize(15).setFont(pdfFont).setFontColor(ColorConstants.WHITE)).setBorder(new SolidBorder(table2_border_color, 1)));
            table_2.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity())).setFontSize(15).setFont(pdfFont).setFontColor(ColorConstants.WHITE)).setBorder(new SolidBorder(table2_border_color, 1)));
            table_2.addCell(new Cell().add(new Paragraph(Utils.formatCurrency((int) (item.getTotalPrice() / item.getQuantity()))).setFontSize(15).setFont(pdfFont).setFontColor(ColorConstants.WHITE)).setBorder(new SolidBorder(table2_border_color, 1)));
            table_2.addCell(new Cell().add(new Paragraph(Utils.formatCurrency((int) item.getTotalPrice())).setFontSize(15).setFont(pdfFont).setFontColor(ColorConstants.WHITE)).setBorder(new SolidBorder(table2_border_color, 1)));
        }

        table_2.addCell(new Cell(1, 2).setBorder(new SolidBorder(table2_border_color, 1)));
        table_2.addCell(new Cell().add(new Paragraph("Tổng tiền").setFontSize(15).setFont(pdfFont).setFontColor(ColorConstants.WHITE)).setBorder(new SolidBorder(table2_border_color, 1)).setBackgroundColor(table2_border_color));
        table_2.addCell(new Cell().add(new Paragraph(Utils.formatCurrency(Integer.parseInt(totalPriceOfOrder)) + " VND").setFontSize(15).setFont(pdfFont).setFontColor(ColorConstants.WHITE)).setBorder(new SolidBorder(table2_border_color, 1)).setBackgroundColor(table2_border_color));

        // Vẽ các thành phần lên file PDF
        document.add(image_1.setFixedPosition(0, 0));
        document.add(image_2);
        document.add(table_1);
        document.add(table_2);

        document.close();
        Toast.makeText(context, "Hoá đơn PDF của " + orderInfo.getOrder().getName() + " đã tạo thành công!", Toast.LENGTH_SHORT).show();
    }
}
