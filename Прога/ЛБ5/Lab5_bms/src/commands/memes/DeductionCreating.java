package commands.memes;

import java.time.LocalDate;

/**
 * Вёрстка tex-документа, где производится вставка личных данных в шаблон документа для последующей компиляции в команде WriteDeduction.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class DeductionCreating {
    /**
     * Строка, хранящая tex-шаблон для последующей вставки вводимых личных данных.
     */
    private String texTemplate = """
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
    /**
     * Строка, которая будет хранить ФИО студента.
     */
    private String fullname;
    /**
     * Строка, хранящая название факультета, где числится студент.
     */
    private String facility;
    /**
     * Строка, хранящая номер телефона студента.
     */
    private String phone;
    /**
     * Строка, хранящая номер группы студента.
     */
    private String group;
    /**
     * Строка, которая хранит электронную почту студента.
     */
    private String email;
    /**
     * Поле, которое хранит текущее время.
     */
    private LocalDate currentDate = LocalDate.now();

    /**
     * Конструктор верстальщика.
     * @param fullname ФИО студента
     * @param group группа студента
     * @param facility факультет, где числится студент
     * @param phone номер телефона студента
     * @param email электронная почта студента
     */
    public DeductionCreating(String fullname, String group, String facility, String phone, String email) {
        this.group = group;
        this.fullname = fullname;
        this.facility = facility;
        this.phone = phone;
        this.email = email;
    }

    /**
     * Подстановка личных данных в шаблон Latex.
     * @return итоговый tex-документ в строковом представлении.
     */
    public String getTexFile() {
        int day = currentDate.getDayOfMonth();
        String month = monthStringer();
        int year = (currentDate.getYear()) % 100;
        String texContent = String.format(texTemplate,
                day, month, year,           // первая дата
                fullname, group, facility, phone, email,  // данные студента
                day, month, year, fullname  // подпись и дата
        );

        return texContent;
    }

    /**
     * Преобразует номер текущего месяца в строку на русском языке в родительном падеже.
     * @return название месяца в родительном падеже (например, "января", "февраля").
     */
    private String monthStringer() {
        int month = currentDate.getMonthValue();
        switch (month) {
            case 1: return "января";
            case 2: return "февраля";
            case 3: return "марта";
            case 4: return "апреля";
            case 5: return "мая";
            case 6: return "июня";
            case 7: return "июля";
            case 8: return "августа";
            case 9: return "сентября";
            case 10: return "октября";
            case 11: return "ноября";
            case 12: return "декабря";
        }
        return "";
    }

}
