package qrcodeapi;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер для обработки запросов, связанных с генерацией QR-кодов.
 */
@RestController
public class QRCodeController {

    /**
     * Проверка работоспособности сервиса.
     *
     * @return HTTP-статус 200 (OK).
     */
    @GetMapping("/api/health")
    public ResponseEntity<Void> healthCheck() {
        return ResponseEntity.ok().build();
    }

    /**
     * Генерация QR-кода по заданным параметрам.
     *
     * @param contents        содержимое QR-кода (обязательно).
     * @param size            размер изображения (по умолчанию 250 пикселей).
     * @param correctionLevel уровень коррекции ошибок (по умолчанию L).
     * @param imageType       тип изображения (по умолчанию png).
     * @return QR-код в виде изображения или сообщение об ошибке.
     */
    @GetMapping(value = "/api/qrcode")
    public ResponseEntity<?> getQRCode(
            @RequestParam(value = "contents", required = true) String contents,
            @RequestParam(value = "size", defaultValue = "250") int size,
            @RequestParam(value = "correction", defaultValue = "L") String correctionLevel,
            @RequestParam(value = "type", defaultValue = "png") String imageType
    ) {
        // Проверка содержимого QR-кода
        if (contents == null || contents.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Contents cannot be null or blank"));
        }

        // Проверка размера изображения
        if (size < 150 || size > 350) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Image size must be between 150 and 350 pixels"));
        }

        // Определение уровня коррекции ошибок
        ErrorCorrectionLevel ecLevel;
        switch (correctionLevel.trim().toUpperCase()) {
            case "L":
                ecLevel = ErrorCorrectionLevel.L;
                break;
            case "M":
                ecLevel = ErrorCorrectionLevel.M;
                break;
            case "Q":
                ecLevel = ErrorCorrectionLevel.Q;
                break;
            case "H":
                ecLevel = ErrorCorrectionLevel.H;
                break;
            default:
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorMessage("Permitted error correction levels are L, M, Q, H"));
        }

        // Проверка типа изображения
        String imageTypeLower = imageType.trim().toLowerCase();
        if (!imageTypeLower.equals("png") && !imageTypeLower.equals("jpeg") && !imageTypeLower.equals("gif")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Only png, jpeg and gif image types are supported"));
        }

        // Параметры для генерации QR-кода
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ecLevel);

        BufferedImage qrImage;
        try {
            // Генерация QR-кода
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(contents, BarcodeFormat.QR_CODE, size, size, hints);
            qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorMessage("Failed to generate QR code"));
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Преобразование QR-кода в изображение
            ImageIO.write(qrImage, imageTypeLower, outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            MediaType mediaType = switch (imageTypeLower) {
                case "gif" -> MediaType.IMAGE_GIF;
                case "jpeg" -> MediaType.IMAGE_JPEG;
                default -> MediaType.IMAGE_PNG;
            };

            return ResponseEntity
                    .ok()
                    .contentType(mediaType)
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorMessage("Failed to serialize image"));
        }
    }

    /**
     * Вспомогательный класс для возврата сообщений об ошибках в формате JSON.
     */
    private static record ErrorMessage(String error) {
    }
}
