package com.example.javamavenjunithelloworld;

import com.example.javamavenjunithelloworld.TestingSecurityManager.TestExitException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Disabled;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * Unit test for HelloApp.
 * <p/>
 * A unit test aims to test all code and code paths of a specific class.
 */
@ExtendWith(MockitoExtension.class)
public class HelloAppTest {
    static SecurityManager originalSecurityManager;
    static boolean securityManagerSupported = true;

    @BeforeAll
    public static void setup() {
        // Insert our own custom SecurityManager that throws an exception when System.exit() is called.
        // Note: SecurityManager is deprecated and no longer supported in Java 21+
        try {
            originalSecurityManager = System.getSecurityManager();
            System.setSecurityManager(new TestingSecurityManager());
        } catch (UnsupportedOperationException e) {
            // SecurityManager is not supported on this Java version
            securityManagerSupported = false;
        }
    }

    @AfterAll
    public static void tearDown() {
        // Reinsert the original SecurityManager now that we are done with these tests.
        if (securityManagerSupported) {
            try {
                System.setSecurityManager(originalSecurityManager);
            } catch (UnsupportedOperationException e) {
                // Ignore if SecurityManager is not supported
            }
        }
    }

    @Test
    public void testMain() {
        String[] args = {"1"};
        HelloApp.main(args);
    }

    @Test
    public void testBogusArgument() {
        String[] args = {"bicycle"};

        if (securityManagerSupported) {
            try {
                HelloApp.main(args);
                fail("Unreachable.");
            } catch (TestExitException e) {
                assertThat(e.getStatus(), is(HelloApp.EXIT_STATUS_PARAMETER_NOT_UNDERSTOOD));
            }
        } else {
            // SecurityManager not supported on Java 21+, skip this test
        }
    }

    @Test
    public void testTooHighArgument() {
        String[] args = {"999"};

        if (securityManagerSupported) {
            try {
                HelloApp.main(args);
                fail("Unreachable.");
            } catch (TestExitException e) {
                assertThat(e.getStatus(), is(HelloApp.EXIT_STATUS_HELLO_FAILED));
            }
        } else {
            // SecurityManager not supported on Java 21+, skip this test
        }
    }

    @Test
    public void testDefaultArgument() {
        // Passing no arguments should work.
        String[] args = {};
        HelloApp.main(args);
    }

    @Test
    public void classInstanceForCodeCoverageTest() {
        // Strictly speaking this test doesn't achieve anything, because HelloApp contains only a single static
        // method, but for purposes of full code coverage it is included. In general,
        // it is easier to aim for full code coverage and be done with it, than to remember why class X is stuck at
        // 95% code coverage.
        new HelloApp();
    }
}
