package commands;

import org.jline.reader.LineReader;
import java.io.*;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class WriteDeduction implements Command {
    private final LineReader reader;

    public WriteDeduction(LineReader reader) {
        this.reader = reader;
    }

    @Override
    public void execute(String arg) {
        try {
            System.out.println("Заполнение ПСЖ:");

            String fullName = askInputWithValidation("Введите ФИО полностью (например, Март Скуберт Олегович): ",
                    "^[А-ЯЁ][а-яё]+(?:\\s[А-ЯЁ][а-яё]+){2}$",
                    "Некорректный ввод имени!");
            String groupNumber = askInputWithValidation("Введите вашу группу (например, P3132): ",
                    "^[A-Z]\\d{4}$",
                    "Некорректный ввод группы!");
            String faculty = askInputWithValidation("Введите название факультета (например, КТ): ",
                    "^[А-ЯЁа-яё\\-\\s]+$",
                    "Некорректный ввод названия факультета!");
            String phone = askInputWithValidation("Введите контактный телефон (например, +79313134585): ",
                    "^\\+7\\d{10}$",
                    "Некорректный ввод номера телефона!");
            String email = askInputWithValidation("Введите email (например, example@yandex.ru): ",
                    "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                    "Некорректный ввод email!");

            String texContent = new DeductionCreating(fullName, groupNumber, faculty, phone, email).getTexFile();
            String currentDir = System.getProperty("user.dir");

            String timestamp = java.time.LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String texFileName = "application_" + timestamp + ".tex";

            try {
                Path texFilePath = Paths.get(currentDir, texFileName);
                Files.writeString(texFilePath, texContent, java.nio.charset.StandardCharsets.UTF_8);

                // Проверяем установлен ли pdflatex
                if (!isLaTeXInstalled()) {
                    System.out.println("\u001B[31mОшибка\u001B[0m: pdflatex не найден!");
                    return;
                }
                
                compileToPdf(currentDir, texFilePath.toString());

            } catch (AccessDeniedException e) {
                System.err.println("\u001B[31mОшибка\u001B[0m: Нет прав на запись в директорию " + currentDir);
            } catch (IOException e) {
                System.err.println("\u001B[31mОшибка\u001B[0m записи файла: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("\u001B[31mОшибка\u001B[0m: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isLaTeXInstalled() {
        try {
            Process process = Runtime.getRuntime().exec("which pdflatex");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );
            String path = reader.readLine();
            process.waitFor();
            return path != null && !path.trim().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private String askInputWithValidation(String prompt, String regex, String errorMessage) {
        Pattern pattern = Pattern.compile(regex);

        while (true) {
            String input = reader.readLine(prompt).trim();

            if (input.isEmpty()) {
                System.out.println("Поле не может быть пустым!");
                continue;
            }

            if (pattern.matcher(input).matches()) {
                return input;
            } else {
                System.out.println("\u001B[31mОшибка\u001B[0m: " + errorMessage);
            }
        }
    }

    private void compileToPdf(String outputDir, String texFilePath) {
        try {
            // Просто запускаем pdflatex для файла
            String fileName = new File(texFilePath).getName();

            // Меняем текущую директорию
            Process process = Runtime.getRuntime().exec("pdflatex " + fileName, null, new File(outputDir));

            // Просто ждем завершения
            int result = process.waitFor();

            if (result == 0) {

                // Удаляем служебные файлы
                String baseName = fileName.replace(".tex", "");
                String[] extensions = {".aux", ".log", ".out"};
                for (String ext : extensions) {
                    Path tempFile = Path.of(outputDir, baseName + ext);
                    Files.deleteIfExists(tempFile);
                }

                // Удаляем tex файл
                Files.deleteIfExists(Paths.get(texFilePath));

                // Переименовываем PDF в ПСЖ.pdf
                Path sourcePdf = Path.of(outputDir, baseName + ".pdf");
                Path targetPdf = Path.of(outputDir, "ПСЖ.pdf");
                Files.move(sourcePdf, targetPdf, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Файл создан: ПСЖ.pdf");

            } else {
                System.out.println("\u001B[31mОшибка\u001B[0m: Файл не был создан!");
            }

        } catch (Exception e) {
            System.err.println("\u001B[31mОшибка\u001B[0m: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "Создаёт pdf-файл с отчислением ПСЖ";
    }
}