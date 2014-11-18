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

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

/**
 * Base Ant Task with a managed reference to the Git repository.
 *
 * @author Yves Zoundi
 */
public abstract class AbstractGitRepoAwareTask extends AbstractGitTask {

        protected Git git;
        private boolean failOnError = true;

        /**
         * Whether or not an exception should be thrown if the task fails.
         *
         * @antdoc.notrequired
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
                                Repository repository = new RepositoryBuilder().
                                        readEnvironment().
                                        findGitDir(getDirectory()).
                                        build();
                                git = new Git(repository);
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

        /**
         * return either a "." if file and prefix have the same value,
         * or the right part of file - length of prefix plus one removed
         * @param file file on which a git operation needs to be done
         * @param prefix folder of the git sandbox
         * @return path relative to git sandbox folder
         * @throws IOException the method uses File#getCanonicalPath which can throw IOException
         */
        protected String translateFilePathUsingPrefix(String file, String prefix) throws IOException {
                if (file.equals(prefix)) {
                        return ".";
                }
                String result = new File(file).getCanonicalPath().substring(prefix.length() + 1);
                if (File.separatorChar != '/') {
                        result = result.replace(File.separatorChar, '/');
                }
                return result;
        }



}
