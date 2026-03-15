package stellarburgersapi.generator;

import stellarburgersapi.model.User;

import java.util.UUID;

public class UserGenerator {

    public static User getRandomUser() {
        String uniquePart = UUID.randomUUID().toString().substring(0, 6);

        return new User(
                "test" + uniquePart + "@email.ru",
                "password",
                "Dominic" + uniquePart
        );
    }
}
