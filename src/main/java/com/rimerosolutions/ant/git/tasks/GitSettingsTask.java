package com.rimerosolutions.ant.git.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Reference;

import com.rimerosolutions.ant.git.GitSettings;
import com.rimerosolutions.ant.git.GitUtils;

public class GitSettingsTask extends Task {

        private String name;
        private String email;
        private String username;
        private String password;
        private String refId;

        public void setRefId(String refId) {
                this.refId = refId;
        }

        /**
         * @param name
         *                the name to set
         */
        public void setName(String name) {
                this.name = name;
        }

        /**
         * @param email
         *                the email to set
         */
        public void setEmail(String email) {
                this.email = email;
        }

        /**
         * @param username
         *                the username to set
         */
        public void setUsername(String username) {
                this.username = username;
        }

        /**
         * @param password
         *                the password to set
         */
        public void setPassword(String password) {
                this.password = password;
        }

        @Override
        public void execute() throws BuildException {
                final GitSettings settings = new GitSettings();

                if (!GitUtils.nullOrEmptyString(username) && !GitUtils.nullOrEmptyString(password)) {
                        settings.setCredentials(username, password);
                }

                if (!GitUtils.nullOrEmptyString(name) && !GitUtils.nullOrEmptyString(email)) {
                        settings.setIdentity(name, email);
                }

                getProject().addReference(refId, new Reference(getProject(), refId) {
                                public GitSettings getReferencedObject(Project fallback) throws BuildException {
                                        return settings;
                                }
                        });
        }

}
