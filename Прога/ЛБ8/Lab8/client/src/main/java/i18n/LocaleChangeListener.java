package i18n;

public interface LocaleChangeListener {
    /**
     * Вызывается при смене локали в ResourceManager
     */
    void onLocaleChange();
}
