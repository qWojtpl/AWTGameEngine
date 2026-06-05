package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.tests.Test;
import pl.AWTGameEngine.engine.enums.ConsoleColor;
import pl.AWTGameEngine.exceptions.tests.TestNotFoundException;

public class TestPerformer {

    public static void performEngineTests() {
        int sum = 0;
        sum += performAllTests(Dependencies.getAppProperties());
        sum += performAllTests(Dependencies.getPreferences());
        sum += performAllTests(Dependencies.getResourceManager());
        if(sum == 0) {
            Logger.logTest(ConsoleColor.GREEN.value + "All tests passed successfully.");
        } else {
            Logger.logTest(ConsoleColor.RED.value + "Total of " + sum + " tests failed.");
        }
    }

    public static int performAllTests(Object context) {
        if(!context.getClass().isAnnotationPresent(Test.class)) {
            return 0;
        }
        int fails = 0;
        for(Test test : context.getClass().getAnnotationsByType(Test.class)) {
            if(!performTest(context, test)) {
                fails++;
            }
        }
        return fails;
    }

    public static boolean performTest(Object context, Test test) {
        Logger.logTest("Running test " + test.name());
        BaseTest baseTest;
        try {
            baseTest = getInstance(test);
        } catch(TestNotFoundException e) {
            Logger.exception("Cannot get instance of a test.", e);
            return false;
        }
        try {
            baseTest.setup(context);
        } catch(Exception e) {
            Logger.exception("Exception in setup of a test " + test.name(), e);
        }
        boolean result = false;
        try {
            result = baseTest.perform();
        } catch(Exception e) {
            Logger.exception("Exception while performing a test " + test.name(), e);
        }
        try {
            baseTest.end();
        } catch(Exception e) {
            Logger.exception("Exception in ending a test " + test.name(), e);
        }
        if(result) {
            Logger.logTest(ConsoleColor.GREEN.value + "Test " + test.name() + " successfully passed.");
        } else {
            Logger.logTest(ConsoleColor.RED.value + "Test " + test.name() + " failed!");
        }
        return result;
    }

    private static BaseTest getInstance(Test test) {
        try {
            Class<?> clazz = Class.forName(test.testClass());
            return (BaseTest) clazz.getConstructor().newInstance();
        } catch(Exception e) {
            throw new TestNotFoundException(test.testClass());
        }
    }

    public static boolean isRunningTests(String[] args) {
        for(String arg : args) {
            if(arg.equals("--test")) {
                performEngineTests();
                return true;
            }
        }
        return false;
    }

}
