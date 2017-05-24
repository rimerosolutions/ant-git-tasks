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

import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;

import com.rimerosolutions.ant.git.AbstractGitRepoAwareTask;
import com.rimerosolutions.ant.git.GitBuildException;

/**
 * Git reset.
 *
 * <pre>{@code
 * <echo file="${testLocalRepo}/test.txt" message="test"/>
 * <git:git directory="${testLocalRepo}" verbose="true" settingsRef="git.testing">
 *   <git:reset mode="hard"/>
 *   <git:uptodate failOnError="true"/>
 * </git:git>
 * }</pre>
 *
 * <p><a href="http://www.kernel.org/pub/software/scm/git/docs/git-reset.html">Git documentation about reset</a></p>
 * <p><a href="http://download.eclipse.org/jgit/docs/jgit-3.0.0.201306101825-r/apidocs/org/eclipse/jgit/api/ResetCommand.html">JGit ResetCommand</a></p>
 *
 * @author VCD
 */
public class ResetTask extends AbstractGitRepoAwareTask {
        private ResetCommand.ResetType mode = ResetType.MIXED;
        private static final String TASK_NAME = "git-reset";

        @Override
        public String getName() {
                return TASK_NAME;
        }

        /**
         * If mode is set, apply the specified mode according to Git manual.
         *
         * @antdoc.notrequired
         * @param mode the mode used to perform git reset. (Default is MIXED)
         */
        public void setMode(String mode) {
                if ("soft".equalsIgnoreCase(mode)) {
                        this.mode = ResetType.SOFT;
                } else if ("mixed".equalsIgnoreCase(mode)) {
                        this.mode = ResetType.MIXED;
                } else if ("hard".equalsIgnoreCase(mode)) {
                        this.mode = ResetType.HARD;
                } else if ("merge".equalsIgnoreCase(mode)) {
                        this.mode = ResetType.MERGE;
                } else if ("keep".equalsIgnoreCase(mode)) {
                        this.mode = ResetType.KEEP;
                } else {
                        this.mode = ResetType.MIXED;
                }
        }

        @Override
        public void doExecute() {
                try {
                        ResetCommand resetCommand = git.reset();

                        resetCommand.setMode(mode).call();
                } catch (Exception e) {
                        throw new GitBuildException("Unexpected exception: " + e.getMessage(), e);
                }
        }

}
