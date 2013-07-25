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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jgit.api.CleanCommand;

import com.rimerosolutions.ant.git.AbstractGitRepoAwareTask;
import com.rimerosolutions.ant.git.GitBuildException;


/**
 * Git clean Ant task.
 *
 * <pre>{@code
 * <echo file="${testLocalRepo}/test.txt" message="test"/>
 * <git:git localDirectory="${testLocalRepo}" verbose="true" settingsRef="git.testing">
 *   <git:clean/>
 *   <git:uptodate failOnError="true"/>
 * </git:git>
 * }</pre>
 *
 * <p><a href="http://www.kernel.org/pub/software/scm/git/docs/git-clean.html">Git documentation about clean</a></p>
 * <p><a href="http://download.eclipse.org/jgit/docs/latest/apidocs/org/eclipse/jgit/api/CleanCommand.html">JGit CleanCommand</a></p>
 *
 * @author Yves Zoundi
 */
public class CleanTask extends AbstractGitRepoAwareTask {
        private boolean dryRun = false;
        private boolean cleanDirectories = true;
        private boolean ignore = true;
        private Set<String> pathList = new HashSet<String>();
        private static final String TASK_NAME = "git-clean";

        @Override
        public String getName() {
                return TASK_NAME;
        }

        /**
         * If paths are set, only these paths are affected by the cleaning.
         *
         * @antdoc.notrequired
         * @param paths the paths to set
         */
        public void setPaths(String paths) {
                if (paths != null && paths.trim().length() > 0) {
                        String[] pathArray = paths.split(",");
                        pathList.addAll(Arrays.asList(pathArray));
                }
        }

        /**
         * If dirs is set, in addition to files, also clean directories.
         *
         * @antdoc.notrequired
         * @param cleanDirectories whether to clean directories too, or only files. (Default is true)
         */
        public void setCleanDirectories(boolean cleanDirectories) {
                this.cleanDirectories = cleanDirectories;
        }

        /**
         * If ignore is set, don't report/clean files/directories that are ignored by a .gitignore. otherwise do handle them.
         *
         * @antdoc.notrequired
         * @param ignore whether to respect .gitignore or not. (Default value is true)
         */
        public void setIgnore(boolean ignore) {
                this.ignore = ignore;
        }

        /**
         * If dryRun is set, the paths in question will not actually be deleted.
         *
         * @antdoc.notrequired
         * @param dryRun (Default value is false)
         */
        public void setDryRun(boolean dryRun) {
                this.dryRun = dryRun;
        }

        @Override
        public void doExecute() {
                try {
                        CleanCommand cleanCommand = git.clean();

                        if (!pathList.isEmpty()) {
                                cleanCommand.setPaths(pathList);
                        }

                        cleanCommand.setDryRun(dryRun).
                                setIgnore(ignore).
                                setCleanDirectories(cleanDirectories).
                                call();
                } catch (Exception e) {
                        throw new GitBuildException("Unexpected exception: " + e.getMessage(), e);
                }
        }

}
