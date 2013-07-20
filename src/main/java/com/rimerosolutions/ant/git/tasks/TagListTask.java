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
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.util.FileUtils;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.IOException;

import com.rimerosolutions.ant.git.AbstractGitRepoAwareTask;
import com.rimerosolutions.ant.git.GitUtils;
import com.rimerosolutions.ant.git.GitBuildException;

/**
 * List tags
 *
 * <p><a href="http://www.kernel.org/pub/software/scm/git/docs/git-tag.html">Git documentation about tag</a></p>
 *
 * <p><a href="http://download.eclipse.org/jgit/docs/latest/apidocs/org/eclipse/jgit/api/ListTagCommand.html">JGit ListTagCommand</a></p>
 *
 * @author Yves Zoundi
 */
public class TagListTask extends AbstractGitRepoAwareTask {

        private static final String TASK_NAME = "git-tag-list";
        private List<String> namesToCheck = new ArrayList<String>();
        private String outputFilename;

        @Override
        public String getName() {
                return TASK_NAME;
        }

        /**
         * Sets the output file that will contain the list of branches
         *
         * @param outputFilename The output file name to use
         */
        public void setOutputFilename(String outputFilename) {
                if (GitUtils.nullOrEmptyString(outputFilename)) {
                        throw new BuildException("Invalid output file name");
                }

                this.outputFilename = outputFilename;
        }

        /**
         * Sets the comma separated list of reference names to check in the list returned by the command
         *
         * @param names The command separated list of references names
         */
        public void setVerifyContainNames(String names) {
                if (!GitUtils.nullOrEmptyString(names)) {
                        namesToCheck.addAll(Arrays.asList(names.split(",")));
                }
                else {
                        throw new BuildException("Invalid references names");
                }
        }

        @Override
        protected void doExecute() {
                try {
                        List<Ref> tagRefList = git.tagList().call();
                        processReferencesAndOutput(tagRefList);
                } catch (GitAPIException e) {
                        throw new GitBuildException("Could not list tags", e);
                }
        }

        /**
         * Processes a list of references, check references names and output to a file if requested.
         *
         * @param refList The list of references to process
         */
        protected void processReferencesAndOutput(List<Ref> refList) {
                List<String> refNames = new ArrayList<String>(refList.size());

                for (Ref ref : refList) {
                        refNames.add(GitUtils.sanitizeRefName(ref.getName()));
                }

                if (!namesToCheck.isEmpty()) {
                        if (!refNames.containsAll(namesToCheck)) {
                                List<String> namesCopy = new ArrayList<String>(namesToCheck);
                                namesCopy.removeAll(refNames);

                                throw new GitBuildException(String.format("Some references could not be found '%s'", namesCopy.toString()));
                        }
                }

                if (!GitUtils.nullOrEmptyString(outputFilename)) {
                        FileUtils fileUtils = FileUtils.newFileUtils();

                        Echo echo = new Echo();
                        echo.setProject(getProject());
                        echo.setFile(fileUtils.resolveFile(getProject().getBaseDir(), outputFilename));

                        for (int i = 0; i < refNames.size(); i++) {
                                String refName = refNames.get(i);
                                echo.addText("* " + refName + System.getProperty("line.separator"));
                        }

                        echo.perform();
                }
        }
}
