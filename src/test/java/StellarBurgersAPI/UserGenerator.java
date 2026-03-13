package StellarBurgersAPI;
import  java.util.UUID;

//класс для создания уникальных пользователей
public class UserGenerator {
    public static User getRandomUser() {
        //создали уникальную строку
        String uniquePart = UUID.randomUUID().toString().substring(0, 6);

        //возвращаем уникальный email
        return new User(
                "test" + uniquePart + "@email.ru", //получим уник строку
                "password",
                "Dominic" + uniquePart    //тут тоже получим уник строку
        );
    }
}
