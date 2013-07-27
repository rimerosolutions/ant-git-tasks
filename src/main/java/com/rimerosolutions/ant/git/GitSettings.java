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

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/**
 * Git settings to reuse credentials and identity information.
 *
 * @author Yves Zoundi
 */
public class GitSettings {

        private PersonIdent identity;
        private CredentialsProvider credentials;

        /**
         * Sets the Git credentials
         *
         * @param username The username
         * @param password The password
         */
        public void setCredentials(String username, String password) {
                if (GitTaskUtils.isNullOrBlankString(username) || GitTaskUtils.isNullOrBlankString(password)) {
                        throw new IllegalArgumentException("Credentials must not be empty.");
                }

                credentials = new UsernamePasswordCredentialsProvider(username, password);
        }

        /**
         * Sets the name and email for the Git commands user
         *
         * @param name The Git user's name
         * @param email The Git user's email
         */
        public void setIdentity(String name, String email) {
                if (GitTaskUtils.isNullOrBlankString(name) || GitTaskUtils.isNullOrBlankString(email)) {
                        throw new IllegalArgumentException("Both the username and password must be provided.");
                }

                identity = new PersonIdent(name, email);
        }

        /**
         * Returns the Git user's identity
         *
         * @return The user's identity
         */
        public PersonIdent getIdentity() {
                return identity;
        }

        /**
         * Returns the Git user's credentials
         *
         * @return The user's credentials
         */
        public CredentialsProvider getCredentials() {
                return credentials;
        }

}
