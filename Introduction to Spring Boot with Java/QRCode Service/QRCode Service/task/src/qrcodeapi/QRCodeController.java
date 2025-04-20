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

@RestController
public class QRCodeController {

    @GetMapping("/api/health")
    public ResponseEntity<Void> healthCheck() {
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/qrcode
     * Mandatory: contents
     * Optional: size (default 250), correction (default L), type (default png)
     * <p>
     * Priority of error checks:
     *  1) Invalid contents
     *  2) Invalid size
     *  3) Invalid correction
     *  4) Invalid type
     */
    @GetMapping(value = "/api/qrcode")
    public ResponseEntity<?> getQRCode(
            @RequestParam(value = "contents") String contents,
            @RequestParam(value = "size", defaultValue = "250") int size,
            @RequestParam(value = "correction", defaultValue = "L") String correctionLevel,
            @RequestParam(value = "type", defaultValue = "png") String imageType
    ) {

        if (contents == null || contents.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Contents cannot be null or blank"));
        }

        if (size < 150 || size > 350) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Image size must be between 150 and 350 pixels"));
        }

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

        String lowerType = imageType.trim().toLowerCase();
        if (!lowerType.equals("png") && !lowerType.equals("jpeg") && !lowerType.equals("gif")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Only png, jpeg and gif image types are supported"));
        }

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ecLevel);

        BufferedImage qrImage;
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(contents, BarcodeFormat.QR_CODE, size, size, hints);
            qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorMessage("Failed to generate QR code"));
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(qrImage, lowerType, baos);
            byte[] imageBytes = baos.toByteArray();

            MediaType mediaType = switch (lowerType) {
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
         * Helper class to return JSON errors.
         */
        private record ErrorMessage(String error) {
    }
}