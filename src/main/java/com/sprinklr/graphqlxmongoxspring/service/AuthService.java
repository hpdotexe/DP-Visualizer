package com.sprinklr.graphqlxmongoxspring.service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.sprinklr.graphqlxmongoxspring.model.Constants.*;

public class AuthService {

    public static final Map<String, Map<String,String>> sessionInfo = new HashMap<>();
    private static final Logger logger = Logger.getLogger("AppController.class");

    public static Map<String,String> getAccessToken(String authCode,String redirect_uri) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://login.microsoftonline.com/"+TENANT_ID+"/oauth2/v2.0/token"))
                .header("content-type", "multipart/form-data; boundary=---FIELD")
                .method("POST",
                        HttpRequest.BodyPublishers.ofString(
                                "-----FIELD\r\nContent-Disposition: form-data; name=\"code\"\r\n\r\n"+authCode+"\r\n" +
                                        "-----FIELD\r\nContent-Disposition: form-data; name=\"grant_type\"\r\n\r\n" + "authorization_code\r\n" +
                                        "-----FIELD\r\nContent-Disposition: form-data; name=\"redirect_uri\"\r\n\r\n"+redirect_uri+"\r\n" +
                                        "-----FIELD\r\nContent-Disposition: form-data; name=\"client_id\"\r\n\r\n"+CLIENT_ID+"\r\n" +
                                        "-----FIELD\r\nContent-Disposition: form-data; name=\"client_secret\"\r\n\r\n"+CLIENT_SECRET+"\r\n" +
                                        "-----FIELD\r\nContent-Disposition: form-data; name=\"scope\"\r\n\r\n"+APP_SCOPE+"\r\n" +
                                        "-----FIELD--\r\n"))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject tokens = new JSONObject(response.body());
        if(!tokens.has("access_token")){
            logger.log(Level.INFO,"Error in getting Access Token : "+tokens);
            Map<String,String> error = new HashMap<>();
            error.put("success","false");
            return error;
        }
        return extractJWT(tokens.get("access_token").toString());
    }

    private static Map<String,String> extractJWT(String accessToken){
        Map<String,String> userInfo = new HashMap<>();
        String[] chunks = accessToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        JSONObject payload = new JSONObject(new String(decoder.decode(chunks[1])));

        fillUserInfo(accessToken, userInfo, payload);
        fillPermissionInfo(userInfo, payload);

        System.out.println("Access token: " + accessToken);
        sessionInfo.put(accessToken, userInfo);
        return userInfo;
    }

    private static void fillPermissionInfo(Map<String, String> userInfo, JSONObject payload) {
        if (payload.has("groups")) {
            String groups = payload.get("groups").toString();
            List<String> groupIDs = Arrays.asList(groups.substring(1, groups.length() - 1).split(","));
            if (groupIDs.contains("\"" + READWRITE_GROUP_OID_AAD + "\"")) {
                userInfo.put("permission", "ReadWrite");
            } else if (groupIDs.contains("\"" + READ_GROUP_OID_AAD + "\"")) {
                userInfo.put("permission", "Read");
            } else userInfo.put("permission", "Blocked");
        } else userInfo.put("permission", "Blocked");
    }

    private static void fillUserInfo(String accessToken, Map<String, String> userInfo, JSONObject payload) {
        userInfo.put("name", payload.get("name").toString());
        if (payload.has("upn")) userInfo.put("upn", payload.get("upn").toString());
        else if (payload.has("email")) userInfo.put("upn", payload.get("email").toString());
        else userInfo.put("upn", payload.get("oid").toString());
        userInfo.put("token", accessToken);
        userInfo.put("success", "true");
        userInfo.put("appid", payload.get("appid").toString());
        userInfo.put("exp", payload.get("exp").toString());
    }

    public static boolean validateToken(String token) {
        if(sessionInfo.containsKey(token)) {
            if(!isSessionActive(token)){
                sessionInfo.remove(token);
                return false;
            }
            return true;
        }

        DecodedJWT jwt = JWT.decode(token);
        System.out.println(jwt.getKeyId());
        JwkProvider provider = null;
        Jwk jwk =null;
        Algorithm algorithm=null;
        try {

            provider = new UrlJwkProvider(new URL("https://login.microsoftonline.com/common/discovery/v2.0/keys"));
            jwk = provider.get(jwt.getKeyId());
            algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            algorithm.verify(jwt);// if the token signature is invalid, the method will throw SignatureVerificationException
            System.out.println("Token Validated!");
            return Objects.equals(extractJWT(token).get("appid"), dotenv.get("CLIENT.ID"));

        } catch (MalformedURLException | JwkException e) {
            e.printStackTrace();
        } catch(SignatureVerificationException e){
            System.out.println(e.getMessage());
        }
        System.out.println("Token Invalid!");
        return false;
    }

    public static boolean hasEditAccess(String token) {
        return Objects.equals(sessionInfo.get(token).get("permission"), "ReadWrite");
    }

    public static boolean hasViewAccess(String token) {
        return Objects.equals(sessionInfo.get(token).get("permission"), "ReadWrite") ||
                Objects.equals(sessionInfo.get(token).get("permission"), "Read");
    }

    public static boolean isSessionActive(String token) {
        return Double.parseDouble(sessionInfo.get(token).get("exp"))<=(double)System.currentTimeMillis();
    }
}
