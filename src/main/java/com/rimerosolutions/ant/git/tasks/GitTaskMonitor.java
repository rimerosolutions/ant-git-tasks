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

import org.eclipse.jgit.lib.ProgressMonitor;

import com.rimerosolutions.ant.git.GitTask;


/**
 * Basic Task execution monitor
 *
 * @author Yves Zoundi
 */
public class GitTaskMonitor implements ProgressMonitor {

        private final GitTask task;
        private int totalTasks;

        public GitTaskMonitor(GitTask task) {
                this.task = task;
        }

        @Override
        public void start(int totalTasks) {
                this.totalTasks = totalTasks;
                task.log(String.format("[%s] starting", task.getName()));
        }

        @Override
        public void beginTask(String title, int totalWork) {
                task.log(String.format("[%s] begin", title));
        }

        @Override
        public void update(int completed) {
                task.log(String.format("[%s] status [%d/%d]", task.getName(), completed, totalTasks));
        }

        @Override
        public boolean isCancelled() {
                return false;
        }

        @Override
        public void endTask() {
                task.log(String.format("[%s] ending", task.getName()));
        }

}
