package ch.ethz.gametheory.ptesolver;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

// Runs all available tests
public class TestRunner {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(NFTests.class, EFTests.class, EFIITests.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        System.out.println(result.wasSuccessful());
    }
}
