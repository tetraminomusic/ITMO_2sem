package commands;

import org.jline.reader.LineReader;
import java.io.*;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;

public class WriteDeduction implements Command {
    private final LineReader reader;

    public WriteDeduction(LineReader reader) {
        this.reader = reader;
    }

    @Override
    public void execute(String arg) {
        try {
            System.out.println("Заполнение ПСЖ:");

            // УБРАТЬ \s из шаблона - это \u001B[31mОшибка\u001B[0m!
            String texTemplate = """
                    \\documentclass[a4paper,12pt]{article}
                    \\usepackage[utf8]{inputenc}
                    \\usepackage[T2A]{fontenc}
                    \\usepackage[russian]{babel}
                    \\usepackage[margin=2cm]{geometry}
                    \\usepackage{array}
                    \\setlength{\\parindent}{0pt}
                    
                    \\begin{document}
                    
                    \\begin{tabular}{p{0.5\\textwidth}p{0.5\\textwidth}}
                    \\vspace{0pt}
                    {В приказ} & \\\\
                    \\vspace{0.2cm} & \\\\
                    \\underline{\\makebox[4cm]{}} & \\\\
                    (подпись) & \\\\
                    \\vspace{0.5cm} & \\\\
                    «\\underline{\\makebox[0.5cm]{%s}}» \\underline{\\makebox[2cm]{%s}} 20\\underline{\\makebox[1cm]{%s}} г. &
                    \\end{tabular}
                    
                    \\vspace{-3cm}
                    
                    
                    \\hfill{}{
                    \\raggedleft
                    \\begin{tabular}{r@{}}
                    Ректору Университета ИТМО \\\\
                    член-корреспонденту РАН \\\\
                    д.т.н., профессору В.Н. Васильеву \\\\[1em]
                    от \\underline{\\makebox[7cm]{%s}} \\\\
                    {(фамилия, имя, отчество полностью)} \\\\
                    обучающегося группы № \\underline{\\makebox[2cm]{%s}} \\\\
                    факультета/института (кластера) \\\\[0.5em]
                    \\underline{\\makebox[7cm]{%s}} \\\\
                    {(наименование подразделения)} \\\\
                    контактный телефон \\underline{\\makebox[4cm]{%s}} \\\\
                    email \\underline{\\makebox[6cm]{%s}}
                    \\end{tabular}
                    }
                    
                    \\vspace{0.5cm}
                    
                    \\begin{center}
                        \\textbf{ЗАЯВЛЕНИЕ}
                    \\end{center}
                    
                    
                    Прошу отчислить меня из университета по собственному желанию.
                    
                    \\begin{flushright}
                    \\begin{tabular}{@{}c@{\\hspace{1.5cm}}c@{\\hspace{1.5cm}}c@{}}
                    «\\underline{\\makebox[0.5cm]{%s}}» \\underline{\\makebox[2.2cm]{%s}} 20\\underline{\\makebox[1cm]{%s}} г. &\s
                    \\underline{\\makebox[4cm]{}} &
                    \\underline{\\makebox[6cm]{%s}} \\\\[3mm]
                    \\multicolumn{1}{l}{\\phantom{абоаф1}(дата)} & (личная подпись) & (ФИО)
                    \\end{tabular}
                    \\end{flushright}
                    
                    \\end{document}
                    """;

            String day = askInput("Введите день (например, 30): ");
            String month = askInput("Введите месяц (например, апреля): ");
            String year = askInput("Введите год (последние две цифры, например, 26): ");
            String fullName = askInput("Введите ФИО полностью (например, Март Скуберт Олегович): ");
            String groupNumber = askInput("Введите номер группы (например, P3132): ");
            String faculty = askInput("Введите название факультета/института (например, ПИиКт): ");
            String phone = askInput("Введите контактный телефон (например, +79313134585): ");
            String email = askInput("Введите email (например, student@yandex.ru): ");

            String texContent = String.format(texTemplate,
                    day, month, year,           // первая дата
                    fullName, groupNumber, faculty, phone, email,  // данные студента
                    day, month, year, fullName  // подпись и дата
            );
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

    private String askInput(String message) {
        while (true) {
            String input = reader.readLine(message).trim();

            if (input.isEmpty()) {
                System.out.println("Поле не может быть пустым!");
                continue;
            }
            return input;
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