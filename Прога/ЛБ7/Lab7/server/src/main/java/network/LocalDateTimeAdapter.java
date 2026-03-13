package network;

import com.google.gson.*;


import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Конвертатор данных о времени создании элемента из/в JSON файл.
 * Позволяет библиотеке GSON корректно преобразовывать объекты даты и времени в JSON-строку и обратно.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    /**
     * Преобразует объект {@link LocalDateTime} в JSON-представление.
     *
     * @param localDateTime исходный объект даты и времени.
     * @param type тип исходного объекта.
     * @param context контекст сериализации.
     * @return JSON-элемент, содержащий строковое представление даты.
     */
    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
    /**
     * Преобразует JSON-представление (строку) обратно в объект {@link LocalDateTime}.
     *
     * @param json JSON-элемент, который нужно десериализовать.
     * @param type тип целевого объекта.
     * @param context контекст десериализации.
     * @return восстановленный объект {@link LocalDateTime}.
     * @throws JsonParseException если строка в JSON не соответствует формату даты.
     */
    @Override
    public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
