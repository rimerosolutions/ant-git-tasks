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
import java.util.LinkedList;
import java.util.Queue;

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

        private Queue<GitTask> tasks = new LinkedList<GitTask>();

        public void setVerbose(boolean verbose) {
                this.verbose = verbose;
        }

        public void setSettingsRef(String settingsRef) {
                this.settingsRef = settingsRef;
        }

        public void setLocalDirectory(File dir) {
                this.localDirectory = dir;
        }

        public BranchDeleteTask createBranchDelete() {
                BranchDeleteTask c = new BranchDeleteTask();
                tasks.offer(c);

                return c;
        }

        public CheckoutTask createCheckout() {
                CheckoutTask c = new CheckoutTask();
                tasks.offer(c);

                return c;
        }

        public CleanTask createClean() {
                CleanTask c = new CleanTask();
                tasks.offer(c);

                return c;
        }

        public CloneTask createClone() {
                CloneTask c = new CloneTask();
                tasks.offer(c);

                return c;
        }

        public CommitTask createCommit() {
                CommitTask c = new CommitTask();
                tasks.offer(c);

                return c;
        }

        public FetchTask createFetch() {
                FetchTask c = new FetchTask();
                tasks.offer(c);

                return c;
        }

        public InitTask createInit() {
                InitTask c = new InitTask();
                tasks.offer(c);

                return c;
        }

        public PullTask createPull() {
                PullTask p = new PullTask();
                tasks.offer(p);

                return p;
        }

        public PushTask createPush() {
                PushTask c = new PushTask();
                tasks.offer(c);

                return c;
        }

        public UpToDateTask createUpToDate() {
                UpToDateTask c = new UpToDateTask();
                tasks.offer(c);

                return c;
        }

        public TagTask createTag() {
                TagTask c = new TagTask();
                tasks.offer(c);

                return c;
        }

        public TagDeleteTask createTagDelete() {
                TagDeleteTask c = new TagDeleteTask();
                tasks.offer(c);

                return c;
        }

        @Override
        public void execute() throws BuildException {
                if (localDirectory == null) {
                        throw new BuildException("Please specify a local repository directory.");
                }

                while (!tasks.isEmpty()) {
                        GitTask t = tasks.poll();
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
                                t.execute();
                        } catch (Exception e) {
                                throw new BuildException("Unexpected exception occurred!", e);
                        }
                }
        }
}
