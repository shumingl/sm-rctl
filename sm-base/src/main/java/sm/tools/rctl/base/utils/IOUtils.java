package sm.tools.rctl.base.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class IOUtils {

    /**
     * 接收固定长度数据
     *
     * @param inputStream 输入流
     * @param buffer      数据缓存
     * @throws IOException
     */
    public static int readBytes(InputStream inputStream, byte[] buffer) throws IOException {
        if (buffer == null)
            return -1;
        return readBytes(inputStream, buffer, 0, buffer.length);
    }

    /**
     * 接收固定长度数据
     *
     * @param inputStream 输入流
     * @param buffer      数据缓存
     * @throws IOException
     */
    public static void readFixedBytes(InputStream inputStream, byte[] buffer) throws IOException {
        readFixedBytes(inputStream, buffer, 0, buffer.length);
    }

    /**
     * 接收固定长度数据，超时则异常
     *
     * @param inputStream 输入流
     * @param buffer      数据缓存
     * @param timeout     超时时间
     * @throws IOException
     */
    public static void readFixedBytes(InputStream inputStream, byte[] buffer, long timeout) throws IOException {
        readFixedBytes(inputStream, buffer, 0, buffer.length, timeout);
    }

    /**
     * 接收固定长度数据
     *
     * @param inputStream 输入流
     * @param buffer      数据缓存
     * @param offset      起始位置（偏移量）
     * @throws IOException
     */
    public static int readBytes(InputStream inputStream, int offset, byte[] buffer) throws IOException {
        if (buffer == null)
            return -1;
        return readBytes(inputStream, buffer, offset, buffer.length);
    }

    /**
     * 接收固定长度数据
     *
     * @param inputStream 输入流
     * @param buffer      数据缓存
     * @param offset      起始位置（偏移量）
     * @throws IOException
     */
    public static void readFixedBytes(InputStream inputStream, int offset, byte[] buffer) throws IOException {
        readFixedBytes(inputStream, buffer, offset, buffer.length);
    }

    /**
     * 接收固定长度数据，超时则异常
     *
     * @param inputStream 输入流
     * @param buffer      数据缓存
     * @param offset      起始位置（偏移量）
     * @param timeout     超时时间
     * @throws IOException
     */
    public static void readFixedBytes(InputStream inputStream, byte[] buffer, int offset, long timeout) throws IOException {
        readFixedBytes(inputStream, buffer, offset, buffer.length, timeout);
    }

    /**
     * 接收固定长度数据
     *
     * @param inputStream 输入流
     * @param buffer      数据缓存
     * @param offset      起始位置（偏移量）
     * @param length      要读取的长度
     * @throws IOException
     */
    public static int readBytes(InputStream inputStream, byte[] buffer, int offset, int length) throws IOException {
        if (buffer == null)
            return -1;
        int total = length + offset;
        int finished = offset;
        int ret;
        while (finished < total) {
            ret = inputStream.read(buffer, finished, total - finished);
            if (ret != -1)
                finished += ret;
            else
                break;
        }
        return finished;
    }

    /**
     * 接收固定长度数据
     *
     * @param inputStream 输入流
     * @param buffer      数据缓存
     * @param offset      起始位置（偏移量）
     * @param length      要读取的长度
     * @throws IOException
     */
    public static void readFixedBytes(InputStream inputStream, byte[] buffer, int offset, int length) throws IOException {
        if (buffer == null)
            return;
        int total = length + offset;
        int finished = offset;
        int ret;
        while (finished < total) {
            ret = inputStream.read(buffer, finished, total - finished);
            if (ret > -1)
                finished += ret;
        }
    }

    /**
     * 接收固定长度数据，超时则异常
     *
     * @param inputStream 输入流
     * @param buffer      数据缓存
     * @param offset      起始位置（偏移量）
     * @param length      要读取的长度
     * @param timeout     超时时间
     * @throws IOException
     */
    public static void readFixedBytes(InputStream inputStream, byte[] buffer, int offset, int length, long timeout)
            throws IOException {
        long start = System.currentTimeMillis();
        int total = length + offset;
        int finished = offset;
        int ret;
        while (finished < total) {
            ret = inputStream.read(buffer, finished, total - finished);
            if (ret > -1)
                finished += ret;
            else {
                if (System.currentTimeMillis() - start > timeout)
                    throw new IOException(String.format("data read timeout (%d ms).", timeout));
            }
        }
    }

    /**
     * 发送数据
     *
     * @param outputStream 输出流
     * @param buffer       数据内容
     * @throws IOException
     */
    public static void writeBytes(OutputStream outputStream, byte[] buffer) throws IOException {
        outputStream.write(buffer);
        outputStream.flush();
    }

    public static List<String> readLines(BufferedReader reader, int lineLimit) throws IOException {
        List<String> result = new ArrayList<>(lineLimit);
        for (int i = 0; i < lineLimit; i++) {
            String line = reader.readLine();
            if (line != null)
                result.add(line);
            else
                break;
        }
        return result;
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
        }
    }
}
