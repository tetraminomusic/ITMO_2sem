package commands;

import java.time.LocalDate;

public class DeductionCreating {
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
    private String fullname;
    private String facility;
    private String phone;
    private String group;
    private String email;
    private LocalDate currentDate = LocalDate.now();

    public DeductionCreating(String fullname, String group, String facility, String phone, String email) {
        this.group = group;
        this.fullname = fullname;
        this.facility = facility;
        this.phone = phone;
        this.email = email;
    }

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
