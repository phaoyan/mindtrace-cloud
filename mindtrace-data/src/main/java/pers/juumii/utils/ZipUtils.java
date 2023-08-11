package pers.juumii.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {

    public static Map<String, byte[]> zipToMap(byte[] zipData) throws IOException {
        Map<String, byte[]> fileMap = new HashMap<>();

        // 创建一个ByteArrayInputStream和一个ZipInputStream
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(zipData);
        ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream);

        // 读取ZIP文件中的每个条目
        ZipEntry zipEntry;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // 将条目的内容读取到ByteArrayOutputStream中
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            // 将文件名和文件内容添加到Map中
            fileMap.put(zipEntry.getName(), byteArrayOutputStream.toByteArray());

            // 关闭当前条目
            zipInputStream.closeEntry();
            byteArrayOutputStream.close();
        }

        // 关闭输入流
        zipInputStream.close();
        byteArrayInputStream.close();

        return fileMap;
    }
}
