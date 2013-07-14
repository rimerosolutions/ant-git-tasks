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
package com.rimerosolutions.ant.git.tasks;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.rimerosolutions.ant.git.GitTask;
import com.rimerosolutions.ant.git.GitUtils;

/**
 * Git tasks container
 *
 * @author Yves Zoundi
 */
public class GitTasks extends Task {

        private boolean verbose = true;
        private File localDirectory;
        private String settingsRef;

        private List<Task> tasks = new ArrayList<Task>();

        /**
         * Unable/Disable Git commands verbosity
         *
         * @param verbose Whether or not the Git commands output should be verbose
         */
        public void setVerbose(boolean verbose) {
                this.verbose = verbose;
        }

        /**
         * Sets a settings reference ID to lookup git settings
         *
         * @param settingsRef A git settings reference ID
         */
        public void setSettingsRef(String settingsRef) {
                this.settingsRef = settingsRef;
        }

        /**
         * Sets the Git local directory
         *
         * @param dir The local directory to set
         */
        public void setLocalDirectory(File dir) {
                this.localDirectory = dir;
        }
        
        public BranchTask createBranch() {
                BranchTask c = new BranchTask();
                tasks.add(c);

                return c;
        }

        public BranchDeleteTask createBranchDelete() {
                BranchDeleteTask c = new BranchDeleteTask();
                tasks.add(c);

                return c;
        }

        public CheckoutTask createCheckout() {
                CheckoutTask c = new CheckoutTask();
                tasks.add(c);

                return c;
        }

        public CleanTask createClean() {
                CleanTask c = new CleanTask();
                tasks.add(c);

                return c;
        }

        public CloneTask createClone() {
                CloneTask c = new CloneTask();
                tasks.add(c);

                return c;
        }

        public CommitTask createCommit() {
                CommitTask c = new CommitTask();
                tasks.add(c);

                return c;
        }

        public FetchTask createFetch() {
                FetchTask c = new FetchTask();
                tasks.add(c);

                return c;
        }

        public InitTask createInit() {
                InitTask c = new InitTask();
                tasks.add(c);

                return c;
        }

        public PullTask createPull() {
                PullTask p = new PullTask();
                tasks.add(p);

                return p;
        }

        public PushTask createPush() {
                PushTask c = new PushTask();
                tasks.add(c);

                return c;
        }

        public UpToDateTask createUpToDate() {
                UpToDateTask c = new UpToDateTask();
                tasks.add(c);

                return c;
        }

        public TagTask createTag() {
                TagTask c = new TagTask();
                tasks.add(c);

                return c;
        }

        public TagDeleteTask createTagDelete() {
                TagDeleteTask c = new TagDeleteTask();
                tasks.add(c);

                return c;
        }

        @Override
        public void execute() throws BuildException {
                if (localDirectory == null) {
                        throw new BuildException("Please specify a localDirectory attribute.");
                }

                for (Task task : tasks) {
                        GitTask t = (GitTask) task;
                        GitUtils.validateTaskConditions(t);

                        if (!GitUtils.nullOrEmptyString(t.getIf())) {
                                if (getProject().getProperty(t.getIf()) == null) {
                                        continue;
                                }
                        }

                        if (!GitUtils.nullOrEmptyString(t.getUnless())) {
                                if (getProject().getProperty(t.getUnless()) != null) {
                                        continue;
                                }
                        }

                        if (!GitUtils.nullOrEmptyString(settingsRef)) {
                                t.setSettingsRef(settingsRef);
                        }

                        if (verbose) {
                                t.setProgressMonitor(new GitTaskMonitor(t));
                        }

                        t.setDirectory(localDirectory);

                        try {
                                task.perform();
                        } catch (Exception e) {
                                throw new BuildException("Unexpected exception occurred!", e);
                        }
                }
        }
}
