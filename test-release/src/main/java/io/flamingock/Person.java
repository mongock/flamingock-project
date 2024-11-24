/*
 * Copyright 2023 Flamingock ("https://oss.flamingock.io")
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.flamingock;

/**
 * Represents a person with a name, surname, and age.
 * Provides methods to access the person's attributes and override
 * {@code toString}, {@code equals}, and {@code hashCode} for proper behavior.
 */
public class Person {

    /**
     * The first name of the person.
     */
    private final String name;

    /**
     * The surname of the person.
     */
    private final String surname;

    /**
     * The age of the person.
     */
    private final int age;

    /**
     * Constructs a new {@code Person} object with the given name, surname, and age.
     *
     * @param name    the first name of the person.
     * @param surname the surname of the person.
     * @param age     the age of the person.
     */
    public Person(String name, String surname, int age) {
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    /**
     * Returns the first name of the person.
     *
     * @return the first name of the person.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the surname of the person.
     *
     * @return the surname of the person.
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Returns the age of the person.
     *
     * @return the age of the person.
     */
    public int getAge() {
        return age;
    }

    /**
     * Returns a string representation of the person.
     *
     * @return a string containing the person's name, surname, and age.
     */
    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", age=" + age +
                '}';
    }

    /**
     * Indicates whether this person is equal to another object.
     * Two {@code Person} objects are considered equal if their name, surname, and age are all the same.
     *
     * @param o the object to compare with this person.
     * @return {@code true} if this person is equal to the given object; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (age != person.age) return false;
        if (!name.equals(person.name)) return false;
        return surname.equals(person.surname);
    }

    /**
     * Returns a hash code for this person.
     * The hash code is based on the name, surname, and age of the person.
     *
     * @return a hash code value for this person.
     */
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + surname.hashCode();
        result = 31 * result + age;
        return result;
    }
}
