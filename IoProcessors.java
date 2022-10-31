package Home_Tasks.HT_11;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class IoProcessors {

    public static final Map<Predicate<Path>, BiConsumer<Path, PrintWriter>> PROCESSORS = Map.of(

            path -> Files.notExists(path),
            (path, writer) -> {
                writer.println("HTTP/1.1 404 OK");
                writer.println("Content-Type: text/html; charset=utf-8");
                writer.println();
                writer.println("<html>");
                String linkPath = Server.getWWW().relativize(path).toString();
                writer.println("<h1>File " + linkPath + "not found</h1>");
                writer.println("</html>");
            },

            path -> Files.isDirectory(path),
            (path, writer) -> {
                writer.println("HTTP/1.1 200 OK");
                writer.println("Content-Type: text/html; charset=utf-8");
                writer.println();
                writer.println("<html>");
                writer.println("<h1>It's directory</h1>");
                File[] files = new File(path.toString()).listFiles();
                Path www = Server.getWWW();
                String inetAddress = Server.getInetAddress();
                writer.println("<ol>");
                for (File fl : files) {
                    String linkPath = www.relativize(fl.toPath()).toString();
                    writer.println("<li><a href=" + inetAddress + linkPath + ">" + fl.getName() + "</a></li>");
                }
                writer.println("</ol>");
                writer.println("</html>");
            },

            path -> !Files.isReadable(path),
            (path, writer) -> {
                writer.println("HTTP/1.1 403 OK");
                writer.println("Content-Type: text/html; charset=utf-8");
                writer.println();
                writer.println("<html>");
                writer.println("<h1>File not readable</h1>");
                writer.println("</html>");
            },

            path -> Files.isRegularFile(path),
            (path, writer) -> {
                try {
                    writer.println("HTTP/1.1 200 OK");
                    writer.println("Content-Type: text/html; charset=utf-8");
                    writer.println();
                    writer.println("<html>");
                    Files.newBufferedReader(path).transferTo(writer);
                    writer.println("</html>");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
    );
}