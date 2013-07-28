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
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

import com.rimerosolutions.ant.git.GitBuildException;
import com.rimerosolutions.ant.git.GitTaskUtils;

/**
 * List branches.
 *
 * <pre>{@code
 * <git:git directory="${testLocalRepo}" verbose="true">
 *    <git:branchlist outputfilename="${branchlist.file}"/>
 * </git:git>
 * }</pre>
 *
 * <p><a href="http://www.kernel.org/pub/software/scm/git/docs/git-branch.html">Git branch documentation</a></p>
 * <p><a href="http://download.eclipse.org/jgit/docs/latest/apidocs/org/eclipse/jgit/api/ListBranchCommand.html">JGit ListBranchCommand</a></p>
 *
 * @author Yves Zoundi
 */
public class BranchListTask extends TagListTask {

        private static final String TASK_NAME = "git-branch-list";
        private ListBranchCommand.ListMode listMode = ListBranchCommand.ListMode.ALL;

        @Override
        public String getName() {
                return TASK_NAME;
        }

        /**
         * Sets the listing mode
         *
         * @antdoc.notrequired
         * @param listMode - optional: corresponds to the -r/-a options; by default, only local branches will be listed
         */
        public void setListMode(String listMode) {
                if (!GitTaskUtils.isNullOrBlankString(listMode)) {
                        try {
                                this.listMode = ListBranchCommand.ListMode.valueOf(listMode);
                        }
                        catch (IllegalArgumentException e) {
                                ListBranchCommand.ListMode[] listModes = ListBranchCommand.ListMode.values();

                                List<String> listModeValidValues = new ArrayList<String>(listModes.length);

                                for (ListBranchCommand.ListMode aListMode : listModes) {
                                        listModeValidValues.add(aListMode.name());
                                }

                                String validModes = listModeValidValues.toString();

                                throw new BuildException(String.format("Valid listMode options are: %s.", validModes));
                        }
                }
        }

        @Override
        protected void doExecute() {
                try {
                        List<Ref> branchesRefList = git.branchList().setListMode(listMode).call();
                        processReferencesAndOutput(branchesRefList);
                } catch (GitAPIException e) {
                        throw new GitBuildException("Could not list branches.", e);
                }
        }

}
