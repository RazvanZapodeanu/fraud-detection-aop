package service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {

    private static final Path FILE_PATH = Paths.get("audit.csv");
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static AuditService instance;

    private AuditService() {
        ensureHeader();
    }

    public static synchronized AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }

    public void log(String actionName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH.toFile(), true))) {
            writer.println(actionName + "," + LocalDateTime.now().format(TIMESTAMP_FORMAT));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write audit log", e);
        }
    }

    private void ensureHeader() {
        if (Files.exists(FILE_PATH)) return;
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH.toFile()))) {
            writer.println("action_name,timestamp");
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize audit log", e);
        }
    }
}