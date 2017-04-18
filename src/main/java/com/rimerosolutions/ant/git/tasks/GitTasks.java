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
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.rimerosolutions.ant.git.GitTask;
import com.rimerosolutions.ant.git.GitTaskMonitor;
import com.rimerosolutions.ant.git.GitTaskUtils;

/**
 * Git tasks container.
 *
 * <pre>{@code
 * <git:settings refId="git.testing"
 *               username="xxxtesting"
 *               password="xxxtesting"
 *               name="xxxtesting"
 *               email="xxxtesting@gmail.com"/>
 *
 *  <git:git directory="${testLocalRepo}" settingsRef="git.testing">
 *     <git:init directory="${testLocalRepo}" bare="false" />
 *     <git:commit message="${dummy.commit.message}" revCommitIdProperty="revcommit"/>
 *  </git:git>}</pre>
 *
 * @author Yves Zoundi
 */
public class GitTasks extends Task {

        private boolean verbose = true;
        private File directory;
        private String settingsRef;

        private final List<Task> tasks = new ArrayList<Task>();

        /**
         * Unable/Disable Git commands verbosity
         *
         * @antdoc.notrequired
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
         * Sets the Git local directory (Not required if set already at the parent task level).
         *
         * @param dir The local directory to set.
         */
        public void setDirectory(File dir) {
                this.directory = dir;
        }

        /**
         * Creates a new nested <code>currentbranch</code> task
         *
         * @return A new task to display the current branch
         */
        public CurrentBranchTask createCurrentBranch() {
                CurrentBranchTask c = new CurrentBranchTask();
                tasks.add(c);

                return c;
        }

        /**
         * Creates a new nested <code>branchlist</code> task
         *
         * @return A new task to list branches
         */
        public BranchListTask createBranchList() {
                BranchListTask c = new BranchListTask();
                tasks.add(c);

                return c;
        }

        /**
         * Creates a nested <code>apply</code> task.
         *
         * @return A new task to apply patches.
         */
        public ApplyTask createApply() {
                ApplyTask c = new ApplyTask();
                tasks.add(c);

                return c;
        }

        /**
         * Creates a nested <code>add</code> task.
         *
         * @return A new task to add files or directories.
         */
        public AddTask createAdd() {
                AddTask c = new AddTask();
                tasks.add(c);

                return c;
        }

        /**
         * Creates a nested <code>rm</code> task.
         *
         * @return A new task to remove files or directories.
         */
        public RmTask createRm() {
                RmTask c = new RmTask();
                tasks.add(c);

                return c;
        }

        /**
             * Creates a nested <code>branch</code> task.
             *
             * @return a new branch task.
             */
        public BranchTask createBranch() {
                BranchTask c = new BranchTask();
                tasks.add(c);

                return c;
        }

        /**
         * Creates a nested <code>branchdelete</code> task.
         *
         * @return a task to delete branches.
         */
        public BranchDeleteTask createBranchDelete() {
                BranchDeleteTask c = new BranchDeleteTask();
                tasks.add(c);

                return c;
        }

        /**
         * Creates a nested checkout task.
         *
         * @return A task to checkout code.
         */
        public CheckoutTask createCheckout() {
                CheckoutTask c = new CheckoutTask();
                tasks.add(c);

                return c;
        }

        /**
         * Creates a nested <code>clean</code> task.
         *
         * @return A task to clean a repository.
         */
        public CleanTask createClean() {
                CleanTask c = new CleanTask();
                tasks.add(c);

                return c;
        }

        /**
         * Creates a nested <code>clone</code> task.
         *
         * @return A task to clone repositories.
         */
        public CloneTask createClone() {
                CloneTask c = new CloneTask();
                tasks.add(c);

                return c;
        }

        /**
         * Creates a nested <code>commit</code> task
         *
         * @return a new task to commit changes
         */
        public CommitTask createCommit() {
                CommitTask c = new CommitTask();
                tasks.add(c);

                return c;
        }

        /**
         * Creates a nested <code>fetch</code> task
         *
         * @return a new task to fetch changes
         */
        public FetchTask createFetch() {
                FetchTask c = new FetchTask();
                tasks.add(c);

                return c;
        }

        /**
         * Creates a nested <code>init</code> task
         *
         * @return a new task to initialize a repository
         */
        public InitTask createInit() {
                InitTask c = new InitTask();
                tasks.add(c);

                return c;
        }

        /**
         * Creates a nested <code>pull</code> task.
         *
         * @return a new task to pull changes.
         */
        public PullTask createPull() {
                PullTask p = new PullTask();
                tasks.add(p);

                return p;
        }

        /**
         * Creates a nested <code>merge</code> task.
         *
         * @return a new task to merge changes.
         */
        public MergeTask createMerge() {
                MergeTask m = new MergeTask();
                tasks.add(m);

                return m;
        }

        /**
         * Creates a nested <code>tag</code> task.
         *
         * @return a new task to create tags.
         */
        public PushTask createPush() {
                PushTask c = new PushTask();
                tasks.add(c);

                return c;
        }

        /**
         * Creates a nested <code>tag</code> task.
         *
         * @return a new task to create tags.
         */
        public ResetTask createReset() {
                ResetTask c = new ResetTask();
                tasks.add(c);

                return c;
        }

        /**
         * Creates a nested <code>tag</code> task
         *
         * @return a new task to create tags
         */
        public UpToDateTask createUpToDate() {
                UpToDateTask c = new UpToDateTask();
                tasks.add(c);

                return c;
        }

        /**
         * Creates a nested <code>tag</code> task.
         *
         * @return a new task to create tags.
         */
        public TagTask createTag() {
                TagTask c = new TagTask();
                tasks.add(c);

                return c;
        }

        /**
         * Creates a nested <code>taglist</code> task
         *
         * @return a new task to list tags
         */
        public TagListTask createTagList() {
                TagListTask c = new TagListTask();
                tasks.add(c);

                return c;
        }

        /**
         * Creats a nested <code>tagdelete</code> task.
         *
         * @return a new task to delete tags.
         */
        public TagDeleteTask createTagDelete() {
                TagDeleteTask c = new TagDeleteTask();
                tasks.add(c);

                return c;
        }

        @Override
        public void execute() throws BuildException {
                if (directory == null) {
                        throw new BuildException("Please specify a directory attribute.");
                }

                for (Task task : tasks) {
                        GitTask t = (GitTask) task;
                        GitTaskUtils.validateTaskConditions(t);

                        if (!GitTaskUtils.isNullOrBlankString(t.getIf())) {
                                if (getProject().getProperty(t.getIf()) == null) {
                                        continue;
                                }
                        }

                        if (!GitTaskUtils.isNullOrBlankString(t.getUnless())) {
                                if (getProject().getProperty(t.getUnless()) != null) {
                                        continue;
                                }
                        }

                        if (!GitTaskUtils.isNullOrBlankString(settingsRef)) {
                                t.setSettingsRef(settingsRef);
                        }

                        if (verbose) {
                                t.useProgressMonitor(new GitTaskMonitor(t));
                        }

                        t.setDirectory(directory);

                        task.perform();
                }
        }
}
