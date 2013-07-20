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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.apache.tools.ant.BuildException;
import com.rimerosolutions.ant.git.tasks.InitTask;

/**
 * Unit test for some utility classes
 *
 * @author Yves Zoundi
 */
public class GitUtilsTest {

        @Test
        public void testIsNullOrBlankString() {
                String s = null;

                assertTrue(GitUtils.isNullOrBlankString(s));

                s = "";
                assertTrue(GitUtils.isNullOrBlankString(s));

                s = " ";
                assertTrue(GitUtils.isNullOrBlankString(s));

                s = "abc";
                assertFalse(GitUtils.isNullOrBlankString(s));
        }

        @Test
        public void  testSanitizeBranchName() {
                String branchName = "refs/heads/mybranch";
                assertEquals("mybranch", GitUtils.sanitizeRefName(branchName));

                branchName = "refs/tags/mybranch";
                assertEquals("mybranch", GitUtils.sanitizeRefName(branchName));
        }

        @Test
        public void testValidateDefinitionSingleCondition() {
                InitTask t = new InitTask();
                t.setIf("Hello");
                GitUtils.validateTaskConditions(t);

                t = new InitTask();
                t.setUnless("Hello");
                GitUtils.validateTaskConditions(t);
        }

        @Test(expected=BuildException.class)
        public void testValidateDefinitionBothConditions() {
                InitTask t = new InitTask();
                t.setIf("Hello");
                t.setUnless("Hello");
                GitUtils.validateTaskConditions(t);
        }
}
