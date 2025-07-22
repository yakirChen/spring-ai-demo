package demo;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;

/**
 * DemoApp
 *
 * @author yakir on 2025/07/21 19:31.
 */
@SpringBootApplication(
        scanBasePackageClasses = {
                DemoApp.class
        }
)
public class DemoApp {

    public static void main(String[] args) {

        new SpringApplicationBuilder(DemoApp.class)
                .web(WebApplicationType.SERVLET)
                .listeners(
                        new ApplicationPidFileWriter("DemoApp.pid")
                )
                .run(args);

    }
}
