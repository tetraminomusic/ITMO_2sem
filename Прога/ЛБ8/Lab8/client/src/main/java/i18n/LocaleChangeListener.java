package i18n;

/**
 * Интерфейс, который реализуют фреймы, где возможна смена локали на горячую.
 * Является ключевым элементом паттерна Observer
 */
public interface LocaleChangeListener {
    /**
     * Вызывается при смене локали в ResourceManager
     */
    void onLocaleChange();
}
