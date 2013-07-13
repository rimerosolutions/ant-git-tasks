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

import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.GitCommand;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

import com.rimerosolutions.ant.git.GitBuildException;
import com.rimerosolutions.ant.git.GitSettings;

/**
 * Base Ant Task with a managed reference to the Git repository.
 *
 * @author Yves Zoundi
 */
public abstract class AbstractGitRepoAwareTask extends AbstractGitTask {

        protected Git git;
        private boolean failOnError = false;

        /**
         * Whether or not an exception should be thrown if the task fails.
         *
         * @param failOnError Build fails on error?
         */
        public void setFailOnError(boolean failOnError) {
                this.failOnError = failOnError;
        }

        /**
         * Whether or not an exception should be thrown if the task fails.
         *
         * @return True if the build should fail when an exception is thrown
         */
        public boolean isFailOnError() {
                return failOnError;
        }

        protected abstract void doExecute() throws BuildException;

        @Override
        public final void execute() {
                try {
                        try {
                                Repository repo = new RepositoryBuilder().findGitDir(getDirectory()).build();
                                git = new Git(repo);
                        }
                        catch (IOException ioe) {
                                String errorMsg = "Specified path (%s) doesn't seem to be a git repository.";

                                throw new BuildException(String.format(errorMsg, getDirectory().getAbsolutePath()), ioe);
                        }

                        doExecute();

                }
                catch (GitBuildException e) {
                        log(e, Project.MSG_ERR);

                        if (failOnError) {
                                throw new BuildException(e);
                        }
                }
                finally {
                        if (git != null) {
                                git.getRepository().close();
                        }
                }
        }

}
