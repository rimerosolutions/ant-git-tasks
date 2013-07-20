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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.util.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

import com.rimerosolutions.ant.git.AbstractGitRepoAwareTask;
import com.rimerosolutions.ant.git.GitBuildException;
import com.rimerosolutions.ant.git.GitUtils;

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
        private static final String REF_NAME_TEMPLATE = "* %s" + System.getProperty("line.separator");
        private static final String MISSING_REFS_TEMPLATE = "Some references could not be found '%s'.";
        private List<String> namesToCheck = new ArrayList<String>();
        private String outputFilename;

        @Override
        public String getName() {
                return TASK_NAME;
        }

        /**
         * Sets the output file that will contain the list of branches
         *
         * @antdoc.notrequired
         * @param outputFilename The output file name to use
         */
        public void setOutputFilename(String outputFilename) {
                if (GitUtils.isNullOrBlankString(outputFilename)) {
                        throw new BuildException("Invalid output file name.");
                }

                this.outputFilename = outputFilename;
        }

        /**
         * Sets the comma separated list of reference names to check in the list returned by the command
         *
         * @antdoc.notrequired
         * @param names The command separated list of references names
         */
        public void setVerifyContainNames(String names) {
                if (!GitUtils.isNullOrBlankString(names)) {
                        namesToCheck.addAll(Arrays.asList(names.split(",")));
                }
                else {
                        throw new BuildException("Invalid references names.");
                }
        }

        @Override
        protected void doExecute() {
                try {
                        List<Ref> tagRefList = git.tagList().call();
                        processReferencesAndOutput(tagRefList);
                } catch (GitAPIException e) {
                        throw new GitBuildException("Could not list tags.", e);
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

                                throw new GitBuildException(String.format(MISSING_REFS_TEMPLATE, namesCopy.toString()));
                        }
                }

                if (!GitUtils.isNullOrBlankString(outputFilename)) {
                        FileUtils fileUtils = FileUtils.getFileUtils();

                        Echo echo = new Echo();
                        echo.setProject(getProject());
                        echo.setFile(fileUtils.resolveFile(getProject().getBaseDir(), outputFilename));

                        for (int i = 0; i < refNames.size(); i++) {
                                String refName = refNames.get(i);
                                echo.addText(String.format(REF_NAME_TEMPLATE, refName));
                        }

                        echo.perform();
                }
        }
}
