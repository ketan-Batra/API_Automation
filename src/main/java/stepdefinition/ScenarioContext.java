package stepdefinition;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class ScenarioContext {

    static Properties apiProperties = new Properties();

    static {
        try {
            apiProperties = initAPIProperties();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private RequestSpecification request = RestAssured.given().relaxedHTTPSValidation();

    public static HashMap<String,Object> attribute = new HashMap<>();

    private Response response;

    public static Properties initAPIProperties() throws IOException{
        String appConfigPath = System.getProperty("user.dir")+"//src//main//resources//api.properties";
        apiProperties.load(new FileInputStream(appConfigPath));
        return apiProperties;
    }

    public void url(String expression){
        if(!(response==null)){
            request = null ;
            request = RestAssured.given().relaxedHTTPSValidation();
        }
        if(!expression.contains("http")){
            expression = apiProperties.getProperty(expression);
        }
        this.request.baseUri(expression);
    }

    public void path(String basePath){
        this.request.basePath(basePath);
    }

    public void queryParam(String paramKey, Object paramValue){
        this.request.queryParam(paramKey,paramValue);
    }

    public void requestBody(String body){
        setContext("request",body);
        this.request.body(body);
    }

    public void requestBody(File body){
        setContext("request",body);
        this.request.body(body);
    }

    public void status (int status){
        Assert.assertEquals(status, this.response.getStatusCode());
    }

    public void header(String name, List<String> values){
        this.request.header(name, values);
    }

    public void header(String name, String value){

        if(apiProperties.getProperty(value)!=null){
            value = apiProperties.getProperty(value);
            this.request.header(name,value);
        }else if(attribute.containsKey(value)){
            this.request.header(name,"Bearer" +getContext(value));
        }
        else{
            this.request.header(name,getContext(value));
        }
    }

    public void headers(HashMap<String,String> keyValue){
        this.request.headers(keyValue);
    }

    public void get(){
        this.request.log().all();
        this.response = this.request.get();
        response.prettyPrint();
    }

    public void put(){
        this.request.log().all();
        this.response = this.request.put();
    }

    public void post(){
        this.request.log().all();
        this.response = this.request.post();
        setContext("response",response.getBody().asString());
        response.prettyPrint();
    }

    public void delete(){
        this.response = this.request.delete();
    }

    public void patch(){
        this.response = this.request.patch();
    }

    public void assertTrue(String expression){
        String[] eval = expression.split("==");
        String actual = this.response.getBody().jsonPath().get(eval[0].trim());
        Assert.assertTrue("Not Matching", eval[1].trim().equalsIgnoreCase(actual));
    }

    public void assertTrueXML(String expression){
        String[] eval = expression.split("==");
        String actual = this.response.getBody().xmlPath().get(eval[0].trim());
        Assert.assertTrue("Not Matching", eval[1].trim().equalsIgnoreCase(actual));
    }

    public static void setContext(String key, Object value){
        attribute.put(key,value);
    }

    public static Object getContext(String key){
        return attribute.getOrDefault(key,key);
    }
}
