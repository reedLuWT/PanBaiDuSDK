package reed.panbaidusdk.api;

import org.springframework.stereotype.Component;
import reed.panbaidusdk.annotation.OpenApi;

import java.util.Map;

public interface PanOpenApi {

    @OpenApi("https://pan.baidu.com/rest/2.0/xpan/nas")
    String getPanUserInfo(Map<String,Object> req);

    @OpenApi("https://openapi.baidu.com/oauth/2.0/token")
    String getAccessToken(Map<String,Object> req);
}
