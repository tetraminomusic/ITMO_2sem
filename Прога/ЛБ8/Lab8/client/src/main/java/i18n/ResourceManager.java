package i18n;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

/**
 * Менеджер локализации, реализованный как Синглтон.
 * Отвечает за предоставление переведённых строк и форматированных данных (то есть чисел или дат)
 */
public class ResourceManager {
    //единственный экземпляр класса
    private static ResourceManager instance;

    //хранилище загруженных текстов
    private ResourceBundle bundle;

    //текущая локаль
    private Locale currentLocale;

    //локализованные форматтеры
    private NumberFormat numberFormat;
    private DateTimeFormatter dateTimeFormatter;

    //список поддерживаемых локалей для выпадающего списка для гуи
    public static final List<Locale> SUPPORTED_LOCALES = Arrays.asList(
            new Locale("ru", "RU"),
            new Locale("en", "NZ"),
            new Locale("nl", "NL"),
            new Locale("lt", "LT")
    );

    // Список тех, кто ждёт смены языка
    private final List<LocaleChangeListener> listeners = new ArrayList<>();

    /**
     * Получение единственного экземпляра менеджера.
     */
    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    private ResourceManager() {
        // По умолчанию ставим русский
        setLocale(new Locale("ru", "RU"));
    }

    /**
     * Возвращает переведённую строку по ключу.
     */
    public String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }
    }

    /**
     * Возвращает форматированную строку (используя MessageFormat)
     */
    public String getFormatted(String key, Object... args) {
        String pattern = getString(key);
        return MessageFormat.format(pattern, args);
    }

    /**
     * Форматирует число с плавающей точкой в строку
     */
    public String formatNumber(double number) {
        return numberFormat.format(number);
    }

    /**
     * Форматирует дату в строку
     */
    public String formatDate(LocalDateTime date) {
        if (date == null) return "N/A";
        return date.format(dateTimeFormatter);
    }

    /**
     * Геттер для получения текущей локали
     */
    public Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Регистрирует новое окно или компонент для отслеживания смены языка.
     */
    public void addLocaleChangeListener(LocaleChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Устанавливает новую локаль и УВЕДОМЛЯЕТ всех слушателей.
     */
    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        this.bundle = ResourceBundle.getBundle("messages", locale);
        this.numberFormat = NumberFormat.getInstance(locale);
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withLocale(locale);

        // ВАЖНО: Оповещаем всех
        for (LocaleChangeListener listener : listeners) {
            listener.onLocaleChange();
        }
    }

}
