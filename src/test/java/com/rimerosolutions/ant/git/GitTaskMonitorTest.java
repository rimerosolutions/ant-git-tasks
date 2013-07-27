/*
 * Copyright 2013 Rimero Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rimerosolutions.ant.git;

import static org.junit.Assert.*;
import org.junit.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
/**
 * Unit test for some utility classes
 *
 * @author Yves Zoundi
 */
public class GitTaskMonitorTest {

        private final StringBuilder sb = new StringBuilder();
        private final String taskName = "monitor-test";
        private final GitTaskMonitor monitor = new GitTaskMonitor(newMockedGitTask());

        // Mock a Git task, we only care about the name and log capabilities.
        private GitTask newMockedGitTask() {
                InvocationHandler handler = new InvocationHandler() {

                                public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
                                        if (m.getName().equals("log")) {
                                                sb.append(args[0].toString());

                                                return null;
                                        }
                                        else if (m.getName().equals("getName")) {
                                                return taskName;
                                        }

                                        throw new UnsupportedOperationException("Not supported");
                                }
                        };

                return (GitTask) Proxy.newProxyInstance(GitTask.class.getClassLoader(),
                                                        new Class[] { GitTask.class },
                                                        handler);
        }


        @Before
        public void resetStringBuilder() {
                sb.setLength(0);
        }

        @Test
        public void testStart() {
                monitor.start(1);
                assertEquals("[" + taskName + "] " + GitTaskMonitor.MESSAGE_STARTING, sb.toString() );
        }

        @Test
        public void testBeginTask() {
                monitor.beginTask("work", 1);
                assertEquals("[work] " + GitTaskMonitor.MESSAGE_BEGIN, sb.toString());
        }

        @Test
        public void testUpdate(){
                monitor.start(1);
                sb.setLength(0);
                monitor.update(1);
                assertEquals("[" + taskName + "] " + GitTaskMonitor.MESSAGE_STATUS + " [1/1]", sb.toString());
        }

        @Test
        public void testEndTask(){
                monitor.endTask();
                assertEquals("[" + taskName + "] " + GitTaskMonitor.MESSAGE_ENDING, sb.toString());
        }
}
