package cinema;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Контроллер для управления местами в кинозале.
 */
@RestController
public class SeatsController {

    private static final int ROWS = 9;
    private static final int COLUMNS = 9;
    private final Set<String> bookedSeats = new HashSet<>();
    private final Map<String, Map<String, Object>> purchasedTickets = new HashMap<>();
    private int totalIncome = 0;

    /**
     * Возвращает список доступных мест.
     *
     * @return JSON-объект с информацией о местах.
     */
    @GetMapping("/seats")
    public Map<String, Object> getSeats() {
        List<Map<String, Object>> seats = new ArrayList<>();
        for (int row = 1; row <= ROWS; row++) {
            for (int column = 1; column <= COLUMNS; column++) {
                if (!isSeatBooked(row, column)) {
                    Map<String, Object> seat = new HashMap<>();
                    seat.put("row", row);
                    seat.put("column", column);
                    seat.put("price", row <= 4 ? 10 : 8);
                    seats.add(seat);
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("rows", ROWS);
        response.put("columns", COLUMNS);
        response.put("seats", seats);

        return response;
    }

    /**
     * Покупка билета.
     *
     * @param request JSON-объект с запросом на покупку.
     * @return JSON-объект с информацией о купленном билете или ошибкой.
     */
    @PostMapping("/purchase")
    public ResponseEntity<Object> purchaseTicket(@RequestBody Map<String, Integer> request) {
        int row = request.getOrDefault("row", -1);
        int column = request.getOrDefault("column", -1);

        if (row < 1 || row > ROWS || column < 1 || column > COLUMNS) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "The number of a row or a column is out of bounds!"));
        }

        String seatKey = row + "-" + column;
        if (bookedSeats.contains(seatKey)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "The ticket has been already purchased!"));
        }

        bookedSeats.add(seatKey);
        int price = row <= 4 ? 10 : 8;

        String token = UUID.randomUUID().toString();
        Map<String, Object> ticketDetails = Map.of(
                "row", row,
                "column", column,
                "price", price
        );
        purchasedTickets.put(token, ticketDetails);

        totalIncome += price;

        return ResponseEntity.ok(Map.of(
                "token", token,
                "ticket", ticketDetails
        ));
    }

    /**
     * Возврат билета.
     *
     * @param request JSON-объект с токеном билета.
     * @return JSON-объект с информацией о возвращенном билете или ошибкой.
     */
    @PostMapping("/return")
    public ResponseEntity<Object> returnTicket(@RequestBody Map<String, String> request) {
        String token = request.get("token");

        if (!purchasedTickets.containsKey(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Wrong token!"));
        }

        Map<String, Object> ticketDetails = purchasedTickets.remove(token);
        int row = (int) ticketDetails.get("row");
        int column = (int) ticketDetails.get("column");
        int price = (int) ticketDetails.get("price");
        bookedSeats.remove(row + "-" + column);

        totalIncome -= price;

        return ResponseEntity.ok(Map.of("ticket", ticketDetails));
    }

    /**
     * Получение статистики.
     *
     * @param password пароль для доступа к статистике.
     * @return JSON-объект с информацией о статистике или ошибкой.
     */
    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam(required = false) String password) {
        if (!"super_secret".equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "The password is wrong!"));
        }

        int availableSeats = ROWS * COLUMNS - bookedSeats.size();
        int purchasedTicketsCount = purchasedTickets.size();

        Map<String, Object> stats = Map.of(
                "income", totalIncome,
                "available", availableSeats,
                "purchased", purchasedTicketsCount
        );

        return ResponseEntity.ok(stats);
    }

    /**
     * Проверяет, забронировано ли место.
     *
     * @param row номер ряда.
     * @param column номер колонки.
     * @return true, если место забронировано, иначе false.
     */
    private boolean isSeatBooked(int row, int column) {
        return bookedSeats.contains(row + "-" + column);
    }
}
