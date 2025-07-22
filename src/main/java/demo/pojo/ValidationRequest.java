package demo.pojo;

import demo.entity.Container;
import demo.entity.Item;
import demo.entity.PackingSolution;
import lombok.Data;

import java.util.List;

@Data
public class ValidationRequest {
    private Container container;
    private List<Item> items;
    private Constraints constraints;
    private PackingSolution solution;
}