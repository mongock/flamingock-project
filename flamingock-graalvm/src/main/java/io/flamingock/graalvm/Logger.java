package io.flamingock.graalvm;

public class Logger {
    private static final String PREFIX = "Flamingock:";


    public void initProcess(String message) {
        System.out.printf("%s ...%s\n", PREFIX, message);
    }

    public void finishedProcess(String message) {
        System.out.printf("%s finished %s\n", PREFIX, message);
    }

    public void initRegistration(String registrationName) {
        initProcess("registering " + registrationName);
    }

    public void finishedRegistration(String registrationName) {
        finishedProcess(registrationName);
    }

    public void initClassRegistration(Class<?> clazz) {
        System.out.printf("\t...registering class: %s \n", clazz.getName());
    }

    public void finishedClassRegistration(Class<?> clazz) {
        System.out.printf("\t...finished registration class: %s \n", clazz.getName());
    }
}
