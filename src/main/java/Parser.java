import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;



public class Parser {
    private static final Logger log = LoggerFactory.getLogger(Parser.class);

    public void parse(String directoryPath, boolean flag){
        File dir = new File(directoryPath);
        if (!dir.isDirectory()) {
            log.info("Path isn't directory");
            return;
        }
        File[] files = dir.listFiles(new Filter());
        for (File file : files) {
            List<String> data;
            try {
                data = Files.lines(file.toPath()).flatMap(s -> Arrays.stream(s.split("\u0001"))).collect(Collectors.toList());
            } catch (IOException e) {
                log.error("Parse information error");
                return;
            }
            Map<String, String> uniqueTagMap = new HashMap<>();
            for (String datum : data) {
                int i = datum.indexOf('=');
                uniqueTagMap.put(datum.substring(0, i), datum.substring(i + 1));
            }
            String CSVFileName = file.getName().substring(0, file.getName().lastIndexOf('.')) + ".csv";
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(CSVFileName), ';');
                String[] tags = uniqueTagMap.keySet().toArray(new String[0]);
                String[] values = uniqueTagMap.entrySet().stream().map(entry -> entry.getValue()).toArray(String[]::new);
                writer.writeNext(tags);
                writer.writeNext(values);
                writer.close();
            } catch (IOException e) {
                log.error("Error writing to file");
                return;
            }
            Map<String, Integer> statisticMap = new HashMap<>();
            if (flag) {
                log.info(CSVFileName + " enable statistics");
                for (String datum : data) {
                    String key = datum.substring(0, datum.indexOf('='));
                    Integer count = statisticMap.get(key);
                    statisticMap.put(key, (count != null) ? count + 1 : 1);
                }
            }
            for (Map.Entry<String, Integer> entry : statisticMap.entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue());
            }
        }


    }

    class Filter implements FileFilter {
        String ext = "data";


        private String getExtension(File pathname) {
            String filename = pathname.getPath();
            int i = filename.lastIndexOf('.');
            if ((i > 0) && (i < filename.length() - 1)) {
                return filename.substring(i + 1).toLowerCase();
            } else {
                return "";
            }
        }

        @Override
        public boolean accept(File pathname) {
            if (!pathname.isFile()) {
                return false;
            }
            String extension = getExtension(pathname);
            if (ext.equalsIgnoreCase(extension)) {
                return true;
            }

            return false;
        }
    }
}
