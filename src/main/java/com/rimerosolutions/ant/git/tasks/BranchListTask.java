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
import org.eclipse.jgit.api.ListBranchCommand;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.IOException;

import com.rimerosolutions.ant.git.AbstractGitRepoAwareTask;
import com.rimerosolutions.ant.git.GitUtils;
import com.rimerosolutions.ant.git.GitBuildException;

/**
 * List branches
 *
 * <p><a href="http://www.kernel.org/pub/software/scm/git/docs/git-branch.html">Git branch documentation</a></p>
 *
 * <p><a href="http://download.eclipse.org/jgit/docs/latest/apidocs/org/eclipse/jgit/api/ListBranchCommand.html">JGit ListBranchCommand</a></p>
 *
 * @author Yves Zoundi
 */
public class BranchListTask extends AbstractGitRepoAwareTask {

        private static final String TASK_NAME = "git-branch-list";
        private List<String> branchesToCheck = new ArrayList<String>();
        private ListBranchCommand.ListMode listMode = ListBranchCommand.ListMode.ALL;
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
         * Sets the listing mode
         *
         * @param listMode - optional: corresponds to the -r/-a options; by default, only local branches will be listed
         */
        public void setListMode(String listMode) {
                if (!GitUtils.nullOrEmptyString(listMode)) {
                        try {
                                this.listMode = ListBranchCommand.ListMode.valueOf(listMode);
                        }
                        catch (IllegalArgumentException e) {
                                ListBranchCommand.ListMode[] listModes = ListBranchCommand.ListMode.values();

                                List<String> listModeValidValues = new ArrayList<String>(listModes.length);
                                
                                for (ListBranchCommand.ListMode aListMode : listModes) {
                                        listModeValidValues.add(aListMode.name());
                                }
                                
                                throw new BuildException(String.format("Valid listMode options are: %s", listModeValidValues.toString()));
                        }
                }
        }

        public void setVerifyContainBranches(String branchNames) {
                if (!GitUtils.nullOrEmptyString(branchNames)) {
                        branchesToCheck.addAll(Arrays.asList(branchNames.split(",")));   
                }
        }
        
        @Override
        protected void doExecute() {
                try {
                        List<Ref> branchesRefList = git.branchList().setListMode(listMode).call();
                        List<String> branchNames = new ArrayList<String>(branchesRefList.size());

                        for (Ref branchRef : branchesRefList) {
                                branchNames.add(branchRef.getName());
                        }

                        if (!branchesToCheck.isEmpty()) {
                                if (!branchNames.containsAll(branchesToCheck)) {
                                        List<String> branchesCopy = new ArrayList<String>(branchesToCheck);
                                        branchesCopy.removeAll(branchNames);
                                        
                                        throw new GitBuildException(String.format("Some branches could not be found '%s'", branchesCopy.toString()));
                                }
                        }

                        if (!GitUtils.nullOrEmptyString(outputFilename)) {
                                FileUtils fileUtils = FileUtils.newFileUtils();
                                
                                Echo echo = new Echo();
                                echo.setProject(getProject());
                                echo.setFile(fileUtils.resolveFile(getProject().getBaseDir(), outputFilename));

                                for (int i = 0; i < branchNames.size(); i++) {
                                        String branchName = branchNames.get(i);
                                        echo.addText(branchName);
                                        
                                        if (i != branchNames.size() - 1) {
                                                echo.addText(System.getProperty("line.separator"));
                                        }
                                }

                                echo.perform();
                        }
                        
                } catch (GitAPIException e) {
                        throw new GitBuildException("Could not list branches", e);
                }
        }

}
