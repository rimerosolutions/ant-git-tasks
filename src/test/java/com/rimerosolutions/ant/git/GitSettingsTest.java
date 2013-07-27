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

/**
 * Unit test for some utility classes
 *
 * @author Yves Zoundi
 */
public class GitSettingsTest {

        private GitSettings settings;

        @Before
        public void initializeSettings() {
                settings = new GitSettings();
        }

        @Test(expected = IllegalArgumentException.class)
        public void testSetIdentityWithInvalidParameters() {
                settings.setIdentity(null, null);
        }

        @Test(expected = IllegalArgumentException.class)
        public void testSetIdentityWithValidNameAndBlankEmail() {
                settings.setIdentity("name", null);
        }

        @Test(expected = IllegalArgumentException.class)
        public void testSetIdentityWithNoNameAndProvidedEmail() {
                settings.setIdentity(null, "email@email.com");
        }

        public void testSetIdentityWithValidParameters() {
                settings.setIdentity("username", "email@email.com");                
                assertEquals(settings.getIdentity().getName(), "username");
                assertEquals(settings.getIdentity().getEmailAddress(), "email@email.com");
        }

        @Test(expected = IllegalArgumentException.class)
        public void testSetCredentialsWithNullUsernameAndPassword() {
                settings.setCredentials(null, null);
        }

        @Test(expected = IllegalArgumentException.class)
        public void testsetCredentialsWithNullUsernameAndProvidedPassword() {
                settings.setCredentials(null, "password");
        }

        @Test(expected = IllegalArgumentException.class)
        public void testSetCredentialsWithProvidedUsernameAndNullPassword() {
                settings.setCredentials("username", null);
        }

        @Test
        public void testSetCredentialsWithValidParameters() {
                settings.setCredentials("username", "password");
        }

}
