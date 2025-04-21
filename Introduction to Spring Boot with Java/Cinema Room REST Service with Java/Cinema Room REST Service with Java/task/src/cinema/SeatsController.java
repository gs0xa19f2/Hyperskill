package cinema;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class SeatsController {

    private final int rows = 9;
    private final int columns = 9;
    private final Set<String> bookedSeats = new HashSet<>();
    private final Map<String, Map<String, Object>> purchasedTickets = new HashMap<>();
    private int totalIncome = 0;

    @GetMapping("/seats")
    public Map<String, Object> getSeats() {
        List<Map<String, Object>> seats = new ArrayList<>();
        for (int row = 1; row <= rows; row++) {
            for (int column = 1; column <= columns; column++) {
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
        response.put("rows", rows);
        response.put("columns", columns);
        response.put("seats", seats);

        return response;
    }

    @PostMapping("/purchase")
    public ResponseEntity<Object> purchaseTicket(@RequestBody Map<String, Integer> request) {
        int row = request.getOrDefault("row", -1);
        int column = request.getOrDefault("column", -1);

        if (row < 1 || row > rows || column < 1 || column > columns) {
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

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam(required = false) String password) {
        if (!"super_secret".equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "The password is wrong!"));
        }

        int availableSeats = rows * columns - bookedSeats.size();
        int purchasedTicketsCount = purchasedTickets.size();

        Map<String, Object> stats = Map.of(
                "income", totalIncome,
                "available", availableSeats,
                "purchased", purchasedTicketsCount
        );

        return ResponseEntity.ok(stats);
    }

    private boolean isSeatBooked(int row, int column) {
        return bookedSeats.contains(row + "-" + column);
    }
}