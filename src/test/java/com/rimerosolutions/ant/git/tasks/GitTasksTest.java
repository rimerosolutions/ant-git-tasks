package com.rimerosolutions.ant.git.tasks;

import org.apache.tools.ant.BuildFileTest;

public class GitTasksTest extends BuildFileTest {

        public GitTasksTest(String s) {
            super(s);
        }

        public void setUp() {
            // initialize Ant
            configureProject("src/test/resources/ant-test.xml");
        }

        public void testWithout() {
            try {
                    executeTarget("setUp");
                    executeTarget("testTouchCreatesFile"); 
            }
            finally {
                    executeTarget("tearDown");
            }
        } 
    }