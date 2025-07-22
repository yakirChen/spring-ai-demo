package demo.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationResult {
    private boolean valid;
    private double utilization;
    private List<String> errors = new ArrayList<>();
    private double centerOffset;

    public void addError(String error) {
        errors.add(error);
        valid = false;
    }

    public boolean isValid() {
        return errors.isEmpty();
    }
}