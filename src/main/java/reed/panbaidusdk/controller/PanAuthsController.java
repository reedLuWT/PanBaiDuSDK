package reed.panbaidusdk.controller;

import org.bytedeco.ffmpeg.avcodec.AVCodecContext;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avutil.AVFrame;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reed.panbaidusdk.api.PanOpenApi;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pan")
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

    @GetMapping("/getPanSize")
    public String getPanSize(){
        Map<String,Object> req = new HashMap<>();
        req.put("checkfree","1");
        req.put("checkexpire","1");
        req.put("access_token",access_token);
        return panOpenApi.getPanSize(req);
    }

    @GetMapping("/getFileList")
    public String getFileList(){
        Map<String,Object> req = new HashMap<>();
        req.put("method","list");
        req.put("dir","/");
        req.put("order","time");
        req.put("start","0");
        req.put("limit","100");
        req.put("web","web");
        req.put("folder","0");
        req.put("access_token",access_token);
        return panOpenApi.getFileList(req);
    }

    @GetMapping("/getFileInfo")
    public String getFileInfo(){
        Map<String,Object> req = new HashMap<>();
        String[] str = {"957308924505794"};
        req.put("method","filemetas");
        req.put("dlink",1);
        req.put("thumb",1);
        req.put("extra",1);
        req.put("fsids","[958120764828697]");
        req.put("access_token",access_token);
        return panOpenApi.getFileInfo(req);
    }

    public static void main(String[] args) {
        try {
            URL url = new URL("");
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("GET");
//            httpConn.setRequestProperty("User-Agent", "pan.baidu.com");
//            httpConn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
//            httpConn.setRequestProperty("Host", "d.pcs.baidu.com");
            File file = new File("E:\\test");
            FileWriter fileWriter = new FileWriter(file);
            BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                fileWriter.write(inputLine);
                System.out.println(inputLine);
            }
            in.close();
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main1(String[] args) throws Exception {
        String inputFile = "path_to_input_file.mp4"; // 输入的音视频文件
        String rtmpURL = "rtmp://your_rtmp_server/live/stream_key"; // RTMP服务器地址和推流密钥

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        grabber.start();

        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(rtmpURL, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
        recorder.setInterleaved(true);

        // 设置视频编码器参数
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFormat("flv");
        recorder.setFrameRate(grabber.getFrameRate());
        recorder.setPixelFormat(grabber.getPixelFormat());
        recorder.setVideoBitrate(grabber.getVideoBitrate());

        // 设置音频编码器参数
        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
        recorder.setSampleRate(grabber.getSampleRate());
        recorder.setAudioBitrate(grabber.getAudioBitrate());
        recorder.setAudioChannels(grabber.getAudioChannels());
        recorder.setAudioQuality(0);

        recorder.start();

        Frame frame;
        while ((frame = grabber.grabFrame()) != null) {
            recorder.record(frame);
        }

        recorder.stop();
        grabber.stop();
    }

}
