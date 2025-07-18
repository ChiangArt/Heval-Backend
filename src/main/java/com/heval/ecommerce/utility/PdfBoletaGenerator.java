package com.heval.ecommerce.utility;

import com.heval.ecommerce.entity.Order;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class PdfBoletaGenerator {

    public static byte[] generateBoleta(Order order, String logoUrl) throws IOException {

        InputStream htmlInputStream = new ClassPathResource("templates/boleta.html").getInputStream();
        String htmlTemplate = new String(htmlInputStream.readAllBytes(), StandardCharsets.UTF_8);

        DecimalFormat df = new DecimalFormat("#,##0.00");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Generar tabla HTML para los productos
        String tablaProductos = order.getOrderItems().stream()
                .map(item -> {
                    String descripcion = item.getProduct().getTitle();
                    int cantidad = item.getQuantity();
                    BigDecimal unitario = item.getDiscountPrice();
                    BigDecimal subtotal = unitario.multiply(BigDecimal.valueOf(cantidad));
                    return String.format(
                            "<tr><td>%d</td><td>%s</td><td>S/ %s</td><td>S/ %s</td></tr>",
                            cantidad,
                            descripcion,
                            df.format(unitario),
                            df.format(subtotal)
                    );
                })
                .collect(Collectors.joining());

        // Calcular subtotal + IGV + total
        BigDecimal subtotal = order.getTotalDiscountedPrice().divide(new BigDecimal("1.18"), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal igv = order.getTotalDiscountedPrice().subtract(subtotal);
        BigDecimal total = order.getTotalDiscountedPrice();

        String orderNumber = order.getOrderId() != null ? order.getOrderId() : "0000";

        String html = htmlTemplate
                .replace("[[nombre]]", order.getContactInfo().getFullName())
                .replace("[[fecha]]", order.getCreatedAt().format(dateFormatter))
                .replace("[[hora]]", order.getCreatedAt().format(timeFormatter))
                .replace("[[orderNumber]]", orderNumber)
                .replace("[[total]]", "S/ " + df.format(total))
                .replace("[[direccion]]", order.getShippingAddress().getFullAddress())
                .replace("[[tabla_productos]]", tablaProductos)
                .replace("[[subtotal]]", "S/ " + df.format(subtotal))
                .replace("[[igv]]", "S/ " + df.format(igv));


        // Generar PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, null);
        builder.toStream(outputStream);
        builder.run();

        return outputStream.toByteArray();
    }
}
