package stepdefinition;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Functions {

    /*******************************************************************************
     * @param fileName - Provide File Name to find in Project Directory
     * @return - Returns the Absolute File Path if file exists in Project Directory
     *******************************************************************************/

    public String getFilePath(String fileName){
        List<Path> result;
        String FilePath = null;
        Path path = Paths.get(System.getProperty("user.dir"));
        try (Stream<Path> pathStream = Files.find(path,Integer.MAX_VALUE,
                (p,basicFileAttributes) -> p.getFileName().toString().equalsIgnoreCase(fileName))){
            result = pathStream.collect(Collectors.toList());
            if(!(result.size() == 0)){
                FilePath = result.get(0).toAbsolutePath().toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return FilePath;
    }

    /*******************************************************************************
     * @param jsonFile - Provide File Name to find in Project Directory
     * @param path - Provide Json Path to find in JSON
     * @param newValue - Provide new value to set in the JSON Node
     * @return - Returns the updated JSON
     *******************************************************************************/

    public static String replaceOldValueWithNewValueForGivenPath(File jsonFile,String path, String newValue){
        try{
            return JsonPath.parse(jsonFile).set(path,newValue).toString();
        }catch (Exception e){
            throw new RuntimeException("No results for path: "+path);
        }
    }

    /*******************************************************************************
     * @param json - Provide JSON in Test
     * @param path - Provide Json Path to find in JSON
     * @param newValue - Provide new value to set in the JSON Node
     * @return - Returns the updated JSON
     *******************************************************************************/

    public static String replaceOldValueWithNewValueForGivenJsonString(String json, String path, String newValue){
        try{
            if(path.contains("*")){
                String[] str = newValue.split(",");
                int index = path.indexOf("*");
                String key = path.substring(index+2);
                List<Map<String,String>> list = new ArrayList<>();
                for(String s : str){
                    Map<String,String> map = new HashMap<>();
                    map.put(key,s);
                    list.add(map);
                }
                DocumentContext context = JsonPath.parse(json).delete(path.substring(0,index+1));
                context.set(JsonPath.compile(path.substring(0,index-1)),list);
                return context.jsonString();
            }else {
                return JsonPath.parse(json).set(path,newValue).jsonString();
            }
        }catch (Exception e){
            throw new RuntimeException("No results for path: "+path);
        }
    }

    public static String getJsonStringFromJsonFile(File jsonFile){
        try {
            return JsonPath.parse(jsonFile).jsonString();
        }catch (Exception e){
            throw new RuntimeException("No results for file: "+jsonFile);
        }
    }

    public static String getJsonNodeDataFromJsonString(String json, String path){
        return JsonPath.parse(json).read(path);
    }

    public boolean fileExists(String fileName){
        String filePath = getFilePath(fileName);
        if(!(filePath == null)){
            File file = new File(filePath);
            return file.exists();
        }else{
            return false;
        }
    }

    public String JsonValue(File jsonFile, String path){
        try {
            return JsonPath.parse(jsonFile).read(path);
        }catch (Exception e){
            throw new RuntimeException("No results for path: "+path);
        }
    }

    public static String getStringFromResource(String resourceFilePath){
        ClassLoader classLoader = Functions.class.getClassLoader();

        InputStream resourceStream = classLoader.getResourceAsStream(resourceFilePath);
        if(resourceStream == null){
            Assert.fail("Failed to read resource document <"+resourceFilePath+">");
        }
        try {
            return IOUtils.toString(resourceStream, StandardCharsets.UTF_8);
        }catch (IOException e){
            Assert.fail("Failed to read resource document <"+resourceFilePath+">\n"+ e);
        }
        return null;
    }

    public static String amendJsonBodyItems(String body, List<Map<String,String>> table){
        for(Map<String,String> tableItems : table){
            for(Map.Entry<String,String> entry : tableItems.entrySet()){
                if(ScenarioContext.getContext(entry.getValue())==entry.getValue()){
                    body = replaceOldValueWithNewValueForGivenJsonString(body,entry.getKey(),entry.getValue());
                }else{
                    body = replaceOldValueWithNewValueForGivenJsonString(body,entry.getKey(),ScenarioContext.getContext(entry.getValue()).toString());
                }
            }
        }
        return body;
    }
}

