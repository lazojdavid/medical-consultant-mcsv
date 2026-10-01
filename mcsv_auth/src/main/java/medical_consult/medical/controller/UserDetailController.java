package medical_consult.medical.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-details")
public class UserDetailController {
    private final JdbcTemplate jdbcTemplate;

    public UserDetailController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> request) {
        Map<String, Object> row = jdbcTemplate.queryForMap(
                "INSERT INTO users_details (user_id, email, name, last_name, address) VALUES (?, ?, ?, ?, ?) RETURNING *",
                request.get("userId"), request.get("email"), request.get("name"), request.get("lastName"), request.get("address"));
        return ResponseEntity.status(HttpStatus.CREATED).body(row);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable long id, @RequestBody Map<String, Object> request) {
        int count = jdbcTemplate.update(
                "UPDATE users_details SET user_id = ?, email = ?, name = ?, last_name = ?, address = ? WHERE users_detailt_id = ?",
                request.get("userId"), request.get("email"), request.get("name"), request.get("lastName"), request.get("address"), id);
        return count == 0 ? ResponseEntity.notFound().build() : find(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable long id) {
        return find(id);
    }

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return jdbcTemplate.queryForList("SELECT * FROM users_details ORDER BY users_detailt_id");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        int count = jdbcTemplate.update("DELETE FROM users_details WHERE users_detailt_id = ?", id);
        return count == 0 ? ResponseEntity.notFound().build() : ResponseEntity.noContent().build();
    }

    private ResponseEntity<Map<String, Object>> find(long id) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM users_details WHERE users_detailt_id = ?", id);
        return rows.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(rows.get(0));
    }
}
