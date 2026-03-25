package network;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Класс, отвечающий за хеширование паролей
 */
public class PasswordHasher {
    /**
     * Хеширует пароль алгоритмом MD5
     * @return зашифрованный пароль
     */
    public static String hashPassword(String password){

        try {//получаем экземпляр вычислителя для алгоритма MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            //возвращает массив байтов, то есть нашу хеш сумму
            byte[] messageDigest = md.digest(password.getBytes());

            //превращаем массив байтов в строку
            BigInteger no = new BigInteger(1, messageDigest);

            //переводим в 16-ричную сс
            String hashtext = no.toString(16);

            //дополняем до 32-разрядов
            while (hashtext.length() < 32) hashtext = "0" + hashtext;

            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Ошибка: Выбран не валидный алгоритм хеширования");
            return null;
        }
    }
}
