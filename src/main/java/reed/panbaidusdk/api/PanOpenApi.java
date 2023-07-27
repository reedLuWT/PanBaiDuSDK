package reed.panbaidusdk.api;

import org.springframework.stereotype.Component;
import reed.panbaidusdk.annotation.OpenApi;

import java.io.InputStream;
import java.util.Map;

public interface PanOpenApi {

    @OpenApi("https://pan.baidu.com/rest/2.0/xpan/nas")
    String getPanUserInfo(Map<String,Object> req);

    @OpenApi("https://openapi.baidu.com/oauth/2.0/token")
    String getAccessToken(Map<String,Object> req);

    @OpenApi("https://pan.baidu.com/api/quota")
    String getPanSize(Map<String,Object> req);

    @OpenApi("https://pan.baidu.com/rest/2.0/xpan/file")
    String getFileList(Map<String,Object> req);

    @OpenApi("http://pan.baidu.com/rest/2.0/xpan/multimedia")
    String getFileInfo(Map<String,Object> req);

    @OpenApi("")
    InputStream getFile();
}
