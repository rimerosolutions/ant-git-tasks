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

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.rimerosolutions.ant.git.AbstractGitTask;
import com.rimerosolutions.ant.git.GitBuildException;

/**
 * Initialize a Git repository.
 *
 * <pre>{@code
 * <git:settings refId="git.testing"
 *               username="xxxtesting"
 *               password="xxxtesting"
 *               name="xxxtesting"
 *               email="xxxtesting@gmail.com"/>
 *
 *  <git:git localDirectory="${testLocalRepo}" settingsRef="git.testing">
 *     <git:init directory="${testLocalRepo}" bare="false" />
 *     <git:commit message="${dummy.commit.message}" revCommitIdProperty="revcommit"/>
 *  </git:git>}</pre>
 *
 * <p><a href="https://www.kernel.org/pub/software/scm/git/docs/git-init.html">Git documentation about init</a></p>
 * <p><a href="http://download.eclipse.org/jgit/docs/latest/apidocs/index.html?org/eclipse/jgit/api/InitCommand.html">JGit Initcommand</a></p>
 *
 * @author Yves Zoundi
 */
public class InitTask extends AbstractGitTask {

        private static final String TASK_NAME = "git-init";
        private static final String MESSAGE_INIT_FAILED = "Could not initialize repository.";
        private boolean bare = false;

        @Override
        public String getName() {
                return TASK_NAME;
        }

        /**
         * Whether the repository is bare or not
         *
         * @param bare (Default false)
         */
        public void setBare(boolean bare) {
                this.bare = bare;
        }

        @Override
        public void execute() {
                try {
                        Git.init().
                                setBare(bare).
                                setDirectory(getDirectory()).
                                call();
                } catch (GitAPIException e) {
                        throw new GitBuildException(MESSAGE_INIT_FAILED, e);
                }
        }

}
