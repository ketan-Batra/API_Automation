package stepdefinition;

import io.cucumber.java.en.When;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilStepActions {

    public final ScenarioContext context;

    Functions functions = new Functions();

    public UtilStepActions(ScenarioContext context){
        this.context = context;
    }

    @When("^url (.+)")
    public void url(String expression){
        if (expression.contains("\"")){
            expression = expression.replace("\"","");
        }
        this.context.url(expression);
    }

    @When("^set path (.+)")
    public void path(String path){
        this.context.path(path);
    }

    @When("^set query param ([^\\s]+) = (.+)")
    public void queryParam(String paramKey, Object paramValue){
        this.context.queryParam(paramKey,getVariable(paramValue.toString()));
    }

    @When("^set header ([^\\s]+) = (.+)")
    public void header(String name, String value){
        this.context.header(name,value);
    }

    @When("^set headers (.+)")
    public void headers(HashMap<String,String> map){
        this.context.headers(map);
    }

    @When("^set request as (.+)")
    public void request(String body){
        if(body.contains(".json") && functions.fileExists(body)){
            this.context.requestBody(Functions.getJsonStringFromJsonFile(new File(functions.getFilePath(body))));
        }else{
            this.context.requestBody(body);
        }
    }

    @When("^set request xml as (.+)")
    public void requestXML(String body){
        if(body.contains(".xml") && functions.fileExists(body)){
            this.context.requestBody(new File(functions.getFilePath(body)));
        }else{
            this.context.requestBody(body);
        }
    }

    @When("^set request (.+) and values")
    public void request(String body, List<Map<String,String>> table){
        if(body.contains(".xml") && functions.fileExists(body)){
            if(table.size()==0){
                this.context.requestBody(Functions.getJsonStringFromJsonFile(new File(functions.getFilePath(body))));
            }else{
                this.context.requestBody(Functions.amendJsonBodyItems(Functions.getJsonStringFromJsonFile(new File(functions.getFilePath(body))),table));
            }
        }else{
            if(table.size()==0){
            this.context.requestBody(body);
        }else{
                this.context.requestBody(Functions.amendJsonBodyItems(body,table));
            }
        }
    }

    @When("^assert status code (\\d+)")
    public void status(int status){
        this.context.status(status);
    }

    @When("^send method (\\w+)")
    public void method(String method){
        switch (method.toLowerCase()) {
            case "get" -> this.context.get();
            case "post" -> this.context.post();
            case "put" -> this.context.put();
            case "delete" -> this.context.delete();
            case "patch" -> this.context.patch();
        }
    }

    @When("^assert field (.+)")
    public void assertTrue(String expression){
        this.context.assertTrue(expression);
    }

    @When("^assert xml field (.+)")
    public void assertTrueXML(String expression){
        this.context.assertTrueXML(expression);
    }

    @When("^set (.+) to (.+)")
    public void setVariable(String key, String val){
        ScenarioContext.setContext(key,val);
    }

    @When("^get (.+) value")
    public String getVariable(String key){
        return ScenarioContext.getContext(key).toString();
    }

    @When("^update request with c(.+) and values")
    public void updateRequest(String key,List<Map<String,String>> table){
        request(Functions.amendJsonBodyItems(Functions.replaceOldValueWithNewValueForGivenJsonString(getVariable("request"),key,getVariable(key)),table));
    }

    @When("^update request with value (.+)")
    public void updateRequest(String key){
        request(Functions.replaceOldValueWithNewValueForGivenJsonString(getVariable("request"),key,getVariable(key)));
    }
}
