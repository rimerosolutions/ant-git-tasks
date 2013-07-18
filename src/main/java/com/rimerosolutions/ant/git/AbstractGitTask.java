/*
 * Copyright 2013 Fabrizio Cannizzo (https://github.com/smartrics/jgit-ant), Rimero Solutions
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

import java.io.File;
import java.net.URISyntaxException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Reference;
import org.eclipse.jgit.api.GitCommand;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.transport.URIish;


/**
 * Abstract Git task
 *
 * @author Fabrizio Cannizzo
 * @author Yves Zoundi
 */
public abstract class AbstractGitTask extends Task implements GitTask {

        private String uri;
        private ProgressMonitor progressMonitor;
        private File directory;
        private String unlessCondition;
        private String ifCondition;
        private String settingsRef;

        /**
         * Returns a reference to git settings
         *
         * @return the settingsRef A reference to git settings
         */
        public String getSettingsRef() {
                return settingsRef;
        }

        /**
         * Sets a git settings reference
         *
         * @param settingsRef the settingsRef to set
         */
        public void setSettingsRef(String settingsRef) {
                if (this.settingsRef == null) {
                        this.settingsRef = settingsRef;
                }
        }

        /**
         * Do not execute unless a given condition is met
         *
         * @param unlessCondition The condition veto execution
         */
        public void setUnless(String unlessCondition) {
                this.unlessCondition = unlessCondition;
        }

        /**
         * Continue execution only if a condition is met
         *
         * @param ifCondition The condition to meet
         */
        public void setIf(String ifCondition) {
                this.ifCondition = ifCondition;
        }

        /**
         * Returns the condition to veto task execution
         *
         * @return the condition to veto task execution
         */
        public String getUnless() {
                return unlessCondition;
        }

        /**
         * Returns the condition to meet prior to task execution
         *
         * @return The condition to meet prior to task execution
         */
        public String getIf() {
                return ifCondition;
        }

        /**
         * Sets the git repository uri
         *
         * @param uri The repository uri
         */
        public void setUri(String uri) {
                if (uri == null) {
                        throw new BuildException("Can't set null URI attribute");
                }
                try {
                        new URIish(uri);
                        this.uri = uri;
                } catch (URISyntaxException e) {
                        throw new BuildException("Invalid URI: " + uri, e);
                }
        }
        
        /**
         * Sets the Git repository directory
         *
         * @param dir The repository directory
         */
        @Override
        public void setDirectory(File dir) {
                if (dir == null) {
                        throw new BuildException("Cannot set null directory attribute");
                }
                this.directory = new File(dir.getAbsolutePath());
        }

        
        /**
         * Sets the Git command progress monitor
         *
         * @param pm The progress monitor
         */
        @Override
        public void setProgressMonitor(ProgressMonitor pm) {
                this.progressMonitor = pm;
        }

        /**
         * Returns the repository uri
         *
         * @return The repository uri
         */
        protected String getUri() {
                return this.uri;
        }

        /**
         * Returns the repository directory
         *
         * @return the repository directory
         */
        protected File getDirectory() {
                return this.directory;
        }

        /**
         * Returns the Git command progress monitor
         *
         * @return the Git command progress monitor
         */
        protected ProgressMonitor getProgressMonitor() {
                return this.progressMonitor;
        }

        /**
         * Lookup the git settings for this task via a project reference
         *
         * @return The configured git settings for this task
         */ 
        protected GitSettings lookupSettings() {
                if (getProject() != null && settingsRef != null) {
                        Reference ref = (Reference) getProject().getReference(settingsRef);

                        if (ref != null) {
                                GitSettings settings = (GitSettings) ref.getReferencedObject();

                                return settings;
                        }
                }

                return null;
        }

        /**
         * Setups the Git credentials if specified and needed
         *
         * @param command The git command to configure
         */
        protected void setupCredentials(GitCommand<?> command) {
                GitSettings settings = lookupSettings();

                if (settings != null && command instanceof TransportCommand) {
                        @SuppressWarnings("rawtypes")
                        TransportCommand cmd = (TransportCommand) command;
                        cmd.setCredentialsProvider(settings.getCredentials());
                }
        }

        /** Execute the task */
        abstract public void execute();
}
