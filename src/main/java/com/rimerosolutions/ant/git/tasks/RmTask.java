/*
 * Copyright 2014 Antoine Levy-Lambert, Rimero Solutions
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

import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.resources.Union;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.rimerosolutions.ant.git.AbstractGitRepoAwareTask;
import com.rimerosolutions.ant.git.GitBuildException;

/**
 * Delete files.
 *
 * <pre>{@code
 *  <git:git directory="${testLocalRepo}">
 *    <git:rm>
 *      <fileset dir="${testLocalRepo}" includes="*.txt"/>
 * </git:rm>
 *  </git:git>}</pre>
 *
 * <p><a href="https://www.kernel.org/pub/software/scm/git/docs/git-rm.html">Git documentation about rm</a></p>
 * <p><a href="http://download.eclipse.org/jgit/docs/latest/apidocs/org/eclipse/jgit/api/RmCommand.html">JGit RmCommand</a></p>
 *
 * @author Antoine Levy-Lambert
 */
public class RmTask extends AbstractGitRepoAwareTask {

        private static final String TASK_NAME = "git-rm";
        private Union path;


        @Override
        public String getName() {
                return TASK_NAME;
        }

        /**
         * Configure the fileset(s) of files to add to revision control
         *
         * @param fileset The fileset to add
         */
        public void addFileset(FileSet fileset) {
                getPath().add(fileset);
        }

        private synchronized Union getPath() {
                if (path == null) {
                        path = new Union();
                        path.setProject(getProject());
                }
                return path;
        }

        @Override
        protected void doExecute() {
                try {
                        RmCommand rmCommand = git.rm();
                        String prefix = getDirectory().getCanonicalPath();
                        String[] allFiles = getPath().list();
                        if (allFiles.length == 0) {
                            return;
                        }

                        for (String file : allFiles) {
                                String addedFile = translateFilePathUsingPrefix(file, prefix);
                                rmCommand.addFilepattern(addedFile);
                        }

                        rmCommand.call();
                }
                catch (GitAPIException e) {
                        throw new GitBuildException(e);
                }
                catch (Exception e) {
                        throw new GitBuildException("Unexpected error.", e);
                }
        }

}
