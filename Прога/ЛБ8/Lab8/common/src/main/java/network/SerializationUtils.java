package network;

import java.io.*;

/**
 * Утилитный класс для превращения Java-объектов в массив байтов и обратно.
 */
public class SerializationUtils {
    /**
     * Сериализует объект в массив байтов
     * @param obj объект, который мы хотим подвергнуть сериализации
     * @return массив байтов
     * @throws IOException ошибка, связанная с некорректной сериализацией
     */
    public static byte[] serialize(Object obj) throws IOException {
        //создаём временный буфер, в котором мы будем хранить байты для последующей передачи
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             //создаёт IO, который может переводить объекты в байты, делая это рекурсивно (если объект состоит из других объектов)
             ObjectOutputStream oos = new ObjectOutputStream(baos);) {
            //превращение объекта в байты
            oos.writeObject(obj);
            //проталкиваем остатки
            oos.flush();
            //берём байты из потока и превращаем в массив
            return baos.toByteArray();
        }
    }

    /**
     * Десериализует массив байтов в объект
     * @param data массив байтов
     * @return собранный объект
     * @throws IOException ошибка, связанная с некорректной десериализацией
     * @throws ClassNotFoundException ошибка, связанная с классом объект, который не совпадает с избранным.
     */
    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        //поток, который читает данные из массива байтов
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
            //переводит массив байтов в объект
            ObjectInputStream ois = new ObjectInputStream(bais)) {
            //сборка объекта
            return ois.readObject();
        }
    }
}
