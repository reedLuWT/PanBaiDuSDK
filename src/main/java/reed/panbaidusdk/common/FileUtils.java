package reed.panbaidusdk.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class FileUtils {
    public static void saveToFile(InputStream inputStream, File outputFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             FileChannel outChannel = fos.getChannel();
             ReadableByteChannel inChannel = Channels.newChannel(inputStream)) {

            // 使用一个合适大小的缓冲区（例如，8KB）来提高性能
            ByteBuffer buffer = ByteBuffer.allocateDirect(8 * 1024);

            // 将输入流数据写入输出文件
            while (inChannel.read(buffer) != -1) {
                buffer.flip(); // 切换为读模式
                outChannel.write(buffer);
                buffer.clear(); // 清空缓冲区以便下次读取
            }
        }
    }
}
