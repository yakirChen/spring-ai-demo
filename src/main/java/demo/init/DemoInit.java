package demo.init;

import demo.entity.Container;
import demo.entity.Item;
import demo.solver.HybridPackingSolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * DemoInit
 *
 * @author yakir on 2025/07/21 19:09.
 */
@Slf4j
@Component
public class DemoInit {

    @Autowired
    private HybridPackingSolver hybridPackingSolver;

//    @EventListener(ApplicationReadyEvent.class)
//    public void warmUpModel() {
//        // 生成简单案例预热模型
//        Container container = new Container("标准箱", 6, 2.4, 2.3, 10000);
//        List<Item> items = Arrays.asList(
//                new Item("box1", 1, 1, 1, 10, false, 1)
//        );
//        hybridPackingSolver.solvePackingProblem(items, List.of(container));
//        log.info("Model warm-up completed");
//    }
}
