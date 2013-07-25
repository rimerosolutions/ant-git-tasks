/*
 * Copyright 2013, Rimero Solutions
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
package com.rimerosolutions.ant.git.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Reference;

import com.rimerosolutions.ant.git.GitSettings;
import com.rimerosolutions.ant.git.GitTaskUtils;

/**
 * Sets reusable Git settings (credentials and identity).
 *
 * <pre>{@code 
 *<git:settings refId="git.testing"
 *              username="xxxtesting"
 *              password="xxxtesting"
 *              name="xxxtesting"
 *              email="xxxtesting@gmail.com"/>
 *}</pre>
 *
 * @author Yves Zoundi
 */
public class GitSettingsTask extends Task {

        private String name;
        private String email;
        private String username;
        private String password;
        private String refId;

        /**
         * Sets a project reference id for reuse
         *
         * @param refId The reference id to set
         */
        public void setRefId(String refId) {
                this.refId = refId;
        }

        /**
         * Sets the user's name
         *
         * @param name the name to set
         */
        public void setName(String name) {
                this.name = name;
        }

        /**
         * Sets the user's email
         *
         * @param email the email to set
         */
        public void setEmail(String email) {
                this.email = email;
        }

        /**
         * Sets the user's name
         *
         * @param username the username to set
         */
        public void setUsername(String username) {
                this.username = username;
        }

        /**
         * Sets the user's password
         *
         * @param password the password to set
         */
        public void setPassword(String password) {
                this.password = password;
        }

        @Override
        public void execute() throws BuildException {
                final GitSettings settings = new GitSettings();

                if (!GitTaskUtils.isNullOrBlankString(username) && !GitTaskUtils.isNullOrBlankString(password)) {
                        settings.setCredentials(username, password);
                }

                if (!GitTaskUtils.isNullOrBlankString(name) && !GitTaskUtils.isNullOrBlankString(email)) {
                        settings.setIdentity(name, email);
                }

                getProject().addReference(refId, new Reference(getProject(), refId) {
                                public GitSettings getReferencedObject(Project fallback) throws BuildException {
                                        return settings;
                                }
                        });
        }

}
