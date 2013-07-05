/*
 * Copyright 2013 Fabrizio Cannizzo (https://github.com/smartrics/jgit-ant),
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
package smartrics.ant.git;

import org.eclipse.jgit.lib.ProgressMonitor;

public class SimpleProgressMonitor implements ProgressMonitor {

        private GitTask owner;
        private String name;

        public SimpleProgressMonitor(GitTask t) {
                this.owner = t;
        }

        @Override
        public void update(int sz) {
        }

        @Override
        public void start(int sz) {
                owner.log("[start] " + sz);
        }

        @Override
        public boolean isCancelled() {
                return false;
        }

        @Override
        public void endTask() {
                owner.log("[end] " + name);
        }

        @Override
        public void beginTask(String what, int sz) {
                name = what;
                owner.log("[begin] " + what + " (" + sz + ")");
        }

}
