package com.application.application.common.pdf;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.element.Image;

public class BackgroundImagePDFHandler implements IEventHandler {
    private final ImageData pdfBackgroundImage;

    public BackgroundImagePDFHandler(ImageData pdfBackgroundImage) {
        this.pdfBackgroundImage = pdfBackgroundImage;
    }

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent pdfEvent = (PdfDocumentEvent) event;
        PdfCanvas canvas = new PdfCanvas(pdfEvent.getPage());

        canvas.addImageFittedIntoRectangle(
                pdfBackgroundImage,
                pdfEvent.getPage().getPageSize(),
                true
        );

        canvas.release();
    }
}
