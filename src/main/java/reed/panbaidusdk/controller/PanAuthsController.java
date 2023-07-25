package reed.panbaidusdk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reed.panbaidusdk.api.PanOpenApi;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auths")
public class PanAuthsController {
    @Autowired
    PanOpenApi panOpenApi;

    @Value("${pan.baidu.client_id}")
    String client_id;
    @Value("${pan.baidu.client_secret}")
    String client_secret;
    @Value("${pan.baidu.access_token}")
    String access_token;


    @GetMapping("/getPanAccessToken")
    public String getPanAccessToken(String code){
        Map<String,Object> req = new HashMap<>();
        req.put("grant_type","authorization_code");
        req.put("code",code);
        req.put("client_id",client_id);
        req.put("client_secret",client_secret);
        req.put("redirect_uri","oob");
        return panOpenApi.getAccessToken(req);
    }

    @GetMapping("/getUserInfo")
    public String getUserInfo(){
        Map<String,Object> req = new HashMap<>();
        req.put("method","uinfo");
        req.put("access_token",access_token);
        return panOpenApi.getPanUserInfo(req);
    }

}
