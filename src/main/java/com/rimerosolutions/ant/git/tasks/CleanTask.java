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

import org.apache.tools.ant.BuildException;
import org.eclipse.jgit.api.Git;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import com.rimerosolutions.ant.git.GitBuildException;

/**
 * Git clean Ant task
 *
 * @author Yves Zoundi
 */
public class CleanTask extends AbstractGitRepoAwareTask {
        private boolean dryRun = true;
        private boolean cleanDirectories = true;
        private boolean ignore = true;
        private List<String> pathList = new ArrayList<String>();

        /**
         * If paths are set, only these paths are affected by the cleaning.
         *
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
         * @param cleanDirectories whether to clean directories too, or only files.
         */
        public void setCleanDirectories(boolean cleanDirectories) {
                this.cleanDirectories = cleanDirectories;
        }

        /**
         * If ignore is set, don't report/clean files/directories that are ignored by a .gitignore. otherwise do handle them.
         *
         * @param ignore whether to respect .gitignore or not. (Default value is true)
         */
        public void setIgnore(boolean ignore) {
                this.ignore = ignore;
        }

        /**
         * Sets whether the fetch operation should be a dry run
         *
         * @param dryRun (Default value is true)
         */
        public void setDryRun(boolean dryRun) {
                this.dryRun = dryRun;
        }

        @Override
        public void doExecute() {
                try {
                        Git.wrap(repo).
                                clean().
                                setDryRun(dryRun).
                                setIgnore(ignore).
                                setCleanDirectories(cleanDirectories).
                                call();
                } catch (Exception e) {
                        throw new GitBuildException("Unexpected exception: " + e.getMessage(), e);
                }
        }

}
