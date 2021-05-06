package TrainingPackageNotForRelease.TestWithString;

public class SubstringTest {
    public static void main(String[] args) {
        String myString = "www.yandex.ru";
        System.out.println("Строка: " + myString);
        System.out.println("Последние 3 символа строки: " + myString.substring(myString.length() - 3));
        System.out.println("Символы с 5 по 10 включительно: " + myString.substring(4, 10));
        System.out.println("Первые 3 символа: " + myString.substring(0, 3));
    }
}
