import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TechnicalTask {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String fileName = "example.xml";
        JsonNode jsonNode = converter(fileName);
        String signature = sha1(jsonNode.toString());
        ((ObjectNode) jsonNode).put("signature", signature);
        request(jsonNode);
    }

    private static void request(JsonNode jsonNode) {
        RestAssured.baseURI = "http://google.com.ua";
        String request = RestAssured.given().
                header("Content-Type", ContentType.JSON).
                body(jsonNode).
                when().post("/queryBatch.cfm")
                .then().extract().asString();
    }

    public static JsonNode converter(String fileName) {
        String data = "";
        JsonNode jsonNode = null;
        try {
            data = FileUtils.readFileToString(new File(fileName), "UTF-8");
            XmlMapper xmlMapper = new XmlMapper();
            jsonNode = xmlMapper.readTree(data.getBytes());
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonNode;
    }

    static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}