package top.yangxf.interest.algorithm.sort;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

public class CountingSorterTest {

    @Test
    public void sort() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        Sorter<User> sorter = new CountingSorter<>(User::getAge);
        User[] users = new User[100];
        for (int i = 0; i < users.length; i++) {
            users[i] = new User().setName("user" + i).setAge(r.nextInt(100));
        }
        User[] users2 = Arrays.copyOf(users, users.length);
        sorter.sort(users);
        Arrays.sort(users2, Comparator.comparingInt(User::getAge));
        boolean equals = Arrays.equals(users, users2);
        Assert.assertTrue(equals);
    }

    static class User {
        int age;
        String name;

        @Override
        public String toString() {
            return name + ":" + age;
        }

        public int getAge() {
            return age;
        }

        public User setAge(int age) {
            this.age = age;
            return this;
        }

        public String getName() {
            return name;
        }

        public User setName(String name) {
            this.name = name;
            return this;
        }
    }

}