import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/features"},
        glue = {"stepdefinition"},
        plugin = {"json:target/cucumber.json","html:target/cucumber-html/Report.html","pretty"},
        tags = "@sample"
        )
public class Runner {

}
