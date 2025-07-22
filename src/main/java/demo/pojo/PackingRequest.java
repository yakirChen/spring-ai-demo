package demo.pojo;

import demo.entity.Container;
import demo.entity.Item;
import lombok.Data;

import java.util.List;

// 请求DTO
@Data
public class PackingRequest {
    private Container container;
    private List<Item> items;
    private Constraints constraints;
}