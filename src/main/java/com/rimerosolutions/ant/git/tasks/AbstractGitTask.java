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
package com.rimerosolutions.ant.git.tasks;

import java.io.File;
import java.net.URISyntaxException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Reference;
import org.eclipse.jgit.api.GitCommand;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.transport.URIish;

import com.rimerosolutions.ant.git.GitSettings;
import com.rimerosolutions.ant.git.GitTask;

public abstract class AbstractGitTask extends Task implements GitTask {

        private String uri;
        private ProgressMonitor progressMonitor;
        private File directory;
        private String unlessCondition;
        private String ifCondition;
        private String settingsRef;

        /**
         * @return the settingsRef
         */
        public String getSettingsRef() {
                return settingsRef;
        }

        /**
         * @param settingsRef
         *                the settingsRef to set
         */
        public void setSettingsRef(String settingsRef) {
                if (this.settingsRef == null) {
                        this.settingsRef = settingsRef;
                }
        }

        /**
         *
         * @param unlessCondition
         */
        public void setUnless(String unlessCondition) {
                this.unlessCondition = unlessCondition;
        }

        /**
         *
         * @param ifCondition
         */
        public void setIf(String ifCondition) {
                this.ifCondition = ifCondition;
        }

        public String getUnless() {
                return unlessCondition;
        }

        public String getIf() {
                return ifCondition;
        }

        /**
         *
         * @param uri
         */
        public void setUri(String uri) {
                if (uri == null) {
                        throw new BuildException("Can;t set null URI attribute");
                }
                try {
                        new URIish(uri);
                        this.uri = uri;
                } catch (URISyntaxException e) {
                        throw new BuildException("Invalid URI: " + uri, e);
                }
        }

        @Override
        public void setDirectory(File dir) {
                if (dir == null) {
                        throw new BuildException("Cannot set null directory attribute");
                }
                this.directory = new File(dir.getAbsolutePath());
        }

        @Override
        public void setProgressMonitor(ProgressMonitor pm) {
                this.progressMonitor = pm;
        }

        protected String getUri() {
                return this.uri;
        }

        protected File getDirectory() {
                return this.directory;
        }

        protected ProgressMonitor getProgressMonitor() {
                return this.progressMonitor;
        }

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



        protected void setupCredentials(GitCommand<?> command) {
                GitSettings settings = lookupSettings();

                if (settings != null && command instanceof TransportCommand) {
                        @SuppressWarnings("rawtypes")
                        TransportCommand cmd = (TransportCommand) command;
                        cmd.setCredentialsProvider(settings.getCredentials());
                }
        }

        abstract public void execute();
}
